(function () {
  "use strict";

  const $ = (id) => document.getElementById(id);
  const steps = ["form", "terms", "sign", "export"];
  let current = "form";
  let lastPdfBlob = null;
  let lastPdfName = "";
  /** @type {{ status: string, text: string, lat: number|null, lon: number|null, city: string, country: string, placeSource: string }} */
  let locationInfo = emptyLocation("not_requested", "not requested");

  const BRAND_KEY = "releasecanvas_web_brand_v1";
  const DEFAULT_ACCENT = "#3A86FF";

  function emptyLocation(status, text) {
    return {
      status,
      text,
      lat: null,
      lon: null,
      city: "",
      country: "",
      placeSource: "none",
    };
  }

  function defaultBrand() {
    return {
      displayName: "",
      studioName: "",
      brandingEnabled: true,
      brandAccentHex: DEFAULT_ACCENT,
      logoDataUrl: "",
    };
  }

  function loadBrand() {
    try {
      const raw = localStorage.getItem(BRAND_KEY);
      if (!raw) return defaultBrand();
      const parsed = JSON.parse(raw);
      return {
        ...defaultBrand(),
        ...parsed,
        brandAccentHex: normalizeHex(parsed.brandAccentHex || DEFAULT_ACCENT),
      };
    } catch (_e) {
      return defaultBrand();
    }
  }

  function saveBrand(brand) {
    localStorage.setItem(BRAND_KEY, JSON.stringify(brand));
  }

  function normalizeHex(hex) {
    const h = String(hex || "").trim();
    if (/^#[0-9A-Fa-f]{6}$/.test(h)) return h.toUpperCase();
    if (/^[0-9A-Fa-f]{6}$/.test(h)) return ("#" + h).toUpperCase();
    return DEFAULT_ACCENT;
  }

  function hexToRgb(hex) {
    const h = normalizeHex(hex).slice(1);
    return {
      r: parseInt(h.slice(0, 2), 16) / 255,
      g: parseInt(h.slice(2, 4), 16) / 255,
      b: parseInt(h.slice(4, 6), 16) / 255,
    };
  }

  /** Resize image for localStorage (keep under ~700KB as data URL). */
  function fileToResizedDataUrl(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onerror = () => reject(new Error("Could not read image"));
      reader.onload = () => {
        const img = new Image();
        img.onload = () => {
          const maxW = 480;
          const maxH = 240;
          let w = img.width;
          let h = img.height;
          const scale = Math.min(1, maxW / w, maxH / h);
          w = Math.max(1, Math.round(w * scale));
          h = Math.max(1, Math.round(h * scale));
          const c = document.createElement("canvas");
          c.width = w;
          c.height = h;
          const cctx = c.getContext("2d");
          cctx.drawImage(img, 0, 0, w, h);
          // JPEG keeps localStorage smaller than PNG for photos
          resolve(c.toDataURL("image/jpeg", 0.82));
        };
        img.onerror = () => reject(new Error("Invalid image"));
        img.src = reader.result;
      };
      reader.readAsDataURL(file);
    });
  }

  function applyBrandToUi(brand) {
    $("brandDisplayName").value = brand.displayName || "";
    $("brandStudio").value = brand.studioName || "";
    $("brandEnabled").checked = brand.brandingEnabled !== false;
    const hex = normalizeHex(brand.brandAccentHex);
    $("brandAccent").value = hex;
    $("brandAccentHex").value = hex;
    const wrap = $("brandLogoPreviewWrap");
    const img = $("brandLogoPreview");
    if (brand.logoDataUrl) {
      img.src = brand.logoDataUrl;
      wrap.hidden = false;
    } else {
      img.removeAttribute("src");
      wrap.hidden = true;
    }
    // Prefill photographer if empty
    if (brand.displayName && !$("photographerName").value.trim()) {
      $("photographerName").value = brand.displayName;
    }
  }

  function readBrandFromUi() {
    return {
      displayName: $("brandDisplayName").value.trim(),
      studioName: $("brandStudio").value.trim(),
      brandingEnabled: $("brandEnabled").checked,
      brandAccentHex: normalizeHex($("brandAccentHex").value || $("brandAccent").value),
      logoDataUrl: $("brandLogoPreview").getAttribute("src") || "",
    };
  }

  function showBrandStatus(msg, isError) {
    const el = $("brandStatus");
    el.hidden = false;
    el.textContent = msg;
    el.style.color = isError ? "var(--danger)" : "var(--muted)";
  }

  // Init branding from localStorage
  let brandState = loadBrand();
  applyBrandToUi(brandState);

  $("brandAccent").addEventListener("input", () => {
    $("brandAccentHex").value = $("brandAccent").value.toUpperCase();
  });
  $("brandAccentHex").addEventListener("change", () => {
    const hex = normalizeHex($("brandAccentHex").value);
    $("brandAccentHex").value = hex;
    $("brandAccent").value = hex;
  });

  $("brandLogoFile").addEventListener("change", async () => {
    const file = $("brandLogoFile").files && $("brandLogoFile").files[0];
    if (!file) return;
    try {
      const dataUrl = await fileToResizedDataUrl(file);
      $("brandLogoPreview").src = dataUrl;
      $("brandLogoPreviewWrap").hidden = false;
      showBrandStatus("Logo ready — click Save branding to keep it.", false);
    } catch (e) {
      showBrandStatus(e.message || "Could not load logo", true);
    }
    $("brandLogoFile").value = "";
  });

  $("btnClearLogo").addEventListener("click", () => {
    $("brandLogoPreview").removeAttribute("src");
    $("brandLogoPreviewWrap").hidden = true;
    showBrandStatus("Logo cleared (save branding to persist).", false);
  });

  $("btnSaveBrand").addEventListener("click", () => {
    try {
      brandState = readBrandFromUi();
      saveBrand(brandState);
      if (brandState.displayName && !$("photographerName").value.trim()) {
        $("photographerName").value = brandState.displayName;
      } else if (brandState.displayName) {
        // Keep form photographer in sync if it still matches old default
        $("photographerName").value = brandState.displayName;
      }
      showBrandStatus("Branding saved in this browser.", false);
    } catch (e) {
      showBrandStatus(
        "Could not save (storage full?). Try a smaller logo. " + (e.message || ""),
        true,
      );
    }
  });

  // --- Signature pad ---
  const canvas = $("sigPad");
  const ctx = canvas.getContext("2d");
  let drawing = false;
  let strokes = []; // array of stroke point arrays
  let currentStroke = null;

  function resizeCanvas() {
    const ratio = window.devicePixelRatio || 1;
    const rect = canvas.getBoundingClientRect();
    const w = Math.max(280, Math.floor(rect.width));
    const h = 220;
    canvas.width = Math.floor(w * ratio);
    canvas.height = Math.floor(h * ratio);
    ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.strokeStyle = "#0d1b2a";
    ctx.lineWidth = 2.2;
    redraw();
  }

  function pointerPos(e) {
    const rect = canvas.getBoundingClientRect();
    const src = e.touches ? e.touches[0] : e;
    return { x: src.clientX - rect.left, y: src.clientY - rect.top };
  }

  function startDraw(e) {
    e.preventDefault();
    drawing = true;
    currentStroke = [pointerPos(e)];
    strokes.push(currentStroke);
  }

  function moveDraw(e) {
    if (!drawing) return;
    e.preventDefault();
    currentStroke.push(pointerPos(e));
    redraw();
  }

  function endDraw(e) {
    if (!drawing) return;
    e.preventDefault();
    drawing = false;
    currentStroke = null;
  }

  function redraw() {
    const ratio = window.devicePixelRatio || 1;
    ctx.save();
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.restore();
    ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.strokeStyle = "#0d1b2a";
    ctx.lineWidth = 2.2;
    for (const stroke of strokes) {
      if (stroke.length < 2) {
        if (stroke.length === 1) {
          ctx.beginPath();
          ctx.arc(stroke[0].x, stroke[0].y, 1.1, 0, Math.PI * 2);
          ctx.fill();
        }
        continue;
      }
      ctx.beginPath();
      ctx.moveTo(stroke[0].x, stroke[0].y);
      for (let i = 1; i < stroke.length; i++) {
        ctx.lineTo(stroke[i].x, stroke[i].y);
      }
      ctx.stroke();
    }
  }

  function clearSig() {
    strokes = [];
    redraw();
  }

  function undoSig() {
    strokes.pop();
    redraw();
  }

  function hasSignature() {
    return strokes.some((s) => s.length > 1) || strokes.some((s) => s.length === 1);
  }

  canvas.addEventListener("mousedown", startDraw);
  canvas.addEventListener("mousemove", moveDraw);
  window.addEventListener("mouseup", endDraw);
  canvas.addEventListener("touchstart", startDraw, { passive: false });
  canvas.addEventListener("touchmove", moveDraw, { passive: false });
  canvas.addEventListener("touchend", endDraw, { passive: false });
  window.addEventListener("resize", () => {
    if (current === "sign") resizeCanvas();
  });

  // --- Templates select ---
  const select = $("templateId");
  window.RC_TEMPLATES.forEach((t) => {
    const opt = document.createElement("option");
    opt.value = t.id;
    opt.textContent = t.name;
    select.appendChild(opt);
  });

  function selectedTemplate() {
    return window.RC_TEMPLATES.find((t) => t.id === select.value) || window.RC_TEMPLATES[0];
  }

  function updateTemplateDesc() {
    $("templateDesc").textContent = selectedTemplate().shortDescription;
  }
  select.addEventListener("change", updateTemplateDesc);
  updateTemplateDesc();

  // --- Navigation ---
  function showStep(name) {
    current = name;
    steps.forEach((s) => {
      const panel = $("step-" + s);
      const ind = document.querySelector('[data-step-ind="' + s + '"]');
      const active = s === name;
      panel.hidden = !active;
      panel.classList.toggle("active", active);
      if (ind) {
        ind.classList.toggle("active", active);
        const idx = steps.indexOf(s);
        const cur = steps.indexOf(name);
        ind.classList.toggle("done", idx < cur);
      }
    });
    if (name === "sign") {
      requestAnimationFrame(resizeCanvas);
    }
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function formData() {
    return {
      modelName: $("modelName").value.trim(),
      modelEmail: $("modelEmail").value.trim(),
      photographerName: $("photographerName").value.trim(),
      description: $("description").value.trim(),
      shootId: $("shootId").value.trim(),
      city: $("city").value.trim(),
      country: $("country").value.trim(),
      template: selectedTemplate(),
      releaseLang: ($("releaseLang") && $("releaseLang").value) || "en",
    };
  }

  function validateEmail(email) {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }

  function validateForm() {
    const d = formData();
    const err = $("formError");
    if (!d.modelName || !d.modelEmail || !d.photographerName || !d.description) {
      err.hidden = false;
      err.textContent = "Please fill all required fields.";
      return null;
    }
    if (!validateEmail(d.modelEmail)) {
      err.hidden = false;
      err.textContent = "Enter a valid model email.";
      return null;
    }
    err.hidden = true;
    return d;
  }

  $("btnToTerms").addEventListener("click", () => {
    const d = validateForm();
    if (!d) return;
    const body = window.RC_fillTemplate(d.template, d.modelName, d.photographerName, d.releaseLang);
    $("termsBody").classList.toggle("rtl", d.releaseLang === "fa");
    document.documentElement.lang = d.releaseLang === "fa" ? "fa" : "en";
    $("termsMeta").textContent = d.template.name + " · " + d.template.version;
    $("termsBody").textContent = body;
    showStep("terms");
  });

  $("btnBackForm").addEventListener("click", () => showStep("form"));
  $("btnToSign").addEventListener("click", () => showStep("sign"));
  $("btnBackTerms").addEventListener("click", () => showStep("terms"));
  $("btnClearSig").addEventListener("click", clearSig);
  $("btnUndoSig").addEventListener("click", undoSig);

  $("btnToExport").addEventListener("click", () => {
    const err = $("signError");
    if (!hasSignature()) {
      err.hidden = false;
      err.textContent = "Please provide a signature before continuing.";
      return;
    }
    if (!$("attestation").checked) {
      err.hidden = false;
      err.textContent = "Confirm age of majority / authority before continuing.";
      return;
    }
    err.hidden = true;
    updateLocationLabel();
    showStep("export");
  });

  $("btnBackSign").addEventListener("click", () => showStep("sign"));

  function updateLocationLabel() {
    $("locationStatus").textContent = "Location: " + locationInfo.text;
  }

  /**
   * Best-effort reverse geocode via OpenStreetMap Nominatim (public API).
   * Requires network. Manual city/country fields override results on export.
   * Usage policy: identify the app; keep request rate low (user-initiated only).
   */
  async function reverseGeocode(lat, lon) {
    const url =
      "https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=" +
      encodeURIComponent(String(lat)) +
      "&lon=" +
      encodeURIComponent(String(lon)) +
      "&zoom=10&addressdetails=1";
    const res = await fetch(url, {
      headers: {
        Accept: "application/json",
        // Nominatim asks for a valid identifying User-Agent / app name.
        // Browsers may override User-Agent; referrer + this product name still help.
      },
    });
    if (!res.ok) throw new Error("geocode HTTP " + res.status);
    const data = await res.json();
    const addr = data.address || {};
    const city =
      addr.city ||
      addr.town ||
      addr.village ||
      addr.municipality ||
      addr.county ||
      addr.suburb ||
      "";
    const country = addr.country || "";
    return { city: String(city).trim(), country: String(country).trim() };
  }

  function applyGeocodeToFormIfEmpty(city, country) {
    if (city && !$("city").value.trim()) $("city").value = city;
    if (country && !$("country").value.trim()) $("country").value = country;
  }

  $("btnLocation").addEventListener("click", () => {
    if (!navigator.geolocation) {
      locationInfo = emptyLocation("unavailable", "unavailable (not supported)");
      updateLocationLabel();
      return;
    }
    locationInfo = emptyLocation("acquiring", "acquiring GPS…");
    updateLocationLabel();
    $("btnLocation").disabled = true;
    navigator.geolocation.getCurrentPosition(
      async (pos) => {
        const lat = pos.coords.latitude;
        const lon = pos.coords.longitude;
        const acc = Math.round(pos.coords.accuracy || 0);
        const gpsText = lat.toFixed(5) + ", " + lon.toFixed(5) + " (±" + acc + " m)";
        locationInfo = {
          status: "available",
          text: gpsText + " · reverse geocoding…",
          lat,
          lon,
          city: "",
          country: "",
          placeSource: "none",
        };
        updateLocationLabel();
        try {
          const place = await reverseGeocode(lat, lon);
          locationInfo.city = place.city;
          locationInfo.country = place.country;
          if (place.city || place.country) {
            locationInfo.placeSource = "reverse_geocode";
            locationInfo.text =
              gpsText +
              " · " +
              [place.city, place.country].filter(Boolean).join(", ") +
              " (reverse geocode)";
            applyGeocodeToFormIfEmpty(place.city, place.country);
          } else {
            locationInfo.text = gpsText + " · place name unavailable";
          }
        } catch (_e) {
          locationInfo.text = gpsText + " · reverse geocode failed (offline or blocked)";
        }
        updateLocationLabel();
        $("btnLocation").disabled = false;
      },
      (err) => {
        locationInfo = emptyLocation(
          "denied",
          "unavailable (" + (err.message || "permission denied") + ")",
        );
        updateLocationLabel();
        $("btnLocation").disabled = false;
      },
      { enableHighAccuracy: true, timeout: 12000, maximumAge: 0 },
    );
  });

  function sanitizeFilename(name) {
    return name.replace(/[^\w.\-]+/g, "_").slice(0, 40) || "model";
  }

  function wrapText(text, font, maxWidth) {
    const words = text.split(/\s+/);
    const lines = [];
    let line = "";
    const canvasMeasure = document.createElement("canvas").getContext("2d");
    canvasMeasure.font = font;
    for (const word of words) {
      const test = line ? line + " " + word : word;
      if (canvasMeasure.measureText(test).width > maxWidth && line) {
        lines.push(line);
        line = word;
      } else {
        line = test;
      }
    }
    if (line) lines.push(line);
    return lines;
  }

  async function buildPdf() {
    const d = formData();
    // Prefer latest UI branding (even if user forgot Save); fall back to stored.
    const brand = (() => {
      try {
        return readBrandFromUi();
      } catch (_e) {
        return brandState;
      }
    })();
    const accentParts = hexToRgb(brand.brandAccentHex);
    const { PDFDocument, rgb, StandardFonts } = PDFLib;
    const accent = rgb(accentParts.r, accentParts.g, accentParts.b);
    const pdfDoc = await PDFDocument.create();
    const isFa = d.releaseLang === "fa";
    let font;
    let fontBold;
    if (isFa) {
      const fontBytes = await fetch("./fonts/Vazirmatn-Regular.ttf").then((r) => {
        if (!r.ok) throw new Error("Vazirmatn font missing");
        return r.arrayBuffer();
      });
      font = await pdfDoc.embedFont(fontBytes, { subset: true });
      fontBold = font; // single weight
    } else {
      font = await pdfDoc.embedFont(StandardFonts.Helvetica);
      fontBold = await pdfDoc.embedFont(StandardFonts.HelveticaBold);
    }
    const pageSize = [612, 792]; // US Letter
    const margin = 48;
    const maxW = pageSize[0] - margin * 2;
    let page = pdfDoc.addPage(pageSize);
    let y = pageSize[1] - margin;

    function newPage() {
      page = pdfDoc.addPage(pageSize);
      y = pageSize[1] - margin;
      drawLine(10, { size: 9, color: rgb(0.4, 0.4, 0.4) }, "ReleaseCanvas Web · continued");
      y -= 12;
    }

    function ensure(h) {
      if (y - h < margin) newPage();
    }

    function drawLine(size, style, text) {
      const f = style.bold ? fontBold : font;
      const lines = wrapText(text, (style.bold ? "bold " : "") + size + "px Helvetica", maxW);
      const lineH = size + 4;
      for (const ln of lines) {
        ensure(lineH);
        page.drawText(ln, {
          x: margin,
          y: y - size,
          size,
          font: f,
          color: style.color || rgb(0.05, 0.08, 0.12),
          maxWidth: maxW,
        });
        y -= lineH;
      }
    }

    const terms = window.RC_fillTemplate(d.template, d.modelName, d.photographerName, d.releaseLang);
    const signedAt = new Date().toISOString();

    if (brand.brandingEnabled) {
      if (brand.logoDataUrl) {
        try {
          const isPng = brand.logoDataUrl.startsWith("data:image/png");
          const logoBytes = await fetch(brand.logoDataUrl).then((r) => r.arrayBuffer());
          const logoImg = isPng
            ? await pdfDoc.embedPng(logoBytes)
            : await pdfDoc.embedJpg(logoBytes);
          const maxLogoW = 120;
          const maxLogoH = 48;
          const scale = Math.min(maxLogoW / logoImg.width, maxLogoH / logoImg.height, 1);
          const lw = logoImg.width * scale;
          const lh = logoImg.height * scale;
          ensure(lh + 12);
          page.drawImage(logoImg, { x: margin, y: y - lh, width: lw, height: lh });
          y -= lh + 8;
        } catch (_e) {
          // skip bad logo
        }
      }
      const studioLine = brand.studioName || brand.displayName;
      if (studioLine) {
        drawLine(13, { bold: true, color: accent }, studioLine);
      }
      ensure(8);
      page.drawLine({
        start: { x: margin, y: y },
        end: { x: pageSize[0] - margin, y: y },
        thickness: 2,
        color: accent,
      });
      y -= 14;
    }

    drawLine(18, { bold: true }, "Model Release Agreement");
    y -= 4;
    drawLine(9, { color: rgb(0.35, 0.35, 0.4) }, "Generated by ReleaseCanvas Web · " + d.template.name + " · " + d.template.version);
    y -= 10;
    drawLine(12, { bold: true, color: accent }, "Parties");
    drawLine(11, {}, "Model: " + d.modelName);
    drawLine(11, {}, "Model email: " + d.modelEmail);
    drawLine(11, {}, "Photographer: " + d.photographerName);
    if (brand.brandingEnabled && brand.studioName) {
      drawLine(11, {}, "Studio: " + brand.studioName);
    }
    y -= 8;
    drawLine(12, { bold: true, color: accent }, "Shoot details");
    if (d.shootId) drawLine(11, {}, "Shoot ID: " + d.shootId);
    drawLine(11, {}, "Description: " + d.description);
    y -= 8;
    drawLine(12, { bold: true, color: accent }, "Release terms");
    drawLine(10, {}, terms);
    y -= 10;
    drawLine(12, { bold: true, color: accent }, "Signature");

    // Embed signature PNG
    const sigDataUrl = canvas.toDataURL("image/png");
    const sigBytes = await fetch(sigDataUrl).then((r) => r.arrayBuffer());
    const sigImg = await pdfDoc.embedPng(sigBytes);
    const sigW = 220;
    const sigH = (sigImg.height / sigImg.width) * sigW;
    ensure(sigH + 20);
    page.drawRectangle({
      x: margin,
      y: y - sigH - 4,
      width: sigW,
      height: sigH,
      borderColor: rgb(0.75, 0.75, 0.75),
      borderWidth: 1,
    });
    page.drawImage(sigImg, { x: margin, y: y - sigH - 4, width: sigW, height: sigH });
    y -= sigH + 16;

    drawLine(12, { bold: true, color: accent }, "Signing metadata");
    drawLine(11, {}, "Signed at (UTC): " + signedAt);
    // Manual form fields override reverse-geocoded place (same idea as Android).
    const placeCity = d.city || locationInfo.city || "";
    const placeCountry = d.country || locationInfo.country || "";
    const placeSource = d.city || d.country
      ? "manual"
      : locationInfo.placeSource === "reverse_geocode"
        ? "reverse geocode"
        : "";
    if (placeCity || placeCountry) {
      const placeLine = [placeCity, placeCountry].filter(Boolean).join(", ");
      drawLine(
        11,
        {},
        "Place: " + placeLine + (placeSource ? " (" + placeSource + ")" : ""),
      );
    }
    if (locationInfo.lat != null) {
      drawLine(11, {}, "GPS: " + locationInfo.lat.toFixed(6) + ", " + locationInfo.lon.toFixed(6));
    } else {
      drawLine(11, {}, "GPS: " + locationInfo.text);
    }
    drawLine(
      11,
      {},
      "Attestation: Model confirmed legal age of majority (or parent/guardian with authority) and accepted the terms.",
    );
    y -= 8;
    drawLine(
      9,
      { color: rgb(0.4, 0.4, 0.4) },
      "This electronic signature and metadata stamp are intended for record-keeping. Not a substitute for legal advice. Generated in-browser by ReleaseCanvas Web.",
    );
    if (brand.brandingEnabled) {
      const prepared = (brand.studioName || brand.displayName || d.photographerName).trim();
      if (prepared) {
        y -= 6;
        drawLine(9, { color: rgb(0.35, 0.35, 0.4) }, "Prepared by " + prepared + " · ReleaseCanvas Web");
      }
    }

    const bytes = await pdfDoc.save();
    return {
      blob: new Blob([bytes], { type: "application/pdf" }),
      name: "ReleaseCanvas_" + sanitizeFilename(d.modelName) + "_" + signedAt.slice(0, 10) + ".pdf",
    };
  }

  function downloadBlob(blob, name) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = name;
    a.rel = "noopener";
    document.body.appendChild(a);
    a.click();
    a.remove();
    setTimeout(() => URL.revokeObjectURL(url), 30_000);
  }

  $("btnCreatePdf").addEventListener("click", async () => {
    const err = $("exportError");
    const busy = $("exportBusy");
    err.hidden = true;
    busy.hidden = false;
    $("btnCreatePdf").disabled = true;
    try {
      if (!window.PDFLib) throw new Error("PDF library failed to load (check network).");
      const { blob, name } = await buildPdf();
      lastPdfBlob = blob;
      lastPdfName = name;
      downloadBlob(blob, name);
      const shareBtn = $("btnShare");
      // Web Share API with files (iOS Safari supports in many cases)
      const file = new File([blob], name, { type: "application/pdf" });
      if (navigator.canShare && navigator.canShare({ files: [file] })) {
        shareBtn.hidden = false;
      } else {
        shareBtn.hidden = true;
      }
    } catch (e) {
      err.hidden = false;
      err.textContent = "Could not create PDF: " + (e.message || String(e));
    } finally {
      busy.hidden = true;
      $("btnCreatePdf").disabled = false;
    }
  });

  $("btnShare").addEventListener("click", async () => {
    if (!lastPdfBlob) return;
    try {
      const file = new File([lastPdfBlob], lastPdfName, { type: "application/pdf" });
      await navigator.share({
        files: [file],
        title: lastPdfName,
        text: "Signed model release PDF from ReleaseCanvas Web",
      });
    } catch (e) {
      if (e && e.name === "AbortError") return;
      $("exportError").hidden = false;
      $("exportError").textContent = "Share failed — use the downloaded file instead.";
    }
  });

  $("btnNew").addEventListener("click", () => {
    lastPdfBlob = null;
    lastPdfName = "";
    $("btnShare").hidden = true;
    clearSig();
    $("attestation").checked = false;
    locationInfo = emptyLocation("not_requested", "not requested");
    $("btnLocation").disabled = false;
    updateLocationLabel();
    showStep("form");
  });
})();

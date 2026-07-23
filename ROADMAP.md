# ReleaseCanvas Roadmap

Living product plan. **GitHub [milestones](https://github.com/ArianAr/ReleaseCanvas/milestones) and [issues](https://github.com/ArianAr/ReleaseCanvas/issues) are the source of truth for work in progress.** This file summarizes themes and status for humans.

> New roadmap items should be agreed with the maintainer before being added here.

---

## Shipped

### v1.0 — Core product
- [x] Form → signature → review → PDF export (`PdfDocument` + MediaStore)
- [x] GPS + UTC signing metadata; optional city/country (manual / reverse geocode)
- [x] Local export history; photographer name preference
- [x] Community docs (README, CONTRIBUTING, CoC, SECURITY)
- [x] Issue/PR templates; main branch ruleset (public repo)

### v1.1 — Form & templates
- [x] Optional shoot metadata (Shoot ID, contacts, client, notes, location name)
- [x] Multi-template release picker (generic, 500px-style unofficial, stock RF-style, editorial, social/web)
- [x] Signature/export error UX; success path polish
- [x] README mock screenshots; CHANGELOG

### v1.2 — Product depth
- [x] About screen (version, legal/privacy)
- [x] Terms preview before signing
- [x] History remove / clear list (PDFs retained on disk)
- [x] Age/authority attestation checkbox
- [x] Share / Email PDF with subject & body
- [x] Multi-page PDF when content overflows
- [x] Private vulnerability reporting enabled

**Current app version:** see `versionName` in `app/build.gradle.kts` (tagged releases on GitHub).

---

## Next

### v1.3 — Scale
| Issue | Theme |
|-------|--------|
| [#20](https://github.com/ArianAr/ReleaseCanvas/issues/20) | Batch multi-model releases |
| [#21](https://github.com/ArianAr/ReleaseCanvas/issues/21) | Cloud backup behind a storage interface |

### v1.4 — UX, branding & localization
Agreed with maintainer:

| Issue | Theme |
|-------|--------|
| [#30](https://github.com/ArianAr/ReleaseCanvas/issues/30) | Photographer profile defaults (studio, email/phone, logo; prefill) |
| [#31](https://github.com/ArianAr/ReleaseCanvas/issues/31) | PDF branding / watermark (logo, accent, studio footer) |
| [#32](https://github.com/ArianAr/ReleaseCanvas/issues/32) | Onboarding / first-run tips |
| [#33](https://github.com/ArianAr/ReleaseCanvas/issues/33) | Accessibility pass (TalkBack, targets, contrast) |
| [#34](https://github.com/ArianAr/ReleaseCanvas/issues/34) | Custom template import (offline `.txt` / `.md`) |
| [#36](https://github.com/ArianAr/ReleaseCanvas/issues/36) | Editable multi-jurisdiction legal templates |
| [#37](https://github.com/ArianAr/ReleaseCanvas/issues/37) | **Full multi-language app UI** + release/PDF text: English, Spanish, French, Italian, German, Persian (**RTL**) |

Related notes:
- **#36** complements **#34** (import) and built-in template picker; built-ins stay as fork-on-edit baselines where possible.
- **#37** is app-wide localization, not PDF-only:
  - All screens/strings (Home → form → terms → sign → review → success → about/history)
  - In-app language picker (+ optional follow-system)
  - Built-in template bodies + PDF in the selected language
  - Persian **RTL** layout; missing strings fall back to English
- Jurisdiction packs remain **unofficial samples**, not attorney-certified law (see Non-goals).

---

## Later / ideas (not committed)

_No unapproved items. Propose additions before adding to this file or filing issues._

---

## Non-goals (for now)

- iOS / KMP
- Cryptographic / blockchain “notary” timestamps
- Shipping **attorney-certified** jurisdiction packs out of the box (editable templates ≠ legal certification)
- GitHub Actions CI (explicitly deferred)

---

## How we ship

1. Issue + milestone  
2. Feature PR → review/tests → merge  
3. After a **minor** or **major** set: version bump, CHANGELOG, **git tag + GitHub Release**  

See [CHANGELOG.md](CHANGELOG.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

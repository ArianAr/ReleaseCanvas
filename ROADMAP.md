# ReleaseCanvas Roadmap

Living product plan. **GitHub [milestones](https://github.com/ArianAr/ReleaseCanvas/milestones) and [issues](https://github.com/ArianAr/ReleaseCanvas/issues) are the source of truth for open work.** This file is a human-readable summary.

> New roadmap items should be agreed with the maintainer before being added here.

**Current tagged release:** **[v1.8.0](https://github.com/ArianAr/ReleaseCanvas/releases/tag/v1.8.0)** (`versionName` **1.8.0** / `versionCode` **11**). Full history: [CHANGELOG.md](CHANGELOG.md).

> **AI-assisted development:** most of the project code and docs have been drafted with AI assistance. **Everything is reviewed, tested, and approved by humans** before it lands on `main`. Maintainers remain accountable for product decisions and quality.

---

## Platforms

| Surface | Status |
|---------|--------|
| **Android app** | Full product (API 29+) — preferred |
| **Web companion** | Sign & export (+ branding, FA via Vazirmatn); no history/batch; [GitHub Pages](https://arianar.github.io/ReleaseCanvas/) |
| **Native iOS** | **Not available** — no Apple device for testing |

---

## Next

### [Future / uncommitted](https://github.com/ArianAr/ReleaseCanvas/milestone/9)

_No scheduled work._ Propose via issue and agree with the maintainer before promoting items here.

Examples of **non-goals** (do not schedule without a new decision):

- Native iOS / KMP rewrite  
- Automatic cloud sync or multi-vendor storage SDKs  
- Official third-party forms without a redistribution license  
- Private-export mode (app-only storage) as a full product — only documented as future idea under privacy work  

---

## Shipped (by product version)

Milestones on GitHub mirror these themes: [all milestones](https://github.com/ArianAr/ReleaseCanvas/milestones?state=closed).

### [v1.8 Engineering hardening](https://github.com/ArianAr/ReleaseCanvas/milestone/8) (tagged **1.8.0**)

Senior review follow-up — issues **#69–#84** (all closed).

**P0 — Privacy**
- [x] [#69](https://github.com/ArianAr/ReleaseCanvas/issues/69) Shared `Documents/ReleaseCanvas` privacy docs (in-app + README)
- [x] [#70](https://github.com/ArianAr/ReleaseCanvas/issues/70) Auto Backup / data-extraction rules exclude DataStore + logo
- [x] [#71](https://github.com/ArianAr/ReleaseCanvas/issues/71) GPS-in-PDF **opt-in** (default off)

**P1 — Quality & security hygiene**
- [x] [#72](https://github.com/ArianAr/ReleaseCanvas/issues/72) Localize dynamic errors / review labels
- [x] [#73](https://github.com/ArianAr/ReleaseCanvas/issues/73) Web: vendored pdf-lib + SRI; Nominatim privacy note
- [x] [#74](https://github.com/ArianAr/ReleaseCanvas/issues/74) Web: **Vazirmatn** for Persian UI + PDF
- [x] [#75](https://github.com/ArianAr/ReleaseCanvas/issues/75) Logo import size/type caps
- [x] [#76](https://github.com/ArianAr/ReleaseCanvas/issues/76) PR CI (`testDebugUnitTest` + `assembleDebug`)
- [x] [#77](https://github.com/ArianAr/ReleaseCanvas/issues/77) Unit tests (export guards, locale, form validation)
- [x] [#78](https://github.com/ArianAr/ReleaseCanvas/issues/78) Frozen GPS stamp for preview + export

**P2 — Maintainability**
- [x] [#79](https://github.com/ArianAr/ReleaseCanvas/issues/79) `LogoImporter` collaborator (ViewModel slim-down start)
- [x] [#80](https://github.com/ArianAr/ReleaseCanvas/issues/80) R8 minify + resource shrink for release
- [x] [#81](https://github.com/ArianAr/ReleaseCanvas/issues/81) Remove unused FileProvider
- [x] [#82](https://github.com/ArianAr/ReleaseCanvas/issues/82) Shared `release_templates.json` (Android assets + web/data)
- [x] [#83](https://github.com/ArianAr/ReleaseCanvas/issues/83) SECURITY.md for 1.x
- [x] [#84](https://github.com/ArianAr/ReleaseCanvas/issues/84) Soft-handle missing history PDFs

PRs: [#85](https://github.com/ArianAr/ReleaseCanvas/pull/85)–[#88](https://github.com/ArianAr/ReleaseCanvas/pull/88).

### [v1.7 Web companion & release pipeline](https://github.com/ArianAr/ReleaseCanvas/milestone/7) (tagged 1.7.0–1.7.1)

- [x] Static **web** app: form → terms → signature → PDF; reverse geocode; localStorage branding  
- [x] **GitHub Pages** deploy for `web/`  
- [x] **GHA signed release APK** on `v*` tags  
- [x] Free local release keystore wiring  
- [x] UI language switcher + onboarding cold-start fixes  
- [x] Formal Persian release wording  
- [x] Device screenshots in README  
- [x] UX polish: status bar, header spacing, official-forms FAQ ([#48](https://github.com/ArianAr/ReleaseCanvas/issues/48)–[#50](https://github.com/ArianAr/ReleaseCanvas/issues/50))

### [v1.6 Scale](https://github.com/ArianAr/ReleaseCanvas/milestone/5) (tagged 1.6.0–1.6.1)

- [x] [#20](https://github.com/ArianAr/ReleaseCanvas/issues/20) Batch multi-model releases  
- [x] [#21](https://github.com/ArianAr/ReleaseCanvas/issues/21) Share / save to cloud via system share sheet (no auto-sync)

### [v1.5 Localization](https://github.com/ArianAr/ReleaseCanvas/milestone/6) (tagged 1.5.0; related items in 1.3–1.4)

- [x] [#37](https://github.com/ArianAr/ReleaseCanvas/issues/37) Independent **app UI language** × **release wording language** (en, es, fr, it, de, fa)  
- UI: optional Follow system; Persian **RTL** when UI=`fa`  
- Release language drives terms preview + PDF legal body/labels  

### v1.4.0 — Custom & editable templates

- [x] [#34](https://github.com/ArianAr/ReleaseCanvas/issues/34) Custom template import  
- [x] [#36](https://github.com/ArianAr/ReleaseCanvas/issues/36) Editable custom templates + jurisdiction labels  

### v1.3.0 — Profile, branding & onboarding

- [x] [#30](https://github.com/ArianAr/ReleaseCanvas/issues/30) Photographer profile defaults  
- [x] [#31](https://github.com/ArianAr/ReleaseCanvas/issues/31) PDF branding  
- [x] [#32](https://github.com/ArianAr/ReleaseCanvas/issues/32) First-run onboarding  
- [x] [#33](https://github.com/ArianAr/ReleaseCanvas/issues/33) Accessibility basics  

### [v1.2 Product depth](https://github.com/ArianAr/ReleaseCanvas/milestone/4)

- [x] About, terms preview, history, attestation, share/email, multi-page PDF, private vuln reporting  

### v1.1 / [v0.3 Form & templates](https://github.com/ArianAr/ReleaseCanvas/milestone/2)

- [x] Optional shoot metadata; multi-template picker; success-path polish  

### v1.0 Core / [v0.2 UX](https://github.com/ArianAr/ReleaseCanvas/milestone/1)

- [x] Form → signature → review → PDF (MediaStore); GPS + UTC; history; community docs  

---

## Non-goals (for now)

- **Native iOS app** until Apple hardware is available for testing (web companion fills the gap)  
- Kotlin Multiplatform dual-UI rewrite  
- Cryptographic / blockchain “notary” timestamps  
- Shipping **attorney-certified** jurisdiction packs out of the box  
- Full Android UI test matrix in CI (unit tests + assemble are enough for now)  
- **Automatic cloud sync** or multi-vendor storage SDKs — share sheet / download only  
- Embedding **official** third-party release forms without a redistribution license  
- Web history or batch (Android-only)  
- Web branding cross-device sync (localStorage only)  

---

## How we ship

1. Issue (+ milestone when themed)  
2. Feature branch → PR → **review comment** → merge to `main`  
3. After a coherent set: bump `versionName` / `versionCode`, CHANGELOG, **git tag + GitHub Release** (GHA attaches signed APK)  

See [CHANGELOG.md](CHANGELOG.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

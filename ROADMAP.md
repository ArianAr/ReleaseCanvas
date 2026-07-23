# ReleaseCanvas Roadmap

Living product plan. **GitHub [milestones](https://github.com/ArianAr/ReleaseCanvas/milestones) and [issues](https://github.com/ArianAr/ReleaseCanvas/issues) are the source of truth for open work.** This file is a human-readable summary.

> New roadmap items should be agreed with the maintainer before being added here.

**Current release:** **[v1.7.1](https://github.com/ArianAr/ReleaseCanvas/releases/tag/v1.7.1)** (`versionName` / `versionCode` in `app/build.gradle.kts`). Full history: [CHANGELOG.md](CHANGELOG.md).

> **AI-assisted development:** most of the project code and docs have been drafted with AI assistance. **Everything is reviewed, tested, and approved by humans** before it lands on `main`. Maintainers remain accountable for product decisions and quality.

---

## Next

_No committed next items._ Open an issue and agree scope before adding work here.

---

## Shipped

### v1.7.1 — Locale, onboarding & Persian templates
- [x] UI language switcher (AppCompat)
- [x] Onboarding persistence fix
- [x] Formal Persian release wording
- [x] Device screenshots in README

### v1.7.0 — Web companion & release pipeline
- [x] Static **web** app (`web/`): form → terms → signature → PDF; reverse geocode; localStorage branding  
- [x] **GitHub Pages** deploy for `web/`  
- [x] **GHA signed release APK** on `v*` tags (secrets-only keystore)  
- [x] Platform messaging: Android full product; web fills iOS/desktop gap; native iOS deferred  

### v1.6.1 — Share / save to cloud
- [x] [#21](https://github.com/ArianAr/ReleaseCanvas/issues/21) Optional off-device PDF copies via **system share sheet** (Drive, Dropbox, email, …) from success and history  
- Local MediaStore export remains source of truth; **no automatic sync**

### v1.6.0 — Batch multi-model
- [x] [#20](https://github.com/ArianAr/ReleaseCanvas/issues/20) Shared shoot details + model roster → sign/export one PDF per model  
- Progress banner; batch success summary (**Next model** / **End batch**)

### v1.5.0 — Localization
- [x] [#37](https://github.com/ArianAr/ReleaseCanvas/issues/37) Independent **app UI language** × **release wording language** (en, es, fr, it, de, fa)  
- UI: optional Follow system; Persian **RTL** when UI=`fa`  
- Release language drives terms preview + PDF legal body/labels; RTL when release=`fa`  
- Fallbacks to English **per channel**

### v1.4.0 — Custom & editable templates
- [x] [#34](https://github.com/ArianAr/ReleaseCanvas/issues/34) Custom template import (`.txt` / `.md`, `{{MODEL}}` / `{{PHOTOGRAPHER}}`)  
- [x] [#36](https://github.com/ArianAr/ReleaseCanvas/issues/36) Editable custom templates + jurisdiction labels; fork built-ins as editable copies  
- Built-ins remain unofficial samples (not attorney-certified)

### v1.3.0 — Profile, branding & onboarding
- [x] [#30](https://github.com/ArianAr/ReleaseCanvas/issues/30) Photographer profile defaults  
- [x] [#31](https://github.com/ArianAr/ReleaseCanvas/issues/31) PDF branding (logo, studio, accent)  
- [x] [#32](https://github.com/ArianAr/ReleaseCanvas/issues/32) First-run onboarding (reopen from About)  
- [x] [#33](https://github.com/ArianAr/ReleaseCanvas/issues/33) Accessibility basics (further a11y welcome later)

### v1.2.0 — Product depth
- [x] About (version, legal/privacy)  
- [x] Terms preview before signature  
- [x] History remove / clear list (PDFs retained on disk)  
- [x] Age/authority attestation before export  
- [x] Share / Email PDF  
- [x] Multi-page PDF when content overflows  
- [x] Private vulnerability reporting

### v1.1.0 — Form & templates
- [x] Optional shoot metadata (Shoot ID, contacts, client, notes, location name)  
- [x] Multi-template picker (generic, 500px-style unofficial, stock RF-style, editorial, social/web)  
- [x] Signature/export error UX; success path polish  
- [x] README mock screenshots; CHANGELOG

### v1.0 — Core product
- [x] Form → signature → review → PDF export (`PdfDocument` + MediaStore → `Documents/ReleaseCanvas`)  
- [x] GPS + UTC signing metadata; optional city/country (manual / reverse geocode)  
- [x] Local export history; photographer name preference  
- [x] Community docs (README, CONTRIBUTING, CoC, SECURITY)  
- [x] Issue/PR templates; main branch ruleset (public repo)

### Other polish (merged on main, folded into recent releases)
- [x] [#48](https://github.com/ArianAr/ReleaseCanvas/issues/48) Status bar on notched devices  
- [x] [#49](https://github.com/ArianAr/ReleaseCanvas/issues/49) Content spacing under top app bars  
- [x] [#50](https://github.com/ArianAr/ReleaseCanvas/issues/50) FAQ: why official platform forms are not bundled  

---

## Later / ideas (not committed)

_No unapproved items. Propose additions before filing issues or editing this section._

---

## Non-goals (for now)

- **Native iOS app** until we have access to Apple hardware for testing (web companion fills the gap)  
- Kotlin Multiplatform dual-UI rewrite  
- Cryptographic / blockchain “notary” timestamps  
- Shipping **attorney-certified** jurisdiction packs out of the box (editable templates ≠ legal certification)  
- Full Android unit/UI CI matrix (Pages deploy + **signed release APK on tags** are allowed)  
- **Automatic cloud sync** or multi-vendor storage SDKs — off-device copies use the **system share sheet** (Android) or browser download/share (web)  
- Embedding **official** third-party release forms (e.g. 500px) without an explicit redistribution license  
- Web **history** or batch (Android-only for now); web branding is localStorage-only (no cross-device sync)

---

## How we ship

1. Issue (+ milestone if useful)  
2. Feature branch → PR → review/tests → merge to `main`  
3. After a **minor** or **major** set: bump `versionName` / `versionCode`, update CHANGELOG, **git tag + GitHub Release**  

See [CHANGELOG.md](CHANGELOG.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

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

### v1.6 — Scale
| Issue | Theme |
|-------|--------|
| [#20](https://github.com/ArianAr/ReleaseCanvas/issues/20) | Batch multi-model releases — **shipped in app 1.6.0** |
| [#21](https://github.com/ArianAr/ReleaseCanvas/issues/21) | Optional **share / save to cloud** via system share sheet — **shipped in app 1.6.1** (no multi-backend storage; no automatic sync) |

### v1.3 — Profile, branding & onboarding (shipped in app 1.3.0)
- [x] [#30](https://github.com/ArianAr/ReleaseCanvas/issues/30) Photographer profile defaults
- [x] [#31](https://github.com/ArianAr/ReleaseCanvas/issues/31) PDF branding / watermark
- [x] [#32](https://github.com/ArianAr/ReleaseCanvas/issues/32) Onboarding / first-run tips
- [x] [#33](https://github.com/ArianAr/ReleaseCanvas/issues/33) Accessibility basics (more a11y work welcome later)

### v1.4 — Templates (shipped in app 1.4.0)
- [x] [#34](https://github.com/ArianAr/ReleaseCanvas/issues/34) Custom template import
- [x] [#36](https://github.com/ArianAr/ReleaseCanvas/issues/36) Editable multi-jurisdiction templates

### v1.5 — Localization (shipped in app 1.5.0)
- [x] [#37](https://github.com/ArianAr/ReleaseCanvas/issues/37) Two language pickers: app UI × release/template language (en, es, fr, it, de, fa; Persian RTL)

Related notes:
- **#36** complements **#34** (import) and built-in template picker; built-ins stay as fork-on-edit baselines where possible.
- **#37** splits localization into **independent** options:
  - **App UI language** — buttons, labels, errors, About, history, onboarding (optional follow-system)
  - **Release / template language** — terms preview body + PDF legal text only
  - Example: UI in **German**, release template in **English** (any combination allowed)
  - Persian **RTL** applies to UI when UI=`fa`, and to PDF/terms when release language=`fa`
  - Missing strings/templates fall back to English **per channel**
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
- **Automatic cloud sync** or multi-vendor storage SDKs (Drive/Dropbox/etc.) — optional off-device copies use the system share sheet only

---

## How we ship

1. Issue + milestone  
2. Feature PR → review/tests → merge  
3. After a **minor** or **major** set: version bump, CHANGELOG, **git tag + GitHub Release**  

See [CHANGELOG.md](CHANGELOG.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

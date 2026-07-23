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

## In progress / next

### v1.3 — Scale
Tracked issues:

| Issue | Theme |
|-------|--------|
| [#20](https://github.com/ArianAr/ReleaseCanvas/issues/20) | Batch multi-model releases |
| [#21](https://github.com/ArianAr/ReleaseCanvas/issues/21) | Cloud backup behind a storage interface |

Related ideas under this theme (not yet issued unless agreed):

- Editable / multi-jurisdiction legal templates (UI editor or import)
- Photographer default profile (studio name, logo watermark on PDF)

---

## Later / ideas (not committed)

_Items below are placeholders only until the maintainer approves them for this file or as GitHub issues._

_(empty — propose additions in discussion or PR review)_

---

## Non-goals (for now)

- iOS / KMP
- Cryptographic / blockchain “notary” timestamps
- Shipping attorney-certified jurisdiction packs out of the box
- GitHub Actions CI (explicitly deferred)

---

## How we ship

1. Issue + milestone  
2. Feature PR → review/tests → merge  
3. After a **minor** or **major** set: version bump, CHANGELOG, **git tag + GitHub Release**  

See [CHANGELOG.md](CHANGELOG.md) and [CONTRIBUTING.md](CONTRIBUTING.md).

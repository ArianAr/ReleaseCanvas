# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
for `versionName` (with integer `versionCode` monotonically increasing).

## [Unreleased]

### Added
- **Web companion** (`web/`): browser sign & PDF export (no history); GitHub Pages deploy workflow
- Platform docs: Android full product; web for iOS/desktop gap; native iOS deferred (no Apple test device)
- Web: best-effort **reverse geocode** after GPS (OpenStreetMap Nominatim); manual city/country still override
- Web: **studio branding** (name, studio, accent, logo) persisted in **localStorage**; optional PDF header branding

## [1.6.1] - 2026-07-24

### Added
- Share / save to cloud via system share sheet (Drive, Dropbox, email, …) from success and history
- Clearer copy that automatic cloud sync is a non-goal; local PDF remains source of truth

### Changed
- [#21](https://github.com/ArianAr/ReleaseCanvas/issues/21) reframed from multi-backend storage to optional share-to-cloud

## [1.6.0] - 2026-07-24

### Added
- **Batch multi-model releases**: shared shoot details + model roster, then sign/export one PDF per model
- Progress banner (model N of M) on terms, signature, and review
- Success screen lists all PDFs in the batch with **Next model** / **End batch**

## [1.5.0] - 2026-07-24

### Added
- Independent **app UI language** and **release wording language** pickers (en, es, fr, it, de, fa)
- Optional “Follow system” for UI language only
- Localized built-in release templates and PDF chrome labels per release language
- Persian RTL for UI (when UI=`fa`) and for terms/PDF when release language=`fa`
- In-app language settings on photographer profile; release language also on the form

## [1.4.0] - 2026-07-23

### Added
- Custom template import (.txt / .md) with {{MODEL}} / {{PHOTOGRAPHER}} placeholders
- Editable custom templates with jurisdiction labels
- Duplicate built-in templates as editable copies

## [1.3.0] - 2026-07-23

### Added
- Photographer profile defaults (name, studio, email, phone, logo)
- PDF branding (logo header, studio line, accent headings, toggle)
- First-run onboarding tips (reopen from About)
- Accessibility basics (headings, taller primary actions)

## [1.2.0] - 2026-07-23

### Added
- About screen with version and legal/privacy notes
- Terms preview step before signature
- History management (remove entry / clear list; PDFs retained)
- Age/authority attestation checkbox required before export
- Share and Email PDF with prefilled subject and body
- Multi-page PDF when content overflows Letter page 1
- Updated product roadmap and milestones

## [1.1.0] - 2026-07-23

### Added
- Empty-signature inline error and export error card with retry
- Success path polish (Documents/ReleaseCanvas copy, open/share errors, folder browse)
- Optional shoot metadata (Shoot ID, photographer contact, client/agency, location name, notes)
- Multi-template release picker (generic, 500px-style unofficial, stock RF-style, editorial, social/web)
- README SVG mock screenshots under `docs/screenshots/`
- CHANGELOG and versioning / manual release notes

## [1.0.0] - 2026-07-23

### Added
- Initial ReleaseCanvas Android app (Kotlin, Jetpack Compose, Material 3)
- Form → signature pad → review → PDF export via `PdfDocument`
- GPS + UTC signing metadata; optional city/country and reverse geocode
- MediaStore export to `Documents/ReleaseCanvas/`
- Local export history and photographer name preference
- Community docs: README, CONTRIBUTING, CODE_OF_CONDUCT, SECURITY
- Issue/PR templates and CODEOWNERS

### Versioning policy

| Field | Meaning |
|-------|---------|
| `versionName` | User-facing semver (`MAJOR.MINOR.PATCH`) in `app/build.gradle.kts` |
| `versionCode` | Integer, increase on every Play/store or tagged release |

### Version bumps & releases (required after meaningful changes)

After each **minor** or **major** change set lands on `main`:

1. Bump `versionName` (semver) and `versionCode` (+1) in `app/build.gradle.kts`
2. Move items from `[Unreleased]` into a dated `## [X.Y.Z]` section in this file
3. Open a short PR if branch protection requires it (or merge via PR as usual)
4. Tag and publish a GitHub Release:

```bash
git tag -a vX.Y.Z -m "ReleaseCanvas X.Y.Z"
git push origin vX.Y.Z
gh release create vX.Y.Z --title "vX.Y.Z" --notes-file /tmp/notes.md
```

**Semver guide for this project**

| Bump | When |
|------|------|
| **MAJOR** | Breaking data/export format or removed features |
| **MINOR** | New user-facing features (screens, templates, export options) |
| **PATCH** | Bug fixes, copy, small UX polish only |

Patch releases may skip a full GitHub Release notes page if tiny, but **minor/major always get a tag + GitHub Release**.

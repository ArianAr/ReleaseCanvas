# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
for `versionName` (with integer `versionCode` monotonically increasing).

## [Unreleased]

### Added
- (none yet)

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

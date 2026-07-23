# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
for `versionName` (with integer `versionCode` monotonically increasing).

## [Unreleased]

### Added
- About screen, terms preview, history management
- Age/authority attestation checkbox
- Email/share subject-body polish
- Multi-page PDF overflow


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

### Cutting a release (manual, no CI required)

1. Update `versionName` / `versionCode` in `app/build.gradle.kts`
2. Add a dated section under CHANGELOG
3. Commit, tag `vX.Y.Z`, push tag
4. Create a GitHub Release from the tag and attach the APK if desired

```bash
git tag -a v1.0.0 -m "ReleaseCanvas 1.0.0"
git push origin v1.0.0
gh release create v1.0.0 --title "v1.0.0" --notes-file CHANGELOG.md
```

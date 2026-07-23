# Contributing to ReleaseCanvas

Thanks for your interest in improving ReleaseCanvas. This guide keeps the project
easy to maintain and friendly to new contributors.

## Code of conduct

Participation is governed by our [Code of Conduct](CODE_OF_CONDUCT.md). By
contributing, you agree to uphold it.

## Ways to contribute

- Report bugs and suggest features via [issues](https://github.com/ArianAr/ReleaseCanvas/issues)
- Improve docs (README, comments, legal-disclaimer clarity)
- Fix bugs or add small, focused features
- Improve tests and docs

**Security issues:** follow [SECURITY.md](SECURITY.md) — do not file public issues.

## Development setup

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Quail 2 / recent stable is fine)
- JDK 17+ (Studio’s embedded **jbr-21** recommended)
- Android SDK platform **36**, build-tools, and an emulator or device on **API 29+**

### Clone and open

```bash
git clone https://github.com/ArianAr/ReleaseCanvas.git
cd ReleaseCanvas
```

Open the repo root in Android Studio and wait for Gradle sync.

### CLI checks

```bash
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

## Project map

```
app/src/main/java/com/releasecanvas/app/
  ui/           Compose screens, theme, navigation, signature pad
  data/         Location, PDF compiler, MediaStore export, DataStore prefs
  util/         Formatting and validation helpers
```

Key flows: **Form → Signature → Review → Export PDF** with UTC + optional GPS metadata.

## Coding guidelines

- Prefer small, reviewable PRs over large mixed changes
- Kotlin + Jetpack Compose idioms; keep UI state in `ReleaseViewModel`
- No secrets, keystores, or `local.properties` in commits
- Match existing package structure and Material 3 styling
- Add or update unit tests for pure helpers (`Formatters`, validation) when you change them
- Do not expand scope with drive-by refactors unrelated to the PR

### Legal template note

The model-release text in `ReleaseTerms` is a **generic template**, not legal
advice. Do not present edits as jurisdiction-certified counsel. Prefer versioning
changes (`STANDARD_V1` → `STANDARD_V2`) when wording changes meaningfully.

## Pull request process

1. Fork (or branch from `main` if you have write access)
2. Create a focused branch: `feat/…`, `fix/…`, or `docs/…`
3. Make your changes with clear commits
4. Ensure `./gradlew :app:assembleDebug :app:testDebugUnitTest` passes
5. Open a PR using the template and link related issues
6. Respond to review feedback promptly

### Commit messages

Use concise, imperative subjects:

- `Add undo support to signature pad`
- `Fix MediaStore export when display name collides`
- `Document Android Studio Quail 2 setup`

## Issue triage labels

We use labels such as `bug`, `enhancement`, `documentation`, `good first issue`,
and `help wanted`. Feel free to suggest labels in your issue text; maintainers
will apply them.

## License

By contributing, you agree that your contributions are licensed under the
project’s [GNU GPL v3](LICENSE).

## Versioning

See [CHANGELOG.md](CHANGELOG.md) for the `versionName` / `versionCode` policy and how to cut a tagged release manually (no GitHub Actions required).

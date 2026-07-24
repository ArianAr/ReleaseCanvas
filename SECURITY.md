# Security Policy

## Supported versions

| Version | Supported |
|---------|-----------|
| Latest `main` and tagged **1.x** releases | ✅ |
| Older untagged commits | ❌ Best-effort only |

Security fixes land on `main` first, then the next patch/minor tag.

## What this app handles

ReleaseCanvas is an **offline-first** Android app (plus a static web companion) that:

- Collects model/shoot form data and a signature image on-device
- Optionally reads GPS at export time **only if the user opts in** on Review
- Writes signed PDFs to the **shared** folder **Documents/ReleaseCanvas** via MediaStore
- Stores history, profile, and custom templates in app DataStore (excluded from Auto Backup via backup rules)

It does **not** upload releases to a ReleaseCanvas backend. Treat exported PDFs and on-device history as **sensitive personal data**.

### Web companion

- Static site (GitHub Pages); PDF built in the browser
- Reverse geocode may call **OpenStreetMap Nominatim** (IP + coordinates leave the device when used)
- Branding in **localStorage** (device/browser only)

## Reporting a vulnerability

**Please do not open a public issue for security problems.**

Prefer:

1. **[GitHub Private Vulnerability Reporting](https://github.com/ArianAr/ReleaseCanvas/security/advisories/new)** (preferred)
2. Draft security advisory from the repository **Security** tab
3. Contact [@ArianAr](https://github.com/ArianAr)

Include: version/commit, device/OS, repro steps, impact, PoC without destructive testing on systems you do not own.

## Response expectations

| Step | Target |
|------|--------|
| Initial acknowledgement | within **7 days** |
| Triage / severity | within **14 days** |
| Fix or mitigation plan | depends on severity |

## Scope

**In scope:** path/file issues, intent misuse, signature/history/prefs handling, dependency issues with clear exploitability in this app.

**Out of scope:** physical access with unlocked device, social engineering, generic OS bugs, legal sufficiency of template text.

## Safe harbor

Good-faith research that avoids privacy harm and public exploitation is welcome; coordinate disclosure when possible.

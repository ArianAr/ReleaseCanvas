# Security Policy

## Supported versions

| Version | Supported |
|---------|-----------|
| `main` (latest) | ✅ |
| Older commits / untagged builds | ❌ |

ReleaseCanvas is currently pre-1.0. Security fixes land on `main` first.

## What this app handles

ReleaseCanvas is an **offline-first** Android app that:

- Collects model/shoot form data and a signature image on-device
- Optionally reads GPS at export time
- Writes signed PDFs to the public **Documents/ReleaseCanvas** folder via MediaStore

It does **not** currently upload releases to a backend. Treat exported PDFs and on-device history as sensitive personal data.

## Reporting a vulnerability

**Please do not open a public issue for security problems.**

Prefer one of these private channels:

1. **[GitHub Private Vulnerability Reporting](https://github.com/ArianAr/ReleaseCanvas/security/advisories/new)** (preferred)
2. Open a **draft security advisory** from the repository **Security** tab
3. Contact the maintainer via GitHub: [@ArianAr](https://github.com/ArianAr)

Include as much detail as you can:

- Affected version / commit SHA
- Device / Android version (if relevant)
- Steps to reproduce
- Impact (data exposure, privilege escalation, etc.)
- Any proof-of-concept (no destructive testing on systems you do not own)

## Response expectations

| Step | Target |
|------|--------|
| Initial acknowledgement | within **7 days** |
| Triage / severity assessment | within **14 days** |
| Fix or mitigation plan | depends on severity and complexity |

We may ask for more information before confirming an issue. Once a fix is released, we appreciate coordinated disclosure (typically allowing time for users to update).

## Scope

**In scope (examples):**

- Path traversal or unexpected file writes outside intended export behavior
- Intent / deep-link misuse that exposes private app data
- Insecure handling of signature bitmaps, history URIs, or preferences
- Dependency issues with clear exploitability in this app’s usage

**Out of scope (examples):**

- Issues that require physical access and an unlocked device with user cooperation
- Social engineering of end users
- Generic Android OS / OEM bugs unrelated to this project
- Legal sufficiency of the model-release template text (see README disclaimer)

## Safe harbor

We will not pursue legal action against researchers who:

- Make a good-faith effort to avoid privacy violations and service disruption
- Report findings privately without public exploitation
- Do not access data that is not their own beyond what is needed to demonstrate the issue

Thank you for helping keep photographers and models safer.

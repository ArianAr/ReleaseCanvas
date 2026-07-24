# ReleaseCanvas Web

Browser companion for **sign → PDF export only**. Hosted on **GitHub Pages** (static files, no server).

## Scope

| Included | Not included |
|----------|----------------|
| Form, terms preview, signature pad | Export history |
| Client-side PDF (pdf-lib CDN) | Batch multi-model |
| Optional GPS + UTC + reverse geocode (Nominatim) | Full multi-language UI |
| **Branding** (studio, accent, logo) via **localStorage** | Native iOS app |
| Download + Web Share (when supported) | Cloud sync of profile |
| Works on iPhone Safari | Cross-device profile sync |

Prefer the **Android app** for the full product. Native **iOS is not available yet** (no Apple device for testing).

## Local preview

```bash
# from repo root — any static server
cd web && python3 -m http.server 8080
# open http://127.0.0.1:8080/
```

Use **https** (or localhost) for geolocation in most browsers.

## GitHub Pages

Workflow: `.github/workflows/deploy-pages.yml` deploys this `web/` folder on push to `main`.

After the first successful run, enable **Settings → Pages → Source: GitHub Actions**.

Site URL (typical):

`https://arianar.github.io/ReleaseCanvas/`

All asset paths are relative (`./styles.css`) so the app works under a project-pages base path.

## Third parties

| Service | When | Data |
|---------|------|------|
| OpenStreetMap Nominatim | User taps “Use device location” | IP + lat/lon (OSM policy applies) |
| pdf-lib | Bundled under `vendor/` | None (local) |
| Vazirmatn | UI + FA PDFs (`fonts/`, OFL) | None (local) |

## Fonts

Persian UI and PDF text use **[Vazirmatn](https://github.com/rastikerdar/vazirmatn)** (SIL Open Font License).

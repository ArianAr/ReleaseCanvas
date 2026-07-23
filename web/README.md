# ReleaseCanvas Web

Browser companion for **sign → PDF export only**. Hosted on **GitHub Pages** (static files, no server).

## Scope

| Included | Not included |
|----------|----------------|
| Form, terms preview, signature pad | Export history |
| Client-side PDF (pdf-lib CDN) | Photographer profile / branding |
| Optional GPS + UTC + reverse geocode (Nominatim) | Batch multi-model |
| Download + Web Share (when supported) | Full multi-language UI |
| Works on iPhone Safari | Native iOS app |

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

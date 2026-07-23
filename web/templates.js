/**
 * Unofficial English release templates (aligned with Android GENERIC_V1 etc.).
 * Not legal advice; not official third-party forms.
 */
window.RC_TEMPLATES = [
  {
    id: "generic",
    name: "Generic portrait release",
    version: "GENERIC_V1",
    shortDescription: "Broad commercial / editorial / promotional grant",
    body: `For good and valuable consideration, the receipt and sufficiency of which is hereby acknowledged, I, {model} ("Model"), grant to {photographer} ("Photographer") and Photographer's licensees, successors, and assigns the irrevocable, perpetual, worldwide right and license to use, reproduce, publish, distribute, display, and create derivative works from photographs and other images of Model captured in connection with the described shoot, in any medium now known or later developed, for any lawful commercial, editorial, promotional, or artistic purpose.

Model represents that Model is of legal age and has full authority to grant the rights set forth herein, or is the parent/guardian of the depicted person and has authority to sign on their behalf. Model releases and discharges Photographer from any and all claims, demands, or causes of action arising out of or related to the use of Model's likeness as authorized by this release, to the fullest extent permitted by applicable law.

Model acknowledges that this document was signed electronically, that the signature below is Model's own, and that the timestamp and location metadata (if available) were captured at the moment of signing for record-keeping purposes.

This release constitutes the entire agreement between the parties concerning the subject matter hereof and may be signed in electronic form. Terms version: {version}.`,
  },
  {
    id: "500px_style",
    name: "500px-style (unofficial)",
    version: "500PX_STYLE_V1",
    shortDescription: "Community / portfolio platform–oriented grant (not official 500px)",
    body: `I, {model} ("Model"), grant {photographer} ("Photographer") permission to display, share, and promote photographs of Model from the described shoot on creative and photography community platforms (including portfolio sites and social networks), and to use such images in Photographer's personal and professional portfolio.

Model understands images may be publicly viewable online and that platform terms of service (of any third-party site) apply separately; this document is not an official form of any platform. Model confirms legal age/authority to sign and releases Photographer from claims arising from authorized online/portfolio use, to the fullest extent permitted by law.

Electronic signature and signing metadata (time/location when available) are acknowledged for record-keeping. Terms version: {version} (unofficial 500px-style template).`,
  },
  {
    id: "stock_rf",
    name: "Royalty-free stock–style",
    version: "STOCK_RF_V1",
    shortDescription: "Broad commercial licensing language typical of RF stock",
    body: `I, {model} ("Model"), grant {photographer} ("Photographer") and Photographer's licensees a broad, royalty-free, perpetual, worldwide license to use images of Model from the described shoot for commercial purposes, including advertising, packaging, websites, and resale or licensing through stock or content libraries, without further compensation beyond any agreed shoot fee.

Model waives inspection/approval rights for finished uses where permitted by law, confirms age/authority to sign, and releases Photographer and downstream licensees from claims related to authorized commercial use of Model's likeness.

This is a generic RF-style template, not the official form of any stock agency. Electronic signature and metadata stamps are for record-keeping. Terms version: {version}.`,
  },
  {
    id: "editorial",
    name: "Editorial only",
    version: "EDITORIAL_V1",
    shortDescription: "Limited to editorial / newsworthy / documentary use",
    body: `I, {model} ("Model"), grant {photographer} ("Photographer") the right to use images of Model from the described shoot for editorial, journalistic, documentary, educational, and newsworthy purposes only (including print and online editorial publications). Commercial advertising or endorsement uses are not granted by this template unless separately agreed in writing.

Model confirms age/authority to sign and releases Photographer from claims arising from authorized editorial uses, to the fullest extent permitted by law. Electronic signature and metadata are for record-keeping. Terms version: {version}.`,
  },
  {
    id: "social_web",
    name: "Social & web promotional",
    version: "SOCIAL_WEB_V1",
    shortDescription: "Websites, social media, and light promotional use",
    body: `I, {model} ("Model"), grant {photographer} ("Photographer") permission to use images of Model from the described shoot on websites, blogs, and social media channels, and for light promotional use related to Photographer's services (e.g. website gallery, Instagram, booking materials). Broad third-party advertising campaigns and stock resale are outside the scope of this template unless separately agreed.

Model confirms age/authority to sign and releases Photographer from claims arising from authorized web/social promotional uses, to the fullest extent permitted by law. Electronic signature and metadata are for record-keeping. Terms version: {version}.`,
  },
];

window.RC_fillTemplate = function (template, model, photographer) {
  return template.body
    .replaceAll("{model}", model.trim())
    .replaceAll("{photographer}", photographer.trim())
    .replaceAll("{version}", template.version)
    .replace(/\s+/g, " ")
    .trim();
};

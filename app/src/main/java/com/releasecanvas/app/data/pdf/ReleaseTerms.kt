package com.releasecanvas.app.data.pdf

/**
 * Versioned model-release templates. Not legal advice — photographers should
 * have counsel review language for their jurisdiction.
 *
 * Naming like “500px-style” means **inspired / unofficial**, not an official
 * form from that company.
 */
object ReleaseTerms {

    fun body(template: ReleaseTemplate, modelName: String, photographerName: String): String {
        val model = modelName.trim()
        val photographer = photographerName.trim()
        val raw = when (template) {
            ReleaseTemplate.GENERIC -> generic(model, photographer, template.version)
            ReleaseTemplate.FIVE_HUNDRED_PX_STYLE -> fiveHundredPxStyle(model, photographer, template.version)
            ReleaseTemplate.STOCK_RF_STYLE -> stockRf(model, photographer, template.version)
            ReleaseTemplate.EDITORIAL_ONLY -> editorial(model, photographer, template.version)
            ReleaseTemplate.SOCIAL_WEB -> socialWeb(model, photographer, template.version)
        }
        return raw.trimIndent().replace(Regex("\\s+"), " ")
    }

    private fun generic(model: String, photographer: String, version: String) = """
        For good and valuable consideration, the receipt and sufficiency of which is hereby
        acknowledged, I, $model ("Model"), grant to $photographer ("Photographer")
        and Photographer's licensees, successors, and assigns the irrevocable, perpetual,
        worldwide right and license to use, reproduce, publish, distribute, display, and
        create derivative works from photographs and other images of Model captured in
        connection with the described shoot, in any medium now known or later developed,
        for any lawful commercial, editorial, promotional, or artistic purpose.

        Model represents that Model is of legal age and has full authority to grant the
        rights set forth herein, or is the parent/guardian of the depicted person and has
        authority to sign on their behalf. Model releases and discharges Photographer from
        any and all claims, demands, or causes of action arising out of or related to the
        use of Model's likeness as authorized by this release, to the fullest extent
        permitted by applicable law.

        Model acknowledges that this document was signed electronically, that the signature
        below is Model's own, and that the timestamp and location metadata (if available)
        were captured at the moment of signing for record-keeping purposes.

        This release constitutes the entire agreement between the parties concerning the
        subject matter hereof and may be signed in electronic form. Terms version: $version.
    """

    private fun fiveHundredPxStyle(model: String, photographer: String, version: String) = """
        I, $model ("Model"), grant $photographer ("Photographer") permission to display,
        share, and promote photographs of Model from the described shoot on creative and
        photography community platforms (including portfolio sites and social networks),
        and to use such images in Photographer's personal and professional portfolio.

        Model understands images may be publicly viewable online and that platform terms
        of service (of any third-party site) apply separately; this document is not an
        official form of any platform. Model confirms legal age/authority to sign and
        releases Photographer from claims arising from authorized online/portfolio use,
        to the fullest extent permitted by law.

        Electronic signature and signing metadata (time/location when available) are
        acknowledged for record-keeping. Terms version: $version (unofficial 500px-style template).
    """

    private fun stockRf(model: String, photographer: String, version: String) = """
        I, $model ("Model"), grant $photographer ("Photographer") and Photographer's
        licensees a broad, royalty-free, perpetual, worldwide license to use images of
        Model from the described shoot for commercial purposes, including advertising,
        packaging, websites, and resale or licensing through stock or content libraries,
        without further compensation beyond any agreed shoot fee.

        Model waives inspection/approval rights for finished uses where permitted by law,
        confirms age/authority to sign, and releases Photographer and downstream licensees
        from claims related to authorized commercial use of Model's likeness.

        This is a generic RF-style template, not the official form of any stock agency.
        Electronic signature and metadata stamps are for record-keeping. Terms version: $version.
    """

    private fun editorial(model: String, photographer: String, version: String) = """
        I, $model ("Model"), grant $photographer ("Photographer") the right to use images
        of Model from the described shoot for editorial, journalistic, documentary,
        educational, and newsworthy purposes only (including print and online editorial
        publications). Commercial advertising or endorsement uses are not granted by this
        template unless separately agreed in writing.

        Model confirms age/authority to sign and releases Photographer from claims arising
        from authorized editorial uses, to the fullest extent permitted by law.
        Electronic signature and metadata are for record-keeping. Terms version: $version.
    """

    private fun socialWeb(model: String, photographer: String, version: String) = """
        I, $model ("Model"), grant $photographer ("Photographer") permission to use images
        of Model from the described shoot on websites, blogs, and social media channels,
        and for light promotional use related to Photographer's services (e.g. website
        gallery, Instagram, booking materials). Broad third-party advertising campaigns
        and stock resale are outside the scope of this template unless separately agreed.

        Model confirms age/authority to sign and releases Photographer from claims arising
        from authorized web/social promotional uses, to the fullest extent permitted by law.
        Electronic signature and metadata are for record-keeping. Terms version: $version.
    """
}

package com.releasecanvas.app.data.pdf

/**
 * Versioned generic portrait model release. Not legal advice — photographers
 * should have counsel review language for their jurisdiction.
 */
object ReleaseTerms {
    const val VERSION = "STANDARD_V1"

    fun body(modelName: String, photographerName: String): String = """
        For good and valuable consideration, the receipt and sufficiency of which is hereby
        acknowledged, I, $modelName ("Model"), grant to $photographerName ("Photographer")
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
        subject matter hereof and may be signed in electronic form. Terms version: $VERSION.
    """.trimIndent().replace(Regex("\\s+"), " ")
}

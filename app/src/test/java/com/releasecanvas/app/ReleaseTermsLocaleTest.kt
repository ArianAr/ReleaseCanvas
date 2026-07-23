package com.releasecanvas.app

import com.releasecanvas.app.data.locale.AppLocale
import com.releasecanvas.app.data.pdf.PdfLabelCatalog
import com.releasecanvas.app.data.pdf.ReleaseTemplate
import com.releasecanvas.app.data.pdf.ReleaseTerms
import com.releasecanvas.app.data.pdf.TemplateResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseTermsLocaleTest {

    @Test
    fun generic_german_differs_from_english() {
        val en = ReleaseTerms.body(ReleaseTemplate.GENERIC, "Alex", "Sam", "en")
        val de = ReleaseTerms.body(ReleaseTemplate.GENERIC, "Alex", "Sam", "de")
        assertTrue(en.contains("Alex") && en.contains("Sam"))
        assertTrue(de.contains("Alex") && de.contains("Sam"))
        assertTrue(de.contains("Fotograf") || de.contains("Model"))
        assertFalse(en == de)
    }

    @Test
    fun unknown_language_falls_back_to_english() {
        val en = ReleaseTerms.body(ReleaseTemplate.GENERIC, "A", "B", "en")
        val fallback = ReleaseTerms.body(ReleaseTemplate.GENERIC, "A", "B", "zz")
        assertEquals(en, fallback)
    }

    @Test
    fun persian_body_contains_farsi_markers() {
        val fa = ReleaseTerms.body(ReleaseTemplate.SOCIAL_WEB, "مینا", "علی", "fa")
        assertTrue(fa.contains("مینا"))
        assertTrue(fa.contains("علی"))
        assertTrue(AppLocale.isRtl("fa"))
        assertFalse(AppLocale.isRtl("de"))
    }

    @Test
    fun resolveBody_passes_language_to_built_in() {
        val body = TemplateResolver.resolveBody(
            "editorial",
            emptyList(),
            "M",
            "P",
            languageTag = "es",
        )
        assertTrue(body.contains("Modelo") || body.contains("editorial") || body.contains("M"))
        assertTrue(body.contains("M") && body.contains("P"))
    }

    @Test
    fun pdf_labels_localized() {
        val en = PdfLabelCatalog.forLanguage("en")
        val de = PdfLabelCatalog.forLanguage("de")
        assertEquals("Parties", en.parties)
        assertEquals("Parteien", de.parties)
        assertTrue(PdfLabelCatalog.forLanguage("nope").title == en.title)
    }

    @Test
    fun app_locale_normalize() {
        assertEquals(AppLocale.UI_FOLLOW_SYSTEM, AppLocale.normalizeUiTag(null))
        assertEquals(AppLocale.UI_FOLLOW_SYSTEM, AppLocale.normalizeUiTag("system"))
        assertEquals("fa", AppLocale.normalizeUiTag("fa"))
        assertEquals("en", AppLocale.normalizeReleaseTag("bogus"))
        assertEquals("it", AppLocale.normalizeReleaseTag("it"))
    }
}

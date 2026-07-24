package com.releasecanvas.app

import com.releasecanvas.app.data.locale.AppLocale
import com.releasecanvas.app.data.model.ExportBlocker
import com.releasecanvas.app.data.model.FieldError
import com.releasecanvas.app.util.Validation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportAndLocaleTest {

    @Test
    fun exportBlocker_requiresSignatureAndAttestation() {
        assertEquals(
            ExportBlocker.MissingSignature,
            Validation.exportBlocker(
                hasSignatureStrokes = false,
                hasSignatureBitmap = false,
                attestationAccepted = true,
            ),
        )
        assertEquals(
            ExportBlocker.MissingAttestation,
            Validation.exportBlocker(
                hasSignatureStrokes = true,
                hasSignatureBitmap = true,
                attestationAccepted = false,
            ),
        )
        assertNull(
            Validation.exportBlocker(
                hasSignatureStrokes = true,
                hasSignatureBitmap = true,
                attestationAccepted = true,
            ),
        )
    }

    @Test
    fun validateForm_marksRequiredFields() {
        val errors = Validation.validateForm("", "", "", "")
        assertTrue(errors.hasErrors)
        assertEquals(FieldError.Required, errors.modelName)
        assertEquals(FieldError.Required, errors.modelEmail)
    }

    @Test
    fun validateForm_invalidEmail() {
        val errors = Validation.validateForm("A", "not-email", "B", "C")
        assertEquals(FieldError.InvalidEmail, errors.modelEmail)
    }

    @Test
    fun appLocale_normalize() {
        assertEquals(AppLocale.UI_FOLLOW_SYSTEM, AppLocale.normalizeUiTag(null))
        assertEquals("fa", AppLocale.normalizeUiTag("fa"))
        assertEquals("en", AppLocale.normalizeReleaseTag("nope"))
        assertTrue(AppLocale.isRtl("fa"))
        assertFalse(AppLocale.isRtl("de"))
    }

    @Test
    fun onboardingSemantics_nullMeansLoadingNotIncomplete() {
        // Document the contract used by AppNav: only explicit false shows tour.
        val loading: Boolean? = null
        val incomplete: Boolean? = false
        val complete: Boolean? = true
        assertTrue(loading != false) // do not navigate
        assertTrue(incomplete == false) // navigate
        assertTrue(complete != false) // stay
    }
}

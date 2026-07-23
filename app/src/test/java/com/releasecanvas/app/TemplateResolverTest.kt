package com.releasecanvas.app

import com.releasecanvas.app.data.model.CustomTemplate
import com.releasecanvas.app.data.pdf.TemplateResolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TemplateResolverTest {

    @Test
    fun applyPlaceholders_replacesModelAndPhotographer() {
        val raw = "I, {{MODEL}}, grant {{PHOTOGRAPHER}} rights."
        val out = TemplateResolver.applyPlaceholders(raw, "Alex", "Sam")
        assertEquals("I, Alex, grant Sam rights.", out)
    }

    @Test
    fun resolveBody_usesCustomWhenIdMatches() {
        val custom = CustomTemplate(
            id = "custom_1",
            name = "Mine",
            version = "C1",
            body = "Signed by {{MODEL}} for {{PHOTOGRAPHER}}.",
        )
        val body = TemplateResolver.resolveBody("custom_1", listOf(custom), "A", "B")
        assertEquals("Signed by A for B.", body)
    }

    @Test
    fun resolveBody_fallsBackToBuiltIn() {
        val body = TemplateResolver.resolveBody("generic", emptyList(), "Model X", "Photo Y")
        assertTrue(body.contains("Model X"))
        assertTrue(body.contains("Photo Y"))
    }

    @Test
    fun allOptions_includesBuiltInsAndCustoms() {
        val custom = CustomTemplate("c1", "Custom", "V1", "body")
        val options = TemplateResolver.allOptions(listOf(custom))
        assertTrue(options.any { it.id == "generic" && !it.isCustom })
        assertTrue(options.any { it.id == "c1" && it.isCustom })
    }
}

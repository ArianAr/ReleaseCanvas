package com.releasecanvas.app.util

import android.util.Patterns
import com.releasecanvas.app.data.model.FormErrors

object Validation {

    fun validateForm(
        modelName: String,
        modelEmail: String,
        shooterName: String,
        description: String,
    ): FormErrors {
        return FormErrors(
            modelName = required(modelName),
            modelEmail = email(modelEmail),
            shooterName = required(shooterName),
            description = required(description),
        )
    }

    private fun required(value: String): String? =
        if (value.trim().isEmpty()) "Required" else null

    private fun email(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Required"
        return if (Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) null else "Enter a valid email"
    }

    /** Test-friendly email check without Android Patterns (used in JVM unit tests via duplicate pure logic). */
    fun isValidEmailPure(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        // Pragmatic RFC-ish check sufficient for form UX
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(trimmed)
    }
}

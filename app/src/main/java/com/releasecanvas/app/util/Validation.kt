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

    /** Shared shoot fields for batch setup (model identity comes from the roster). */
    fun validateSharedShoot(
        shooterName: String,
        description: String,
    ): FormErrors {
        return FormErrors(
            shooterName = required(shooterName),
            description = required(description),
        )
    }

    /**
     * Validates a batch model roster. Returns an error message, or null if OK.
     * Requires at least [minModels] non-blank names with valid emails.
     */
    fun validateBatchModels(
        names: List<String>,
        emails: List<String>,
        minModels: Int = 2,
    ): String? {
        if (names.size != emails.size) return "Model list is inconsistent"
        val pairs = names.zip(emails).map { (n, e) -> n.trim() to e.trim() }
        val filled = pairs.filter { (n, _) -> n.isNotEmpty() }
        if (filled.size < minModels) {
            return "Add at least $minModels models with names"
        }
        filled.forEachIndexed { index, (name, email) ->
            if (name.isEmpty()) return "Model ${index + 1}: name is required"
            if (!isValidEmailPure(email)) return "Model ${index + 1}: enter a valid email"
        }
        return null
    }

    private fun required(value: String): String? =
        if (value.trim().isEmpty()) "Required" else null

    private fun email(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return "Required"
        return if (Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
            null
        } else if (isValidEmailPure(trimmed)) {
            null
        } else {
            "Enter a valid email"
        }
    }

    /** Test-friendly email check without Android Patterns (used in JVM unit tests via duplicate pure logic). */
    fun isValidEmailPure(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        // Pragmatic RFC-ish check sufficient for form UX
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(trimmed)
    }
}

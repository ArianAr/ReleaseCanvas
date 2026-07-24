package com.releasecanvas.app.util

import android.util.Patterns
import com.releasecanvas.app.data.model.BatchRosterError
import com.releasecanvas.app.data.model.ExportBlocker
import com.releasecanvas.app.data.model.FieldError
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
     * Validates a batch model roster.
     * Requires at least [minModels] non-blank names with valid emails.
     */
    fun validateBatchModels(
        names: List<String>,
        emails: List<String>,
        minModels: Int = 2,
    ): BatchRosterError? {
        if (names.size != emails.size) return BatchRosterError.Inconsistent
        val pairs = names.zip(emails).map { (n, e) -> n.trim() to e.trim() }
        val filled = pairs.filter { (n, _) -> n.isNotEmpty() }
        if (filled.size < minModels) {
            return BatchRosterError.TooFew(minModels)
        }
        filled.forEachIndexed { index, (name, email) ->
            if (name.isEmpty()) return BatchRosterError.NameRequired(index + 1)
            if (!isValidEmailPure(email)) return BatchRosterError.InvalidEmail(index + 1)
        }
        return null
    }

    fun exportBlocker(
        hasSignatureStrokes: Boolean,
        hasSignatureBitmap: Boolean,
        attestationAccepted: Boolean,
    ): ExportBlocker? {
        if (!hasSignatureStrokes || !hasSignatureBitmap) return ExportBlocker.MissingSignature
        if (!attestationAccepted) return ExportBlocker.MissingAttestation
        return null
    }

    private fun required(value: String): FieldError? =
        if (value.trim().isEmpty()) FieldError.Required else null

    private fun email(value: String): FieldError? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return FieldError.Required
        // Prefer pure regex so JVM unit tests work without Android Patterns.
        if (isValidEmailPure(trimmed)) return null
        val androidOk = runCatching {
            Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
        }.getOrDefault(false)
        return if (androidOk) null else FieldError.InvalidEmail
    }

    /** Test-friendly email check without Android Patterns. */
    fun isValidEmailPure(value: String): Boolean {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return false
        return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(trimmed)
    }
}

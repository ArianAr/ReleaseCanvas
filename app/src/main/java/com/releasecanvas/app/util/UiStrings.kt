package com.releasecanvas.app.util

import android.content.Context
import com.releasecanvas.app.R
import com.releasecanvas.app.data.model.BatchRosterError
import com.releasecanvas.app.data.model.ExportBlocker
import com.releasecanvas.app.data.model.FieldError

fun FieldError.toMessage(context: Context): String = when (this) {
    FieldError.Required -> context.getString(R.string.error_required)
    FieldError.InvalidEmail -> context.getString(R.string.error_email)
}

fun ExportBlocker.toMessage(context: Context): String = when (this) {
    ExportBlocker.MissingSignature -> context.getString(R.string.error_signature_empty)
    ExportBlocker.MissingAttestation -> context.getString(R.string.error_attestation_required)
}

fun BatchRosterError.toMessage(context: Context): String = when (this) {
    BatchRosterError.Inconsistent -> context.getString(R.string.batch_error_inconsistent)
    is BatchRosterError.TooFew -> context.getString(R.string.batch_error_too_few, min)
    is BatchRosterError.NameRequired -> context.getString(R.string.batch_error_name_required, index)
    is BatchRosterError.InvalidEmail -> context.getString(R.string.batch_error_email, index)
}

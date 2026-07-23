package com.releasecanvas.app.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.releasecanvas.app.R

/**
 * System intents for opening/sharing release PDFs.
 * Cloud backup is intentional via the share sheet (Drive, Dropbox, etc.) —
 * not automatic sync or a multi-vendor storage SDK.
 */
object PdfIntents {

    fun openPdf(context: Context, uri: Uri): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return try {
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    /**
     * Opens the system share sheet so the user can save/send the PDF to email,
     * messaging, or cloud apps installed on the device.
     */
    fun sharePdf(
        context: Context,
        uri: Uri,
        modelName: String,
        displayName: String,
        signedAtUtc: String? = null,
    ): Boolean {
        val subjectName = modelName.ifBlank { displayName }
        val subject = context.getString(R.string.email_subject, subjectName)
        val body = if (signedAtUtc != null) {
            context.getString(R.string.email_body, displayName, signedAtUtc)
        } else {
            context.getString(R.string.share_pdf_body_short, displayName)
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = android.content.ClipData.newUri(
                context.contentResolver,
                displayName,
                uri,
            )
        }
        return try {
            context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.share_pdf_chooser_title)),
            )
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }
}

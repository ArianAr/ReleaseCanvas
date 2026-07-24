package com.releasecanvas.app.data.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

sealed class LogoImportResult {
    data class Ok(val path: String) : LogoImportResult()
    data object Invalid : LogoImportResult()
    data object TooLarge : LogoImportResult()
}

/**
 * Caps size/type of photographer logo files stored under app filesDir.
 */
class LogoImporter(
    private val context: Context,
    private val maxBytes: Int = 2 * 1024 * 1024,
    private val maxEdge: Int = 4096,
) {
    suspend fun import(uri: Uri): LogoImportResult = withContext(Dispatchers.IO) {
        val type = context.contentResolver.getType(uri).orEmpty()
        if (type.isNotEmpty() && !type.startsWith("image/")) {
            return@withContext LogoImportResult.Invalid
        }
        runCatching {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: return@withContext LogoImportResult.Invalid
            if (bytes.size > maxBytes) return@withContext LogoImportResult.TooLarge
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0 ||
                bounds.outWidth > maxEdge || bounds.outHeight > maxEdge
            ) {
                return@withContext LogoImportResult.Invalid
            }
            val dest = File(context.filesDir, "profile_logo.jpg")
            FileOutputStream(dest).use { it.write(bytes) }
            LogoImportResult.Ok(dest.absolutePath)
        }.getOrDefault(LogoImportResult.Invalid)
    }
}

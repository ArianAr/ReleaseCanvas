package com.releasecanvas.app.data.storage

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.releasecanvas.app.data.model.ExportResult
import com.releasecanvas.app.data.model.SigningMetadata
import com.releasecanvas.app.util.Formatters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DocumentStore(private val context: Context) {

    suspend fun savePdf(
        bytes: ByteArray,
        modelName: String,
        metadata: SigningMetadata,
    ): ExportResult = withContext(Dispatchers.IO) {
        val displayName = Formatters.buildFileName(modelName, metadata.signedAtUtc)
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOCUMENTS}/ReleaseCanvas",
            )
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(collection, values)
            ?: error("MediaStore rejected PDF insert")

        try {
            resolver.openOutputStream(uri)?.use { stream ->
                stream.write(bytes)
                stream.flush()
            } ?: error("Unable to open output stream for $uri")

            val done = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }
            resolver.update(uri, done, null, null)

            ExportResult(
                displayName = displayName,
                contentUri = uri,
                metadata = metadata,
            )
        } catch (t: Throwable) {
            resolver.delete(uri, null, null)
            throw t
        }
    }

    fun openViewIntentUri(uri: Uri): Uri = uri
}

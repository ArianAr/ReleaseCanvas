package com.releasecanvas.app.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.theme.screenBody
import com.releasecanvas.app.util.Formatters
import com.releasecanvas.app.util.PdfIntents
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(
    viewModel: ReleaseViewModel,
    onDone: () -> Unit,
    onNewRelease: () -> Unit,
    onNextBatchModel: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val export = state.lastExport
    val batch = state.batch
    val batchActive = batch != null
    val hasNextModel = batch?.hasNext == true
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val openFailed = stringResource(R.string.open_pdf_failed)
    val shareFailed = stringResource(R.string.share_pdf_failed)
    val folderFailed = stringResource(R.string.open_folder_failed)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (batchActive && !hasNextModel && (batch?.completedExports?.isNotEmpty() == true)) {
                                R.string.batch_complete_title
                            } else {
                                R.string.success_title
                            },
                        ),
                    )
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .screenBody(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(64.dp),
            )
            Spacer(Modifier.height(16.dp))
            if (batchActive) {
                Text(
                    text = stringResource(
                        R.string.batch_model_exported,
                        batch!!.completedExports.size,
                        batch.total,
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = export?.displayName ?: stringResource(R.string.pdf_saved_fallback),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.saved_path_detail),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.saved_path_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
            export?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Signed: ${Formatters.formatUtc(it.metadata.signedAtUtc)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Location: ${Formatters.formatLocation(it.metadata)}",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }

            if (batch != null && batch.completedExports.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.batch_exports_heading),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                batch.completedExports.forEach { record ->
                    Text(
                        text = "${record.modelName} — ${record.export.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(record.export.contentUri, "application/pdf")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                runCatching { context.startActivity(intent) }
                                    .onFailure {
                                        scope.launch { snackbar.showSnackbar(openFailed) }
                                    }
                            }
                            .padding(vertical = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    val uri = export?.contentUri ?: return@Button
                    if (!PdfIntents.openPdf(context, uri)) {
                        scope.launch { snackbar.showSnackbar(openFailed) }
                    }
                },
                enabled = export != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.open_pdf))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    val exp = export ?: return@OutlinedButton
                    val ok = PdfIntents.sharePdf(
                        context = context,
                        uri = exp.contentUri,
                        modelName = state.draft.modelName,
                        displayName = exp.displayName,
                        signedAtUtc = Formatters.formatUtc(exp.metadata.signedAtUtc),
                    )
                    if (!ok) {
                        scope.launch { snackbar.showSnackbar(shareFailed) }
                    }
                },
                enabled = export != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.share_pdf))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    val exp = export ?: return@OutlinedButton
                    val subject = context.getString(
                        R.string.email_subject,
                        state.draft.modelName.ifBlank { exp.displayName },
                    )
                    val body = context.getString(
                        R.string.email_body,
                        exp.displayName,
                        Formatters.formatUtc(exp.metadata.signedAtUtc),
                    )
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_STREAM, exp.contentUri)
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, body)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    try {
                        context.startActivity(
                            Intent.createChooser(intent, context.getString(R.string.email_pdf)),
                        )
                    } catch (_: ActivityNotFoundException) {
                        scope.launch { snackbar.showSnackbar(shareFailed) }
                    }
                },
                enabled = export != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.email_pdf))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    if (!openReleaseCanvasFolder(context)) {
                        scope.launch { snackbar.showSnackbar(folderFailed) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.open_folder))
            }
            if (hasNextModel) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onNextBatchModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                ) {
                    Text(stringResource(R.string.batch_next_model))
                }
            }
            if (!batchActive) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onNewRelease,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.new_release))
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(
                        if (batchActive && hasNextModel) {
                            R.string.batch_end_early
                        } else {
                            R.string.done
                        },
                    ),
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/**
 * Best-effort open of Documents/ReleaseCanvas in a system file UI.
 * OEMs differ; callers should handle false.
 */
private fun openReleaseCanvasFolder(context: android.content.Context): Boolean {
    val docId = "primary:Documents/ReleaseCanvas"
    val treeUri = DocumentsContract.buildTreeDocumentUri(
        "com.android.externalstorage.documents",
        docId,
    )
    val documentUri = DocumentsContract.buildDocumentUri(
        "com.android.externalstorage.documents",
        docId,
    )
    val candidates = listOf(
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(documentUri, DocumentsContract.Document.MIME_TYPE_DIR)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },
        Intent(Intent.ACTION_VIEW).apply {
            data = treeUri
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADocuments")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
    )
    for (intent in candidates) {
        if (intent.resolveActivity(context.packageManager) != null) {
            return runCatching {
                context.startActivity(intent)
                true
            }.getOrDefault(false)
        }
    }
    return false
}

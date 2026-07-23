package com.releasecanvas.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.util.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(
    viewModel: ReleaseViewModel,
    onDone: () -> Unit,
    onNewRelease: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val export = state.lastExport
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.success_title)) })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
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
            Text(
                text = export?.displayName ?: "PDF saved",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Saved to Documents/ReleaseCanvas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            export?.let {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Signed: ${Formatters.formatUtc(it.metadata.signedAtUtc)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "GPS: ${Formatters.formatLocation(it.metadata)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    export?.contentUri?.let { uri ->
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        runCatching { context.startActivity(intent) }
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
                    export?.contentUri?.let { uri ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        runCatching {
                            context.startActivity(Intent.createChooser(intent, "Share release"))
                        }
                    }
                },
                enabled = export != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.share_pdf))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onNewRelease,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.new_release))
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.done))
            }
        }
    }
}

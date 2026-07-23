package com.releasecanvas.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.data.model.HistoryEntry
import com.releasecanvas.app.ui.ReleaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ReleaseViewModel,
    onNewRelease: () -> Unit,
    onAbout: () -> Unit = {},
) {
    val history by viewModel.history.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendingDelete by remember { mutableStateOf<HistoryEntry?>(null) }
    var showClearAll by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onAbout) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.about_title),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onNewRelease,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.new_release))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.about_legal),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.recent_releases),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (history.isNotEmpty()) {
                    TextButton(onClick = { showClearAll = true }) {
                        Text(stringResource(R.string.clear_history))
                    }
                }
            }
            Text(
                text = stringResource(R.string.history_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            if (history.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_recent),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(history, key = { it.uriString + it.signedAtUtc }) { entry ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            ListItem(
                                leadingContent = {
                                    Icon(Icons.Outlined.Description, contentDescription = null)
                                },
                                headlineContent = {
                                    Text(
                                        entry.modelName.ifBlank { entry.displayName },
                                        modifier = Modifier.clickable {
                                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                                setDataAndType(Uri.parse(entry.uriString), "application/pdf")
                                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                            }
                                            runCatching { context.startActivity(intent) }
                                        },
                                    )
                                },
                                supportingContent = {
                                    Text("${entry.displayName}\n${entry.signedAtUtc}")
                                },
                                trailingContent = {
                                    IconButton(onClick = { pendingDelete = entry }) {
                                        Icon(
                                            Icons.Outlined.Delete,
                                            contentDescription = stringResource(R.string.remove_history_item),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { entry ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(stringResource(R.string.remove_history_title)) },
            text = { Text(stringResource(R.string.remove_history_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.removeHistoryEntry(entry.uriString)
                        pendingDelete = null
                    },
                ) {
                    Text(stringResource(R.string.remove_history_item))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showClearAll) {
        AlertDialog(
            onDismissRequest = { showClearAll = false },
            title = { Text(stringResource(R.string.clear_history_title)) },
            text = { Text(stringResource(R.string.clear_history_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearHistory()
                        showClearAll = false
                    },
                ) {
                    Text(stringResource(R.string.clear_history))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAll = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

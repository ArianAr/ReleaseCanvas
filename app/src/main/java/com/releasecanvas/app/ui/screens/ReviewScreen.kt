package com.releasecanvas.app.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.data.locale.AppLocale
import com.releasecanvas.app.data.model.LocationStatus
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.components.BatchProgressBanner
import com.releasecanvas.app.ui.theme.screenBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    viewModel: ReleaseViewModel,
    onBack: () -> Unit,
    onExported: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val draft = state.draft

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) {
        viewModel.refreshLocationPreview()
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
        viewModel.refreshLocationPreview()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.review_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !state.isExporting) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .screenBody(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            state.batch?.let { batch ->
                BatchProgressBanner(batch = batch)
                Spacer(Modifier.height(12.dp))
            }
            ReviewRow("Release template", state.selectedTemplateOption.displayName)
            ReviewRow(
                stringResource(R.string.review_release_language),
                AppLocale.displayName(state.releaseLanguageTag),
            )
            ReviewRow("Model", draft.modelName)
            ReviewRow("Email", draft.modelEmail)
            ReviewRow("Photographer", draft.shooterName)
            ReviewRow("Description", draft.description)
            if (draft.shootId.isNotBlank()) ReviewRow("Shoot ID", draft.shootId)
            if (draft.photographerEmail.isNotBlank()) ReviewRow("Photographer email", draft.photographerEmail)
            if (draft.photographerPhone.isNotBlank()) ReviewRow("Photographer phone", draft.photographerPhone)
            if (draft.clientAgency.isNotBlank()) ReviewRow("Client / agency", draft.clientAgency)
            if (draft.locationName.isNotBlank()) ReviewRow("Shoot location", draft.locationName)
            if (draft.notes.isNotBlank()) ReviewRow("Notes", draft.notes)
            if (draft.city.isNotBlank()) ReviewRow("City", draft.city)
            if (draft.country.isNotBlank()) ReviewRow("Country", draft.country)
            Spacer(Modifier.height(12.dp))
            Text("Signature", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            SignaturePreview(draft.signatureBitmap)
            Spacer(Modifier.height(16.dp))
            Text("Location stamp", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = state.locationPreviewText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (state.locationPreviewStatus != LocationStatus.Available &&
                state.locationPreviewStatus != LocationStatus.Acquiring
            ) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.location_unavailable_warning),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .toggleable(
                        value = state.attestationAccepted,
                        onValueChange = viewModel::setAttestationAccepted,
                        role = Role.Checkbox,
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Checkbox(
                    checked = state.attestationAccepted,
                    onCheckedChange = null,
                )
                Text(
                    text = stringResource(R.string.attestation_label),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp, top = 12.dp),
                )
            }
            state.exportError?.let { error ->
                Spacer(Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.export_error_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.clearExportError()
                                viewModel.export(onSuccess = onExported)
                            },
                            enabled = !state.isExporting,
                        ) {
                            Text(stringResource(R.string.retry_export))
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            if (state.isExporting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.exporting),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            } else {
                Button(
                    onClick = {
                        viewModel.clearExportError()
                        viewModel.export(onSuccess = onExported)
                    },
                    enabled = state.attestationAccepted,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.sign_and_export))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    Text(value, style = MaterialTheme.typography.bodyLarge)
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun SignaturePreview(bitmap: Bitmap?) {
    if (bitmap == null || bitmap.isRecycled) {
        Text("—", style = MaterialTheme.typography.bodyMedium)
        return
    }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = "Signature",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(4.dp),
    )
}

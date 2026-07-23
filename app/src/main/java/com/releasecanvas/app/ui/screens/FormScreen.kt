package com.releasecanvas.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.ui.ReleaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.releasecanvas.app.ui.theme.screenBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: ReleaseViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val draft = state.draft
    val errors = state.formErrors
    val selected = state.selectedTemplateOption
    var templateMenuExpanded by remember { mutableStateOf(false) }
    var showImportNameDialog by remember { mutableStateOf(false) }
    var pendingImportBody by remember { mutableStateOf<String?>(null) }
    var importName by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editVersion by remember { mutableStateOf("") }
    var editJurisdiction by remember { mutableStateOf("") }
    var editBody by remember { mutableStateOf("") }
    var editId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val text = withContext(Dispatchers.IO) {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }
            if (!text.isNullOrBlank()) {
                pendingImportBody = text
                importName = uri.lastPathSegment
                    ?.substringAfterLast('/')
                    ?.substringBeforeLast('.')
                    ?.ifBlank { "Imported template" }
                    ?: "Imported template"
                showImportNameDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.form_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            Text(
                text = stringResource(R.string.release_template_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.release_template_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            ExposedDropdownMenuBox(
                expanded = templateMenuExpanded,
                onExpandedChange = { templateMenuExpanded = it },
            ) {
                OutlinedTextField(
                    value = selected.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.release_template)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = templateMenuExpanded) },
                    supportingText = { Text(selected.shortDescription) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
                ExposedDropdownMenu(
                    expanded = templateMenuExpanded,
                    onDismissRequest = { templateMenuExpanded = false },
                ) {
                    state.templateOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(option.displayName)
                                    Text(
                                        option.shortDescription,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            },
                            onClick = {
                                viewModel.updateTemplateId(option.id)
                                templateMenuExpanded = false
                            },
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        importLauncher.launch(arrayOf("text/plain", "text/*", "text/markdown", "application/octet-stream"))
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.import_template))
                }
                Spacer(Modifier.width(8.dp))
                if (selected.isCustom) {
                    OutlinedButton(
                        onClick = {
                            val custom = viewModel.customTemplateById(selected.id)
                            if (custom != null) {
                                editId = custom.id
                                editName = custom.name
                                editVersion = custom.version
                                editJurisdiction = custom.jurisdiction
                                editBody = custom.body
                                showEditDialog = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.edit_custom_template))
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.forkBuiltInAsCustom(selected.id) },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(stringResource(R.string.fork_template))
                    }
                }
            }
            if (selected.isCustom) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.deleteCustomTemplate(selected.id) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.delete_custom_template))
                }
            }
            Text(
                text = stringResource(R.string.import_template_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = draft.modelName,
                onValueChange = viewModel::updateModelName,
                label = { Text(stringResource(R.string.model_name)) },
                isError = errors.modelName != null,
                supportingText = errors.modelName?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.modelEmail,
                onValueChange = viewModel::updateModelEmail,
                label = { Text(stringResource(R.string.model_email)) },
                isError = errors.modelEmail != null,
                supportingText = errors.modelEmail?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.shooterName,
                onValueChange = viewModel::updateShooterName,
                label = { Text(stringResource(R.string.shooter_name)) },
                isError = errors.shooterName != null,
                supportingText = errors.shooterName?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.description,
                onValueChange = viewModel::updateDescription,
                label = { Text(stringResource(R.string.description)) },
                isError = errors.description != null,
                supportingText = errors.description?.let { { Text(it) } },
                minLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.optional_shoot_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.optional_shoot_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.shootId,
                onValueChange = viewModel::updateShootId,
                label = { Text(stringResource(R.string.shoot_id_optional)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.photographerEmail,
                onValueChange = viewModel::updatePhotographerEmail,
                label = { Text(stringResource(R.string.photographer_email_optional)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.photographerPhone,
                onValueChange = viewModel::updatePhotographerPhone,
                label = { Text(stringResource(R.string.photographer_phone_optional)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.clientAgency,
                onValueChange = viewModel::updateClientAgency,
                label = { Text(stringResource(R.string.client_agency_optional)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.locationName,
                onValueChange = viewModel::updateLocationName,
                label = { Text(stringResource(R.string.location_name_optional)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text(stringResource(R.string.notes_optional)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.optional_place_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.optional_place_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.city,
                onValueChange = viewModel::updateCity,
                label = { Text(stringResource(R.string.city_optional)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.country,
                onValueChange = viewModel::updateCountry,
                label = { Text(stringResource(R.string.country_optional)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.continue_action))
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showImportNameDialog && pendingImportBody != null) {
        AlertDialog(
            onDismissRequest = {
                showImportNameDialog = false
                pendingImportBody = null
            },
            title = { Text(stringResource(R.string.import_template_name_title)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = importName,
                        onValueChange = { importName = it },
                        label = { Text(stringResource(R.string.import_template_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val body = pendingImportBody
                        if (body != null) {
                            viewModel.importCustomTemplate(importName, body)
                        }
                        showImportNameDialog = false
                        pendingImportBody = null
                    },
                ) {
                    Text(stringResource(R.string.import_template_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImportNameDialog = false
                        pendingImportBody = null
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showEditDialog && editId != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(stringResource(R.string.edit_template_title)) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(stringResource(R.string.import_template_name_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editJurisdiction,
                        onValueChange = { editJurisdiction = it },
                        label = { Text(stringResource(R.string.template_jurisdiction)) },
                        supportingText = { Text(stringResource(R.string.template_jurisdiction_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editVersion,
                        onValueChange = { editVersion = it },
                        label = { Text(stringResource(R.string.template_version_label)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBody,
                        onValueChange = { editBody = it },
                        label = { Text(stringResource(R.string.template_body_label)) },
                        minLines = 8,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = editId ?: return@TextButton
                        viewModel.saveCustomTemplate(
                            com.releasecanvas.app.data.model.CustomTemplate(
                                id = id,
                                name = editName,
                                version = editVersion.ifBlank { "CUSTOM_V1" },
                                body = editBody,
                                jurisdiction = editJurisdiction,
                            ),
                        )
                        showEditDialog = false
                    },
                ) {
                    Text(stringResource(R.string.profile_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

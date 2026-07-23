package com.releasecanvas.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.data.model.BatchModelInput
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.components.LanguagePickerField
import com.releasecanvas.app.ui.theme.screenBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchSetupScreen(
    viewModel: ReleaseViewModel,
    onBack: () -> Unit,
    onStart: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val draft = state.draft
    val errors = state.formErrors
    val selected = state.selectedTemplateOption
    var templateMenuExpanded by remember { mutableStateOf(false) }
    var models by remember {
        mutableStateOf(
            listOf(
                BatchModelInput(id = viewModel.newBatchModelId()),
                BatchModelInput(id = viewModel.newBatchModelId()),
            ),
        )
    }

    fun updateModel(index: Int, transform: (BatchModelInput) -> BatchModelInput) {
        models = models.toMutableList().also { list ->
            if (index in list.indices) list[index] = transform(list[index])
        }
        viewModel.clearBatchSetupError()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.batch_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
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
                text = stringResource(R.string.batch_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.batch_shared_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.batch_shared_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))

            LanguagePickerField(
                label = stringResource(R.string.release_language),
                supportingText = stringResource(R.string.release_language_hint),
                selectedTag = state.releaseLanguageTag,
                includeSystemOption = false,
                systemOptionLabel = stringResource(R.string.ui_language_system),
                onSelected = { viewModel.updateReleaseLanguage(it) },
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
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = templateMenuExpanded)
                    },
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
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
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
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = draft.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text(stringResource(R.string.notes_optional)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.batch_models_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.batch_models_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))

            models.forEachIndexed { index, model ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(R.string.batch_model_n, index + 1),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f),
                            )
                            if (models.size > 2) {
                                IconButton(
                                    onClick = {
                                        models = models.filterIndexed { i, _ -> i != index }
                                        viewModel.clearBatchSetupError()
                                    },
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = stringResource(R.string.batch_remove_model),
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = model.modelName,
                            onValueChange = { value ->
                                updateModel(index) { it.copy(modelName = value) }
                            },
                            label = { Text(stringResource(R.string.model_name)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = model.modelEmail,
                            onValueChange = { value ->
                                updateModel(index) { it.copy(modelEmail = value) }
                            },
                            label = { Text(stringResource(R.string.model_email)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = {
                    models = models + BatchModelInput(id = viewModel.newBatchModelId())
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.batch_add_model))
            }

            state.batchSetupError?.let { err ->
                Spacer(Modifier.height(12.dp))
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (viewModel.startBatch(models)) {
                        onStart()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(stringResource(R.string.batch_start))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

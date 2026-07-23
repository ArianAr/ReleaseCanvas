package com.releasecanvas.app.ui.screens

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.releasecanvas.app.R
import com.releasecanvas.app.data.model.PhotographerProfile
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.components.LanguagePickerField
import kotlinx.coroutines.launch
import java.io.File
import com.releasecanvas.app.ui.theme.screenBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ReleaseViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val saved = state.profile
    var displayName by remember(saved.displayName) { mutableStateOf(saved.displayName) }
    var studioName by remember(saved.studioName) { mutableStateOf(saved.studioName) }
    var email by remember(saved.email) { mutableStateOf(saved.email) }
    var phone by remember(saved.phone) { mutableStateOf(saved.phone) }
    var logoPath by remember(saved.logoPath) { mutableStateOf(saved.logoPath) }
    var brandingEnabled by remember(saved.brandingEnabled) { mutableStateOf(saved.brandingEnabled) }
    var brandAccentHex by remember(saved.brandAccentHex) { mutableStateOf(saved.brandAccentHex) }

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMsg = stringResource(R.string.profile_saved)

    LaunchedEffect(state.profileSavedMessage) {
        state.profileSavedMessage?.let {
            snackbar.showSnackbar(savedMsg)
            viewModel.clearProfileSavedMessage()
        }
    }

    val pickLogo = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val path = viewModel.importProfileLogo(uri)
            if (path != null) logoPath = path
        }
    }

    val logoBitmap = remember(logoPath) {
        if (logoPath.isNotBlank() && File(logoPath).exists()) {
            BitmapFactory.decodeFile(logoPath)
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
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
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .screenBody(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.profile_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.language_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            LanguagePickerField(
                label = stringResource(R.string.ui_language),
                supportingText = stringResource(R.string.ui_language_hint),
                selectedTag = state.uiLanguageTag,
                includeSystemOption = true,
                systemOptionLabel = stringResource(R.string.ui_language_system),
                onSelected = { viewModel.updateUiLanguage(it) },
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
            Spacer(Modifier.height(20.dp))
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text(stringResource(R.string.profile_display_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = studioName,
                onValueChange = { studioName = it },
                label = { Text(stringResource(R.string.profile_studio_name)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.profile_email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.profile_phone)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.profile_logo_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            if (logoBitmap != null && !logoBitmap.isRecycled) {
                Image(
                    bitmap = logoBitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.profile_logo_section),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Spacer(Modifier.height(8.dp))
            }
            OutlinedButton(
                onClick = { pickLogo.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.profile_pick_logo))
            }
            if (logoPath.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val current = PhotographerProfile(
                            displayName = displayName,
                            studioName = studioName,
                            email = email,
                            phone = phone,
                            logoPath = logoPath,
                            brandingEnabled = brandingEnabled,
                            brandAccentHex = brandAccentHex,
                        )
                        logoPath = ""
                        viewModel.clearProfileLogo(current)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.profile_clear_logo))
                }
            }
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.profile_branding_section),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = brandingEnabled,
                    onCheckedChange = { brandingEnabled = it },
                )
                Text(
                    text = stringResource(R.string.profile_branding_enabled),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = brandAccentHex,
                onValueChange = { brandAccentHex = it },
                label = { Text(stringResource(R.string.profile_brand_accent)) },
                supportingText = { Text(stringResource(R.string.profile_brand_accent_hint)) },
                singleLine = true,
                enabled = brandingEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.savePhotographerProfile(
                        PhotographerProfile(
                            displayName = displayName,
                            studioName = studioName,
                            email = email,
                            phone = phone,
                            logoPath = logoPath,
                            brandingEnabled = brandingEnabled,
                            brandAccentHex = brandAccentHex,
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.profile_save))
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

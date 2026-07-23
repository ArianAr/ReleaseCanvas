package com.releasecanvas.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.releasecanvas.app.R
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.components.SignaturePad
import com.releasecanvas.app.ui.components.emitBitmap
import com.releasecanvas.app.ui.components.rememberSignaturePadState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    viewModel: ReleaseViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val padState = rememberSignaturePadState()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val emptyMessage = stringResource(R.string.error_signature_empty)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.signature_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(R.string.signature_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            SignaturePad(
                state = padState,
                onStrokeChange = { hasStrokes, bitmap ->
                    viewModel.setSignature(bitmap, hasStrokes)
                },
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = {
                        padState.undo()
                        padState.emitBitmap { has, bmp -> viewModel.setSignature(bmp, has) }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.undo))
                }
                OutlinedButton(
                    onClick = {
                        padState.clear()
                        viewModel.clearSignatureFlag()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.clear))
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    if (!padState.hasStrokes) {
                        scope.launch { snackbar.showSnackbar(emptyMessage) }
                    } else {
                        padState.emitBitmap { has, bmp -> viewModel.setSignature(bmp, has) }
                        onContinue()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.continue_action))
            }
        }
    }
}

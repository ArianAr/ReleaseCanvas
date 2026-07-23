package com.releasecanvas.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.releasecanvas.app.R
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.components.SignaturePad
import com.releasecanvas.app.ui.components.emitBitmap
import com.releasecanvas.app.ui.components.rememberSignaturePadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen(
    viewModel: ReleaseViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit,
) {
    val padState = rememberSignaturePadState()
    var showEmptyError by remember { mutableStateOf(false) }

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
                modifier = if (showEmptyError) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(12.dp))
                } else {
                    Modifier
                },
                onStrokeChange = { hasStrokes, bitmap ->
                    if (hasStrokes) showEmptyError = false
                    viewModel.setSignature(bitmap, hasStrokes)
                },
            )
            if (showEmptyError) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.error_signature_empty),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = {
                        padState.undo()
                        padState.emitBitmap { has, bmp ->
                            if (has) showEmptyError = false
                            viewModel.setSignature(bmp, has)
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.undo))
                }
                OutlinedButton(
                    onClick = {
                        padState.clear()
                        viewModel.clearSignatureFlag()
                        showEmptyError = false
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
                        showEmptyError = true
                    } else {
                        showEmptyError = false
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

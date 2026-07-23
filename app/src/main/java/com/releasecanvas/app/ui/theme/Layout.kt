package com.releasecanvas.app.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Scaffold body padding: system insets from [padding], then horizontal margins
 * and a small gap under the top app bar so body text is not flush with the header.
 */
fun Modifier.screenBody(padding: PaddingValues): Modifier =
    this
        .padding(padding)
        .padding(start = 20.dp, end = 20.dp, top = 12.dp)

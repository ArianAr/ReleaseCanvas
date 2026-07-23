package com.releasecanvas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.navigation.AppNav
import com.releasecanvas.app.ui.theme.ReleaseCanvasTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ReleaseViewModel by viewModels {
        ReleaseViewModel.factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReleaseCanvasTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                ) {
                    AppNav(viewModel = viewModel)
                }
            }
        }
    }
}

package com.releasecanvas.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
        // Draw behind system bars; Scaffold / TopAppBar own the insets so the
        // status bar sits on the app bar surface with readable icons (not a
        // blank white strip above content on notched devices).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
        )
        super.onCreate(savedInstanceState)
        setContent {
            ReleaseCanvasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNav(viewModel = viewModel)
                }
            }
        }
    }
}

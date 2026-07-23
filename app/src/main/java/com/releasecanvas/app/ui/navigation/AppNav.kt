package com.releasecanvas.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.screens.AboutScreen
import com.releasecanvas.app.ui.screens.FormScreen
import com.releasecanvas.app.ui.screens.HomeScreen
import com.releasecanvas.app.ui.screens.ReviewScreen
import com.releasecanvas.app.ui.screens.SignatureScreen
import com.releasecanvas.app.ui.screens.SuccessScreen
import com.releasecanvas.app.ui.screens.TermsPreviewScreen

object Routes {
    const val HOME = "home"
    const val FORM = "form"
    const val TERMS = "terms"
    const val SIGNATURE = "signature"
    const val REVIEW = "review"
    const val SUCCESS = "success"
    const val ABOUT = "about"
}

@Composable
fun AppNav(
    viewModel: ReleaseViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNewRelease = {
                    viewModel.resetForNewRelease()
                    navController.navigate(Routes.FORM)
                },
                onAbout = { navController.navigate(Routes.ABOUT) },
            )
        }
        composable(Routes.ABOUT) {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.FORM) {
            FormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onContinue = {
                    if (viewModel.validateForm()) {
                        navController.navigate(Routes.TERMS)
                    }
                },
            )
        }
        composable(Routes.TERMS) {
            TermsPreviewScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.SIGNATURE) },
            )
        }
        composable(Routes.SIGNATURE) {
            SignatureScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(Routes.REVIEW) },
            )
        }
        composable(Routes.REVIEW) {
            ReviewScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onExported = {
                    navController.navigate(Routes.SUCCESS) {
                        popUpTo(Routes.HOME)
                    }
                },
            )
        }
        composable(Routes.SUCCESS) {
            SuccessScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onNewRelease = {
                    viewModel.resetForNewRelease()
                    navController.navigate(Routes.FORM) {
                        popUpTo(Routes.HOME)
                    }
                },
            )
        }
    }
}

package com.releasecanvas.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.releasecanvas.app.ui.ReleaseViewModel
import com.releasecanvas.app.ui.screens.AboutScreen
import com.releasecanvas.app.ui.screens.BatchSetupScreen
import com.releasecanvas.app.ui.screens.FormScreen
import com.releasecanvas.app.ui.screens.HomeScreen
import com.releasecanvas.app.ui.screens.OnboardingScreen
import com.releasecanvas.app.ui.screens.ProfileScreen
import com.releasecanvas.app.ui.screens.ReviewScreen
import com.releasecanvas.app.ui.screens.SignatureScreen
import com.releasecanvas.app.ui.screens.SuccessScreen
import com.releasecanvas.app.ui.screens.TermsPreviewScreen

object Routes {
    const val HOME = "home"
    const val FORM = "form"
    const val BATCH_SETUP = "batch_setup"
    const val TERMS = "terms"
    const val SIGNATURE = "signature"
    const val REVIEW = "review"
    const val SUCCESS = "success"
    const val ABOUT = "about"
    const val PROFILE = "profile"
    const val ONBOARDING = "onboarding"
}

@Composable
fun AppNav(
    viewModel: ReleaseViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    // null = prefs still loading — never treat that as “show onboarding”
    val onboardingDone by viewModel.onboardingDone.collectAsStateWithLifecycle()

    LaunchedEffect(onboardingDone) {
        val done = onboardingDone ?: return@LaunchedEffect
        val route = navController.currentDestination?.route
        if (!done && route != Routes.ONBOARDING) {
            navController.navigate(Routes.ONBOARDING) {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    viewModel.completeOnboarding()
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNewRelease = {
                    viewModel.resetForNewRelease()
                    navController.navigate(Routes.FORM)
                },
                onBatchRelease = {
                    viewModel.prepareBatchSetup()
                    navController.navigate(Routes.BATCH_SETUP)
                },
                onAbout = { navController.navigate(Routes.ABOUT) },
                onProfile = { navController.navigate(Routes.PROFILE) },
            )
        }
        composable(Routes.ABOUT) {
            AboutScreen(
                onBack = { navController.popBackStack() },
                onShowTips = {
                    viewModel.reopenOnboarding()
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.HOME)
                    }
                },
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
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
        composable(Routes.BATCH_SETUP) {
            BatchSetupScreen(
                viewModel = viewModel,
                onBack = {
                    viewModel.endBatch()
                    navController.popBackStack()
                },
                onStart = {
                    navController.navigate(Routes.TERMS) {
                        popUpTo(Routes.BATCH_SETUP)
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
                    viewModel.endBatch()
                    navController.popBackStack(Routes.HOME, inclusive = false)
                },
                onNewRelease = {
                    viewModel.endBatch()
                    viewModel.resetForNewRelease()
                    navController.navigate(Routes.FORM) {
                        popUpTo(Routes.HOME)
                    }
                },
                onNextBatchModel = {
                    if (viewModel.advanceToNextBatchModel()) {
                        navController.navigate(Routes.TERMS) {
                            popUpTo(Routes.HOME)
                        }
                    }
                },
            )
        }
    }
}

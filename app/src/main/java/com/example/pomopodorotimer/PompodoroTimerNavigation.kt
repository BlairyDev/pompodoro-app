package com.example.pomopodorotimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pomopodorotimer.ui.screen.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeDestination : NavKey

@Composable
fun PompodoroTimerNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(HomeDestination)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeDestination> {
                HomeScreen(
                    onBackClicked = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}
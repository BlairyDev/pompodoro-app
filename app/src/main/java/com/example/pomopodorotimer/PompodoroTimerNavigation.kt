package com.example.pomopodorotimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pomopodorotimer.ui.screen.HomeScreen
import com.example.pomopodorotimer.ui.screen.LoginScreen
import kotlinx.serialization.Serializable


@Serializable
data object LoginDestination : NavKey

@Serializable
data object SignUpDestination : NavKey

@Serializable
data class HomeDestination(val userId: String) : NavKey

@Composable
fun PompodoroTimerNavigation(modifier: Modifier = Modifier) {
    val backStack = rememberNavBackStack(LoginDestination)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<LoginDestination> {
                LoginScreen(
                    onHomeClicked = { userId ->
                        backStack.add(HomeDestination(userId))
                    }
                )
            }
            entry<HomeDestination> {
                HomeScreen()
            }
        }
    )
}
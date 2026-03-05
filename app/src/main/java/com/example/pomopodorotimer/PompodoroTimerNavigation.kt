package com.example.pomopodorotimer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pomopodorotimer.ui.screen.HomeScreen
import com.example.pomopodorotimer.ui.screen.LoginScreen
import com.example.pomopodorotimer.ui.screen.RegisterScreen
import kotlinx.serialization.Serializable


@Serializable
data object LoginDestination : NavKey

@Serializable
data object RegisterDestination : NavKey

@Serializable
data object HomeDestination : NavKey

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
                    onHomeClicked = {
                        backStack.add(HomeDestination)
                    },
                    onRegisterClicked = {
                        backStack.add(RegisterDestination)
                    }
                )
            }
            entry<RegisterDestination>{
                RegisterScreen(
                    onHomeClicked = {
                        backStack.add(HomeDestination)
                    },
                    onBackClicked = {
                        backStack.removeLastOrNull()
                    }
                )
            }
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
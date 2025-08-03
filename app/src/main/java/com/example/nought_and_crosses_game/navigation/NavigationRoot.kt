package com.example.nought_and_crosses_game.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nought_and_crosses_game.ui.gameBoard.GameView
import com.example.nought_and_crosses_game.ui.hostAndJoin.HostGameView
import com.example.nought_and_crosses_game.ui.hostAndJoin.JoinGameView
import kotlinx.serialization.Serializable

@Serializable
data object HostGameUi : NavKey

@Serializable
data class JoinGameUi(val hasHost: Boolean, val hostName: String) : NavKey

@Serializable
data class GameUI(val hasGameStarted: Boolean, val playerNames: List<String>) : NavKey

@Composable
fun NavigationRoot() {
    val backStack = rememberNavBackStack(HostGameUi)
    NavDisplay(
        backStack = backStack, //At creation, each NavBackStackEntry starts in Lifecycle.State.INITIALIZED
        entryProvider = { key ->
            when (key) {
                is HostGameUi -> {
                    NavEntry(key) { // handle navigating back to previous page
                        HostGameView(onHostGameTapped = { hostName, hasHost ->
                            backStack.add(JoinGameUi(hostName = hostName, hasHost = hasHost))
                        })
                    }
                }

                is JoinGameUi -> {
                    NavEntry(key) {
                        JoinGameView(
                            onGameStarted = { playerNames ->
                                backStack.add(
                                    GameUI(
                                        hasGameStarted = key.hasHost,
                                        playerNames = playerNames
                                    )
                                )
                            },
                            hostName = key.hostName
                        )
                    }
                }

                is GameUI -> {
                    NavEntry(key) {
                        GameView(playerNames = key.playerNames)
                    }
                }

                else -> throw RuntimeException("")
            }
        }
    )
}

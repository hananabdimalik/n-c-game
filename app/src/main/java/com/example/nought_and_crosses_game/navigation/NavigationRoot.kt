package com.example.nought_and_crosses_game.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.nought_and_crosses_game.ui.gameBoard.GameView
import com.example.nought_and_crosses_game.ui.hostAndJoin.HostGameView
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class HostGameUi(val playerId: String) : NavKey

@Serializable
data class GameUI(
    val hasGameStarted: Boolean,
    val playerNames: List<String>,
    val gameSessionId: String?
) : NavKey

@Composable
fun NavigationRoot(context: Context) {
    val id = UUID.randomUUID().toString()
    val backStack = rememberNavBackStack(HostGameUi(id))
    NavDisplay(
        backStack = backStack, //At creation, each NavBackStackEntry starts in Lifecycle.State.INITIALIZED
        entryProvider = { key ->
            when (key) {
                is HostGameUi -> {
                    NavEntry(key) { // handle navigating back to previous page
                        HostGameView(
                            onHostGameTapped = { hasGameStarted, playerNames, gameSessionId ->
                                backStack.add(
                                    GameUI(
                                        hasGameStarted = hasGameStarted,
                                        playerNames = playerNames,
                                        gameSessionId = gameSessionId
                                    )
                                )
                            },
                            onJoinGameTapped = { hasGameStarted, playerNames, gameSessionId ->
                                backStack.add(
                                    GameUI(
                                        hasGameStarted = hasGameStarted,
                                        playerNames = playerNames,
                                        gameSessionId = gameSessionId
                                    )
                                )
                            },
                            context = context
                        )
                    }
                }

                is GameUI -> {
                    NavEntry(key) {
                        GameView(playerNames = key.playerNames, context = context)
                    }
                }

                else -> throw RuntimeException("")
            }
        }
    )
}

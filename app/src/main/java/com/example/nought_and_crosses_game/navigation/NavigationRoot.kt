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

@Serializable
object HostGameUi : NavKey

@Serializable
data class GameUI(
    val hasGameStarted: Boolean,
    val playerNames: String,
    val gameSessionId: String?,
    val playerId: String
) : NavKey

@Composable
fun NavigationRoot(context: Context) {
    val backStack = rememberNavBackStack(HostGameUi)
    NavDisplay(
        backStack = backStack, //At creation, each NavBackStackEntry starts in Lifecycle.State.INITIALIZED
        entryProvider = { key ->
            when (key) {
                is HostGameUi -> {
                    NavEntry(key) { // handle navigating back to previous page
                        HostGameView(
                            onHostGameTapped = { hasGameStarted, playerNames, gameSessionId, playerId ->
                                backStack.add(
                                    GameUI(
                                        hasGameStarted = hasGameStarted,
                                        playerNames = playerNames,
                                        gameSessionId = gameSessionId,
                                        playerId = playerId
                                    )
                                )
                            },
                            onJoinGameTapped = { hasGameStarted, playerNames, gameSessionId, playerId ->
                                backStack.add(
                                    GameUI(
                                        hasGameStarted = hasGameStarted,
                                        playerNames = playerNames,
                                        gameSessionId = gameSessionId,
                                        playerId = playerId
                                    )
                                )
                            },
                            context = context
                        )
                    }
                }

                is GameUI -> {
                    NavEntry(key) {
                        GameView(
                            playerName = key.playerNames,
                            context = context,
                            playerId = key.playerId,
                            sessionId = key.gameSessionId
                        )
                    }
                }

                else -> throw RuntimeException("")
            }
        }
    )
}

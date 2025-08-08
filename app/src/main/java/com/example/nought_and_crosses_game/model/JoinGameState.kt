package com.example.nought_and_crosses_game.model

import kotlinx.serialization.Serializable

@Serializable
data class JoinGameData(
    val gameSessionState: GameSessionState = GameSessionState.Ended,
    val gameSessionId: String? = null,
    val playerNames: List<String> = emptyList()
)

@Serializable
enum class GameSessionState {
    Waiting, Started, Ended
}

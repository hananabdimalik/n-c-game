package com.example.nought_and_crosses_game.model

import kotlinx.serialization.Serializable

enum class GamePieces {
    Nought, Cross, Unplayed
}

data class GameCell(val piece: GamePieces, val position: Int)

enum class GameState {
    Win, Draw, None
}

data class GameSession(
    val sessionId: String? = null,
    val players: List<Player> = emptyList(),
    val gameState: GameState = GameState.None,
    val currentPlayer: Player? = null,
    var gameSessionState: GameSessionState = GameSessionState.Ended,
    val error: String? = null,
)

data class Player(val name: String, val id: String, val gamePiece: GamePieces)

@Serializable
enum class GameSessionState {
    Waiting, Started, Ended
}

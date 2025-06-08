package com.example.nought_and_crosses_game.model

enum class GamePieces {
    Nought, Cross, Unplayed
}

data class GameCell(val piece: GamePieces, val position: Int)

enum class GameState {
    Win, Draw, None
}

data class GameSession(
    val players: List<Player> = emptyList(),
    val hasGameBegan: Boolean = false,
    val gameState: GameState = GameState.None
)

data class Player(val name: String, val id: String, val gamePiece: GamePieces)

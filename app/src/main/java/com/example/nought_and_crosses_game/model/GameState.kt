package com.example.nought_and_crosses_game.model

enum class GamePieces {
    Nought, Cross, Unplayed
}

data class GameCell(val piece: GamePieces, val position: Int)

enum class GameState {
    Win, Draw, None
}

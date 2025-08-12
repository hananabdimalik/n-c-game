package com.example.nought_and_crosses_game.repository

import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.Player

interface GameRepository {
    suspend fun hostGameSession(path: String, player: Player): GameSession

    suspend fun joinGameSession(path: String, player: Player): GameSession

    suspend fun loadGameState(path: String): GameSession

    suspend fun getBoardState(path: String): List<GameCell>

    suspend fun updateBoard(path: String, player: Player, position: Int): List<GameCell>

    suspend fun resetGameBoard(path: String): List<GameCell>

    suspend fun restartGame(path: String): GameSession
}
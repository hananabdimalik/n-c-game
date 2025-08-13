package com.example.nought_and_crosses_game.repository

import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.model.RestartGame

interface GameRepository {
    suspend fun hostGameSession(path: String, player: Player): GameSession

    suspend fun joinGameSession(path: String, player: Player): GameSession

    suspend fun loadGameState(path: String): GameSession

    suspend fun getGameBoard(path: String): List<GameCell>

    suspend fun updateBoard(path: String, player: Player, position: Int, sessionId: String): List<GameCell>

    suspend fun getGameState(path: String): GameSession

    suspend fun resetGameBoard(path: String): List<GameCell>

    suspend fun restartGameSession(path: String): RestartGame
}
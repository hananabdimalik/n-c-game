package com.example.nought_and_crosses_game.ui.gameBoard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.GameSessionState
import com.example.nought_and_crosses_game.model.GameState
import com.example.nought_and_crosses_game.repository.GameRepositoryImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel() : ViewModel() {
    private val repository = GameRepositoryImpl()

    data class State(
        val input: String? = null,
        val gameSession: GameSession = GameSession(),
        val gameCells: List<GameCell> = List(9) { GameCell(GamePieces.Unplayed, it) },
        val gameState: GameState = GameState.None,
        val gameSessionState: GameSessionState = GameSessionState.Ended
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        // only check gameState if game is still going
        if (state.value.gameState == GameState.None) {
            loadGameState()
            getGameBoard()
        }

        if (state.value.gameSession.gameState != GameState.None) {
            updateGameState()
        }
    }

    private fun loadGameState() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.loadGameState(path = loadGameStatePath)
                }.onSuccess { result ->
                    _state.update {
                        it.copy(
                            gameSession = result, gameSessionState = result.gameSessionState
                        )
                    }
                }.onFailure {
                    // handle failure -> by showing an error message
                }
                delay(5000)
            }
        }
    }

    private fun updateGameState() {
        _state.update { it.copy(gameSession = state.value.gameSession) }
    }

    private fun getGameBoard() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getBoardState(path = gameBoardPath)
                }.onSuccess { result ->
                    _state.update { it.copy(gameCells = result) }
                }.onFailure {
                    // handle failure
                    it.stackTrace
                }
                delay(500)
            }
        }
    }

    fun updateGrid(position: Int, playerId: String) {
        viewModelScope.launch {
            runCatching {
                val player = state.value.gameSession.players.first { it.id == playerId }
                repository.updateBoard(updateBoardPath, player, position)
            }
        }
    }

    fun onResetTapped() { // provide sessionId
        viewModelScope.launch {
            runCatching {
                repository.resetGameBoard(path = resetGamePath)
            }.onSuccess { result ->
                _state.update {
                    it.copy(
                        gameCells = result,
                        gameState = GameState.None,
                        gameSession = state.value.gameSession.copy(gameSessionState = GameSessionState.Ended) // double check that
                    )
                }
            }.onFailure {
                // handle failure
            }
        }
    }

    fun onStop() { // check rotation
        viewModelScope.launch {
            runCatching {
                repository.restartGame(restartGameSessionPath)
            }.onSuccess { result ->
                _state.update { it.copy(gameSession = result) }
            }.onFailure { }
        }
    }

    companion object {
        private const val loadGameStatePath = "loadGameSession"
        private const val gameBoardPath = "gameBoard"
        private const val updateBoardPath = "updateBoard"
        private const val resetGamePath = "resetGame"
        private const val restartGameSessionPath = "restartGameSession"
    }
}

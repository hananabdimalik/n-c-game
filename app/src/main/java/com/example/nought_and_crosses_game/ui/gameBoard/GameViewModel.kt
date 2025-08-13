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
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    init {
        if (state.value.gameSession.gameState == GameState.None) {
            loadGameState()
            getGameBoard()
            getGameState()
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
                            gameSession = result
                        )
                    }
                }.onFailure {
                    // handle failure -> by showing an error message
                }
                delay(1000)
            }
        }
    }

    private fun getGameState() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getGameState(getGameStatePath)
                }.onSuccess { result ->
                    _state.update { it.copy(gameSession = result) }
                }.onFailure {
                    it
                }
            }
            delay(1000)
        }
    }

    private fun getGameBoard() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getGameBoard(path = gameBoardPath)
                }.onSuccess { result ->
                    _state.update { it.copy(gameCells = result) }
                }.onFailure {
                    // handle failure
                    it.stackTrace
                }
                delay(1000)
            }
        }
    }

    fun updateGrid(position: Int, playerId: String, sessionId: String?) {
        viewModelScope.launch {
            runCatching {
                val player = state.value.gameSession.players.first { it.id == playerId }
                sessionId?.let {
                    repository.updateBoard(updateBoardPath, player, position, it)
                }
            }
        }
    }

    fun onResetTapped() {
        viewModelScope.launch {
            runCatching {
                repository.resetGameBoard(path = resetGamePath)
            }.onSuccess { result ->
                _state.update {
                    it.copy(
                        gameCells = result,
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
                repository.restartGameSession(restartGameSessionPath)
            }.onSuccess { result ->
                _state.update {
                    it.copy(
                        gameSession = result.gameSession,
                        gameCells = result.gameBoard
                    )
                }
            }.onFailure { }
        }
    }

    companion object {
        private const val loadGameStatePath = "loadGameSession"
        private const val gameBoardPath = "gameBoard"
        private const val updateBoardPath = "updateBoard"
        private const val resetGamePath = "resetGame"
        private const val restartGameSessionPath = "restartGameSession"
        private const val getGameStatePath = "getGameState"
    }
}

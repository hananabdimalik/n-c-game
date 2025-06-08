package com.example.nought_and_crosses_game.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.GameState
import com.example.nought_and_crosses_game.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    data class State(
        val input: String? = null,
        val gameSession: GameSession? = null,
        val gameCells: List<GameCell> = List(9) { GameCell(GamePieces.Unplayed, it) },
        val gameState: GameState = GameState.None
    )

    private val _state = MutableStateFlow(State())

    val state: StateFlow<State> = _state

    init {
        // only check gameState if game is still going
        if (state.value.gameState == GameState.None) {
            getGameBoard()
            getGameState()
            onJoinTapped() // only ask for username if game hasnt started
        }
        if (state.value.gameSession == null && state.value.gameState == GameState.None) {
            loadGameSession()
        }
    }

    private fun loadGameSession() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                runCatching {
                    repository.getGameSession()
                }.onSuccess { result ->
                    _state.update {
                        it.copy(
                            gameSession = result,
                        )
                    }
                }.onFailure {
                    // handle failure -> throw an exception and translate to a dialog
                }
            }
        }
    }

    private fun getGameBoard() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getBoardState()
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

    private fun getGameState() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getGameState()
                }.onSuccess { gameState ->
                    if (gameState != GameState.None) {
                        _state.update {
                            it.copy(
                                gameState = gameState,
                                gameSession = state.value.gameSession?.copy(hasGameBegan = false)
                            )
                        }
                    }
                }.onFailure {
                    // handle failure
                }
            }
            delay(500)
        }
    }

    fun updateGrid(position: Int) {
        viewModelScope.launch {
            runCatching {
                repository.updateBoard(position)
            }
        }
    }

    fun onResetTapped() {
        viewModelScope.launch {
            runCatching {
                repository.resetGameBoard()
            }.onSuccess { result ->
                _state.update {
                    it.copy(
                        gameCells = result,
                        gameState = GameState.None,
                        gameSession = state.value.gameSession?.copy(hasGameBegan = false)
                    )
                }
            }.onFailure {
                // handle failure
            }
        }
    }

    fun onJoinTapped() {
        viewModelScope.launch {
            runCatching {
                state.value.input?.let { repository.addUserToGame(it) }
            }.onSuccess { gameSession ->
                _state.update { it.copy(gameSession = gameSession, input = null) }
            }.onFailure {
                it.stackTrace
            }
        }
    }

    fun onValueChanged(input: String) {
        if (input.isNotEmpty()) {
            _state.update { it.copy(input = input.replaceFirstChar { letter -> letter.uppercase() }) } // capitalise first letter
        }
    }
}

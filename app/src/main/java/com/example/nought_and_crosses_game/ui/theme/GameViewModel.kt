package com.example.nought_and_crosses_game.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
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
        val gameCells: List<GameCell> = List(9) { GameCell(GamePieces.Unplayed, it) },
        val gameState: GameState = GameState.None,
        val hasGameEnded: Boolean = false
    )

    private val _state = MutableStateFlow(State())

    val state: StateFlow<State> = _state

    init {
        getGameBoard()
        // only check gameState if game is still going
        if (state.value.gameState == GameState.None) {
            getGameState()
        }
    }

    private fun getGameBoard() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getBoardState("gameBoard")
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

    private fun getGameState() {
        viewModelScope.launch {
            while (isActive) {
                runCatching {
                    repository.getGameState()
                }.onSuccess { gameState ->
                    if (gameState != GameState.None) {
                        _state.update { it.copy(hasGameEnded = true, gameState = gameState) }
                    }
                }.onFailure {
                    // handle failure
                }
            }
            delay(1000)
        }
    }

    fun updateGrid(position: Int) {
        viewModelScope.launch {
            runCatching {
                repository.updateBoard(position)
            }
        }
    }
}

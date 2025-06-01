package com.example.nought_and_crosses_game.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    data class State(val gameCells: List<GameCell> = List(9) { GameCell(GamePieces.Unplayed, it) })

    private val _state = MutableStateFlow(State())

    val state: StateFlow<State> = _state

    fun getBoard() {
        viewModelScope.launch {
            runCatching {
                repository.getBoardState("gameBoard")
            }.onSuccess { result ->
                _state.update { it.copy(gameCells = result) }
            }.onFailure {
                it.stackTrace
            }
        }
    }

    fun updateGrid(position: Int) {
        viewModelScope.launch {
            runCatching {
                repository.updateBoard(position.toString())
            }
        }
    }
}

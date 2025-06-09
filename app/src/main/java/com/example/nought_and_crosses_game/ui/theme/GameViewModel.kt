package com.example.nought_and_crosses_game.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.GameState
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.repository.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    data class State(
        val input: String? = null,
        val gameSession: GameSession? = null,
        val gameCells: List<GameCell> = List(9) { GameCell(GamePieces.Unplayed, it) },
        val gameState: GameState = GameState.None
    )

    private val _state = MutableStateFlow(State())

    val state: StateFlow<State> = _state
    private val id = UUID.randomUUID().toString()

    init {
        // only check gameState if game is still going
        if (state.value.gameState == GameState.None) {
            getGameBoard()
        }

        if (state.value.gameSession?.hasGameBegan == false) {
            onJoinTapped() // only ask for username if game hasnt started
        }

        if (state.value.gameSession == null) { // double check this
            loadGameSession()
        }

        if (state.value.gameSession?.gameState != GameState.None) {
            updateGameState()
        }
    }

    private fun loadGameSession() {
        viewModelScope.launch {
            while (isActive) {
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

    private fun updateGameState() {
        _state.update { it.copy(gameSession = state.value.gameSession) }
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

    fun updateGrid(position: Int) {
        viewModelScope.launch {
            runCatching {
                // bug -> new id made when app is restarted. Should use one from the server -> if lifecycle has ended -> call restart
                val player = state.value.gameSession?.players?.first { it.id == id }
                if (player != null) {
                    repository.updateBoard(player, position)
                }
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
    } // should this be tappable if there's one player?

    fun onJoinTapped() {
        viewModelScope.launch {
            runCatching {
                state.value.input?.let {
                    val player =
                        Player(name = it, id = id, gamePiece = GamePieces.Unplayed)
                    state.value.input?.let { repository.addPlayer(player) }
                }
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

    fun onResume(){
        onResetTapped()
    }
}

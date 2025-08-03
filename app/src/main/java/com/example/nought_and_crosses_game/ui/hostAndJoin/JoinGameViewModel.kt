package com.example.nought_and_crosses_game.ui.hostAndJoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.model.GameSession
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class JoinGameViewModel : ViewModel() {
    private val repository = GameRepository()
    private val id = UUID.randomUUID().toString()

    data class JoinGameState(
        val players: List<String> = emptyList(),
        val input: String? = null,
        val hasGameStarted: Boolean = false,
        val gameSession: GameSession? = null
    )

    private val _state = MutableStateFlow(JoinGameState())
    val state: StateFlow<JoinGameState> = _state
    fun onValueChanged(input: String) {
        _state.update {
            it.copy(input = input)
        }
    }

    fun updatePlayers(players: List<String>) {
        _state.update {
            it.copy(players = players)
        }
    }

    fun onGameJoined() {
        viewModelScope.launch {
            runCatching {
                state.value.input?.let {
                    val player = Player(name = it, id = id, gamePiece = GamePieces.Unplayed)
                    state.value.input?.let { repository.joinGameSession(player) }
                }
            }.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            hasGameStarted = result == "Started",
                            players = listOf()
                        )
                    }
                },
                onFailure = {
                    it.stackTrace
                }
            )
        }
    }
}
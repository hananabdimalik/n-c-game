package com.example.nought_and_crosses_game.ui.hostAndJoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GamePieces.Unplayed
import com.example.nought_and_crosses_game.model.GameSessionState
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HostAndJoinGameViewModel : ViewModel() {
    private val gameRepository = GameRepository()
    private val id = UUID.randomUUID().toString()

    data class HostState(
        val gameSessionId: String? = null,
        val hasHost: Boolean = false,
        val input: String? = null,
        val hasGameStarted: Boolean = false,
        val errorMessage: String? = null,
        val playerNames: List<String> = emptyList()
    )

    private val _state = MutableStateFlow(HostState())

    val state: StateFlow<HostState> = _state

    fun onValueChanged(input: String) {
        _state.update { it.copy(input = input) }
    }

    fun onHostGame() {
        val playerName = state.value.input
        viewModelScope.launch {
            runCatching {
                gameRepository.hostGameSession(Player(playerName ?: "", id, Unplayed))
            }.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            hasHost = true,
                            gameSessionId = result.sessionId,
                            playerNames = result.players.map { player -> player.name }
                        )
                    }
                },
                onFailure = { it } // handle no connection error
            )
        }
    }

    fun joinGame() {
        val player = Player(state.value.input.orEmpty(), id, Unplayed)

        viewModelScope.launch {
            runCatching {
                gameRepository.joinGameSession(player)
            }.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            hasGameStarted = result.gameSessionState == GameSessionState.Started,
                            playerNames = result.playerNames,
                            gameSessionId = result.gameSessionId
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(errorMessage = "Please host a game session") }
                }
            )
        }
    }

    fun removeErrorMessage() {
        _state.update { it.copy(errorMessage = null) }
    }
}

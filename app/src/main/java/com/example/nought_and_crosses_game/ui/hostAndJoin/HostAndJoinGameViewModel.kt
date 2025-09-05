package com.example.nought_and_crosses_game.ui.hostAndJoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GamePieces.Unplayed
import com.example.nought_and_crosses_game.model.GameSessionState
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.repository.GameRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.util.UUID

class HostAndJoinGameViewModel : ViewModel() {
    private val gameRepository = GameRepositoryImpl()
    private val id = UUID.randomUUID().toString()

    companion object {
        private const val hostGamePath = "hostSession"
        private const val joinGamePath = "joinSession"
    }

    data class HostState(
        val gameSessionId: String? = null,
        val hasHost: Boolean = false,
        val input: String? = null,
        val hasGameStarted: Boolean = false,
        val errorMessage: String? = null,
        val playerNames: String = "",
        val playerId: String = ""
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
                gameRepository.hostGameSession(
                    path = hostGamePath,
                    player = Player(playerName ?: "", id, Unplayed)
                )
            }.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            hasHost = true,
                            gameSessionId = result.sessionId,
                            playerNames = result.players.first().name,
                            playerId = id
                        )
                    }
                },
                onFailure = { error ->
                    if (error is ConnectException) {
                        _state.update { it.copy(errorMessage = "Server error - please try again") }
                    } else {
                        _state.update { it.copy(errorMessage = "Unable to host a game - please try again") }
                    }
                }
            )
        }
    }

    fun joinGame() {
        val player = Player(state.value.input.orEmpty(), id, Unplayed)

        viewModelScope.launch {
            runCatching {
                gameRepository.joinGameSession(path = joinGamePath, player = player)
            }.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            hasGameStarted = result.gameSessionState == GameSessionState.Started,
                            playerNames = result.players.last().name,
                            gameSessionId = result.sessionId,
                            playerId = id
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

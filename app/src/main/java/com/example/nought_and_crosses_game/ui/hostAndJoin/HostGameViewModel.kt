package com.example.nought_and_crosses_game.ui.hostAndJoin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nought_and_crosses_game.model.GamePieces.Unplayed
import com.example.nought_and_crosses_game.model.Player
import com.example.nought_and_crosses_game.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class HostGameViewModel : ViewModel() {

    private val gameRepository = GameRepository()
    private val id = UUID.randomUUID().toString()

    data class HostState(
        val hasHost: Boolean = false,
        val playerNameInput: String? = null,
        val player: Player? = null,
    )

    private val _state = MutableStateFlow(HostState())

    val state: StateFlow<HostState> = _state

    fun onValueChanged(input: String) {
        _state.update { it.copy(playerNameInput = input) }
    }

    fun onHostGame() {
        val playerName = state.value.playerNameInput
        viewModelScope.launch {
            runCatching {
                gameRepository.hostGameSession(Player(playerName ?: "", id, Unplayed))
            }.fold(
                onSuccess = { result ->
                    _state.update {
                        it.copy(hasHost = true, player = result.players.first())
                    }
                },
                onFailure = { throw it } // handle no connection error
            )
        }
    }
}

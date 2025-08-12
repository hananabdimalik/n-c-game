package com.example.nought_and_crosses_game.ui.hostAndJoin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme

@Composable
fun HostGameView(
    context: Context?,
    viewModel: HostAndJoinGameViewModel = viewModel(),
    onHostGameTapped: (Boolean, String, String, String) -> Unit,
    onJoinGameTapped: (Boolean, String, String, String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 40.dp)
            .padding(
                vertical = 100.dp,
                horizontal = 20.dp
            ),

        ) {
        TextFieldView(
            input = state.value.input,
            onValueChanged = viewModel::onValueChanged
        )

        Button(
            enabled = !state.value.input.isNullOrEmpty(),
            onClick = viewModel::onHostGame,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(vertical = 10.dp)

        ) {
            Text(text = "Host Game")
        }

        Button(
            enabled = !state.value.input.isNullOrEmpty(),
            onClick = viewModel::joinGame,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = "Join Game")
        }

        if (state.value.hasHost) {
            state.value.input?.let { hostName ->
                onHostGameTapped(
                    state.value.hasGameStarted,
                    state.value.playerNames,
                    state.value.gameSessionId.orEmpty(),
                    state.value.playerId
                )
            }
        }

        if (state.value.hasGameStarted) {
            state.value.gameSessionId?.let { gameId ->
                onJoinGameTapped(
                    state.value.hasGameStarted,
                    state.value.playerNames,
                    gameId,
                    state.value.playerId
                )
            }
        }

        if (state.value.errorMessage != null) {
            val toast = Toast.makeText(context, state.value.errorMessage, Toast.LENGTH_SHORT)
            toast.show()
            viewModel.removeErrorMessage()
        }
    }
}

@Preview
@Composable
fun HostGamePreview() {
    NoughtandcrossesgameTheme {
        HostGameView(
            onHostGameTapped = { _, _, _ , None-> },
            onJoinGameTapped = { _, _, _ , None-> },
            context = null,
        )
    }
}

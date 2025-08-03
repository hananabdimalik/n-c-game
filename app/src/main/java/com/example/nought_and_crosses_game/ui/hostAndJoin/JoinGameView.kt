package com.example.nought_and_crosses_game.ui.hostAndJoin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun JoinGameView(
    viewModel: JoinGameViewModel = viewModel(),
    onGameStarted: (List<String>) -> Unit,
    hostName: String
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Column(modifier = Modifier.padding(50.dp)) {

        TextFieldView(state.value.input, viewModel::onValueChanged)
        Spacer(modifier = Modifier.padding(vertical = 20.dp))

        val hasNewPlayerName = !state.value.input.isNullOrEmpty()
        Row(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Text("$hostName VS ")
            if (hasNewPlayerName) {
                Text("${state.value.input}")
            }
        }

        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        Button(
            enabled = !state.value.input.isNullOrEmpty(),
            onClick = viewModel::onGameJoined,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text("Join Game")
        }

        if (state.value.hasGameStarted) {
            viewModel.updatePlayers(listOf(hostName, state.value.input.orEmpty()))
            onGameStarted(state.value.players)
        }
    }
}

@Preview
@Composable
fun JoinGameViewPreview() {
    NoughtandcrossesgameTheme {
        JoinGameView(hostName = "Bar", onGameStarted = { _ -> })
    }
}

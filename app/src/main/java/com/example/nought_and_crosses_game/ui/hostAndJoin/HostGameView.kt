package com.example.nought_and_crosses_game.ui.hostAndJoin

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
    viewModel: HostGameViewModel = viewModel(),
    onHostGameTapped: (String, Boolean) -> Unit
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
            input = state.value.playerNameInput,
            onValueChanged = viewModel::onValueChanged
        )

        Button(
            enabled = !state.value.playerNameInput.isNullOrEmpty(),
            onClick = viewModel::onHostGame,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .padding(vertical = 30.dp)

        ) {
            Text(text = "Host Game")
        }

        if (state.value.hasHost) {
            state.value.playerNameInput?.let { hostName ->
                onHostGameTapped(hostName, state.value.hasHost)
            }
        }
    }
}

@Preview
@Composable
fun HostGamePreview() {
    NoughtandcrossesgameTheme {
        HostGameView(onHostGameTapped = { _, _ -> })
    }
}

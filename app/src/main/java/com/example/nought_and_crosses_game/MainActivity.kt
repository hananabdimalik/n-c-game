package com.example.nought_and_crosses_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import com.example.nought_and_crosses_game.ui.theme.GameViewModel
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme
import com.example.nought_and_crosses_game.ui.theme.ui.GameGrid

class MainActivity : ComponentActivity() {
    val viewModel = GameViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoughtandcrossesgameTheme {
                val state = viewModel.state.collectAsState()
                GameGrid(
                    board = state.value.gameCells,
                    onCellTapped = viewModel::updateGrid,
                    onResetTapped = viewModel::onResetTapped,
                    onValueChanged = viewModel::onValueChanged,
                    onJoinTapped = viewModel::onJoinTapped,
                    state = state.value
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }
}


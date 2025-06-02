package com.example.nought_and_crosses_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.example.nought_and_crosses_game.repository.GameRepository
import com.example.nought_and_crosses_game.ui.theme.GameViewModel
import com.example.nought_and_crosses_game.ui.theme.GameViewModelFactory
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme
import com.example.nought_and_crosses_game.ui.theme.ui.GameGrid

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModelFactory = GameViewModelFactory(GameRepository())
            val viewModel =
                ViewModelProvider(owner = this, viewModelFactory)[GameViewModel::class.java]
            NoughtandcrossesgameTheme {
                val state = viewModel.state.collectAsState()
                GameGrid(state.value.gameCells, viewModel::updateGrid, state.value.hasGameEnded)
            }
        }
    }
}


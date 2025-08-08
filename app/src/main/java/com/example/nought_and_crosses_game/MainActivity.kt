package com.example.nought_and_crosses_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.nought_and_crosses_game.navigation.NavigationRoot
import com.example.nought_and_crosses_game.ui.gameBoard.GameViewModel
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme

class MainActivity : ComponentActivity() {
    private val viewModel = GameViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoughtandcrossesgameTheme {
                NavigationRoot(this.applicationContext)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }
}


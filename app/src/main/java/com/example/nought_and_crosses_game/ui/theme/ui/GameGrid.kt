package com.example.nought_and_crosses_game.ui.theme.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nought_and_crosses_game.R
import com.example.nought_and_crosses_game.model.GameCell
import com.example.nought_and_crosses_game.model.GamePieces
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme

@Composable
fun GameGrid(board: List<GameCell>) {
    val gridSize = 9

    Box(
        modifier = Modifier
            .padding(top = 100.dp, start = 40.dp, end = 60.dp)
            .size(height = 300.dp, width = 600.dp)
    ) {
        VerticalDivider(
            modifier = Modifier
                .size(300.dp)
                .padding(start = 100.dp)
        )
        VerticalDivider(
            modifier = Modifier
                .size(300.dp)
                .padding(start = 200.dp)
        )
        HorizontalDivider(modifier = Modifier.padding(top = 100.dp))
        HorizontalDivider(modifier = Modifier.padding(top = 200.dp))

        var x = 0
        var y = 0
        for (i in 0 until gridSize) {
            GridCell(x, y, board[i].piece)
            val numberOfRow = i % 3
            if (numberOfRow == 2) {
                x = 0
                y += 100
            } else {
                x += 100
            }
        }
    }

}

@Composable
fun GridCell(x: Int, y: Int, gamePiece: GamePieces) {
    val density = LocalDensity.current
    val xInDp = with(density) { x.dp }
    val yInDp = with(density) { y.dp }
    val image = when (gamePiece) {
        GamePieces.Nought -> R.drawable.ic_nought
        GamePieces.Cross -> R.drawable.ic_cross
        else -> null
    }
    Box(modifier = Modifier.size(80.dp)) {
        image?.let {
            Image(
                painter = painterResource(it),
                contentDescription = null,
                modifier = Modifier.offset(x = xInDp, y = yInDp)
            )
        }
    }
}

@Preview
@Composable
fun GameGridPreview() {
    NoughtandcrossesgameTheme {
        GameGrid(List(9) { GameCell(GamePieces.Nought, it) })
    }
}

@Preview
@Composable
fun PreviewGridCell() {
    NoughtandcrossesgameTheme {
        GridCell(0, 0, GamePieces.Nought)
    }
}

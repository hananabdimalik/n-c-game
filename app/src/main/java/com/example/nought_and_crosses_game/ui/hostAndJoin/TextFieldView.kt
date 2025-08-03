package com.example.nought_and_crosses_game.ui.hostAndJoin

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.nought_and_crosses_game.ui.theme.NoughtandcrossesgameTheme

@Composable
fun TextFieldView(input: String?, onValueChanged: (String) -> Unit) {

    TextField(
        value = input ?: "",
        onValueChange = onValueChanged
    )
}

@Preview
@Composable
fun PreviewTextFieldView() {
    NoughtandcrossesgameTheme {
        TextFieldView("Nemo") { }
    }
}

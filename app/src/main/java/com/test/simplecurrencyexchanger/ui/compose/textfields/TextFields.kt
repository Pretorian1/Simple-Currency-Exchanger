package com.test.simplecurrencyexchanger.ui.compose.textfields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SimpleDebouncingOutlinedTextField(
    text: String = "",
    singleLine: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    onInputChanged: (Double) -> Unit
) {
    var textState by remember { mutableStateOf(text) }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .padding(2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor
        ),
        singleLine = singleLine,
        value = textState,
        onValueChange = { input ->
            if (input.all { it.isDigit() || it == '.' }) textState = input
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
    )

    LaunchedEffect(key1 = textState) {
        onInputChanged(if (textState.isEmpty()) 0.0 else textState.toDouble())
    }
}
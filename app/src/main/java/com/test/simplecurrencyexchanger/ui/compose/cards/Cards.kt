package com.test.simplecurrencyexchanger.ui.compose.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.lang.String.format

@SuppressLint("DefaultLocale")
@Composable
fun CurrencyItemCard(currency: String, balance: Double, action: (String) -> Unit) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { action(currency) }
            .padding(
                10.dp,
            )
            .defaultMinSize(minWidth = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = currency,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = format("%.2f", balance),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CurrencyItemCardPreview() {
    CurrencyItemCard("GBP", 5389.234141) { }
}
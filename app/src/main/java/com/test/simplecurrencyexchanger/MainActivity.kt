package com.test.simplecurrencyexchanger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.test.simplecurrencyexchanger.screens.exchangecurrency.ExchangeCurrencyScreen
import com.test.simplecurrencyexchanger.ui.theme.SimpleCurrencyExchangerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleCurrencyExchangerTheme {
                ExchangeCurrencyScreen()
            }
        }
    }
}
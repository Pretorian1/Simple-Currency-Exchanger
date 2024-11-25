package com.test.core.domain.models

sealed class CurrencyExchangeResult {
    data class Success(
        val fromCurrency: String,
        val fromCurrencyValue: Double,
        val toCurrency: String,
        val toCurrencyValue: Double,
        val commission: Double,
    ) : CurrencyExchangeResult()

    data class Failed(
        val fromCurrency: String,
        val fromCurrencyValue: Double,
        val toCurrency: String,
        val toCurrencyValue: Double,
        val commission: Double,
    ) : CurrencyExchangeResult()
}
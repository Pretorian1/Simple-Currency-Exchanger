package com.test.core.domain.models

data class DataForCurrencyExchange(
    val fromCurrency: String,
    val fromCurrencyValue: Double,
    val fromCurrencyRate: Double,
    val toCurrency: String,
    val toCurrencyRate: Double
)
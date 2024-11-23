package com.test.core.domain.models

data class CurrencyExchangeRates(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)

package com.test.core.data.models

import com.google.gson.annotations.SerializedName

data class CurrencyExchangeRatesResult(
    @SerializedName("base") val base: String,
    @SerializedName("date") val date: String,
    @SerializedName("rates") val rates: Map<String, Double>
)

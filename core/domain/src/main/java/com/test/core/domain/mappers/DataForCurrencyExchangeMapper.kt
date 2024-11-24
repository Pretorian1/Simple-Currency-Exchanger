package com.test.core.domain.mappers

import com.test.core.domain.models.CurrencyExchangeRates
import com.test.core.domain.models.DataForCurrencyExchange
import javax.inject.Inject

private const val BASE_RATE = 1.0

class DataForCurrencyExchangeMapper @Inject constructor() {

    fun map(
        fromCurrency: String,
        fromCurrencyValue: Double,
        toCurrency: String,
        currencyExchangeRates: CurrencyExchangeRates
    ) =
        DataForCurrencyExchange(
            fromCurrency = fromCurrency,
            fromCurrencyValue = fromCurrencyValue,
            fromCurrencyRate = if (fromCurrency == currencyExchangeRates.base) BASE_RATE
            else currencyExchangeRates.rates[fromCurrency]!!,
            toCurrency = toCurrency,
            toCurrencyRate = if (toCurrency == currencyExchangeRates.base) BASE_RATE else currencyExchangeRates.rates[toCurrency]!!
        )
}
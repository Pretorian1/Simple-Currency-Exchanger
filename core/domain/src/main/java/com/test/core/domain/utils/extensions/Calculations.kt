package com.test.core.domain.utils.extensions

import com.test.core.domain.models.DataForCurrencyExchange

private const val PERCENT = 100.0

internal fun DataForCurrencyExchange.calculateExchange(commissionInPercents: Double = 0.0): CalculationsExchangeResult {
    val commissionValue: Double
    val fromValue: Double
    val toValue: Double
    with(this) {
        toValue = (fromCurrencyValue / fromCurrencyRate) * toCurrencyRate
        commissionValue = fromCurrencyValue * (commissionInPercents / PERCENT)
        fromValue = fromCurrencyValue + commissionValue
    }

    return CalculationsExchangeResult(
        fromValue = fromValue,
        toValue = toValue,
        commissionValue = commissionValue
    )
}

internal data class CalculationsExchangeResult(
    val fromValue: Double,
    val toValue: Double,
    val commissionValue: Double
)
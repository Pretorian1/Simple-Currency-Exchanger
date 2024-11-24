package com.test.core.domain.usecases

import com.test.core.domain.mappers.DataForCurrencyExchangeMapper
import com.test.core.domain.models.CurrencyExchangeRates
import com.test.core.domain.models.CurrencyExchangeResult
import com.test.core.domain.repositories.AppPreferenceRepository
import com.test.core.domain.utils.extensions.calculateExchange
import javax.inject.Inject

private const val FREE_COMMISSION_FOR_BASE = 200.0
private const val FIRST_COMMISSION_CHANGE = 5
private const val BASE_COMMISSION_IN_PERCENTS = 0.7
private const val EVERY_CONVERSION_FREE_VALUE = 15

class CurrencyExchangeUseCase @Inject constructor(
    private val repo: AppPreferenceRepository, private val mapper: DataForCurrencyExchangeMapper
) {

    suspend operator fun invoke(
        fromCurrency: String,
        fromCurrencyValue: Double,
        toCurrency: String,
        currencyExchangeRates: CurrencyExchangeRates
    ): CurrencyExchangeResult {
        val dataForCurrencyExchange =
            mapper.map(fromCurrency, fromCurrencyValue, toCurrency, currencyExchangeRates)
        val userData = repo.getUserData()!!
        val commission = calculateCommissionRate(
            userData.exchangeCounter,
            dataForCurrencyExchange.fromCurrencyValue,
            dataForCurrencyExchange.fromCurrencyRate
        )
        val calculation = dataForCurrencyExchange.calculateExchange(commission)
        return if (calculation.fromValue < userData.balance[fromCurrency]!!) {
            CurrencyExchangeResult.Success(
                fromCurrency =
                fromCurrency, fromCurrencyValue = calculation.fromValue,
                toCurrency = toCurrency, toCurrencyValue = calculation.toValue,
                commission = calculation.commissionValue
            )
        } else {
            CurrencyExchangeResult.Failed(expectedValue = calculation.fromValue)
        }
    }
}

fun calculateCommissionRate(
    exchangeCounter: Int,
    fromCurrencyValue: Double,
    fromCurrencyRate: Double
) =
    when {
        fromCurrencyValue / fromCurrencyRate > FREE_COMMISSION_FOR_BASE -> 0.0
        exchangeCounter <= FIRST_COMMISSION_CHANGE -> 0.0
        exchangeCounter % EVERY_CONVERSION_FREE_VALUE == 0 -> 0.0
        else -> BASE_COMMISSION_IN_PERCENTS
    }
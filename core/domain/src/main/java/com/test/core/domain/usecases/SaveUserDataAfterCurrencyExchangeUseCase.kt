package com.test.core.domain.usecases

import com.test.core.domain.models.UserData
import com.test.core.domain.repositories.AppPreferenceRepository
import javax.inject.Inject

class SaveUserDataAfterCurrencyExchangeUseCase @Inject constructor(private val repo: AppPreferenceRepository) {

    suspend operator fun invoke(
        fromCurrency: String,
        fromCurrencyValue: Double,
        toCurrency: String,
        toCurrencyValue: Double
    ) {
        val userData = repo.getUserData()!!
        repo.saveUserData(
            prepareUserData(
                fromCurrency = fromCurrency, fromCurrencyValue = fromCurrencyValue,
                toCurrency = toCurrency, toCurrencyValue = toCurrencyValue, userData = userData
            )
        )

    }
}

fun prepareUserData(
    fromCurrency: String,
    fromCurrencyValue: Double,
    toCurrency: String,
    toCurrencyValue: Double,
    userData: UserData
): UserData {
    val exchangeCounter = userData.exchangeCounter.inc()
    val balance = userData.balance.toMutableMap()
    balance[fromCurrency] = balance[fromCurrency]!!.minus(fromCurrencyValue)
    balance[toCurrency] =
        if (balance.containsKey(toCurrency)) balance[toCurrency]!!.plus(toCurrencyValue) else toCurrencyValue
    return userData.copy(
        exchangeCounter = exchangeCounter,
        balance = balance
    )
}
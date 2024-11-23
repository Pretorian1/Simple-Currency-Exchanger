package com.test.core.domain.repositories

import com.test.core.domain.models.CurrencyExchangeRates

interface TaskRepository {

    suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates
}
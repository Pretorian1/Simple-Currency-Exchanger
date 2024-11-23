package com.test.core.data.repositories

import com.test.core.data.network.qualifiers.BaseRetrofit
import com.test.core.data.network.services.TasksService
import com.test.core.domain.models.CurrencyExchangeRates
import com.test.core.domain.repositories.TaskRepository
import retrofit2.Retrofit
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    @BaseRetrofit retrofit: Retrofit,
) : TaskRepository, BaseRetrofitRepository() {

    private val service = retrofit.create(TasksService::class.java)
    override suspend fun getCurrencyExchangeRates(): CurrencyExchangeRates {
        val result = callApi { service.retrieveAssetTypes() }
        return CurrencyExchangeRates(
            base = result.base,
            date = result.date,
            rates = result.rates
        )
    }
}
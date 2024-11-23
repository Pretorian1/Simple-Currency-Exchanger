package com.test.core.data.network.services

import com.test.core.data.models.CurrencyExchangeRatesResult
import retrofit2.Response
import retrofit2.http.GET

interface TasksService {
    @GET("/tasks/api/currency-exchange-rates")
    suspend fun retrieveAssetTypes(): Response<CurrencyExchangeRatesResult>
}
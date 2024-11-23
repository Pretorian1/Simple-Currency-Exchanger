package com.test.core.domain.usecases

import com.test.core.domain.repositories.TaskRepository
import javax.inject.Inject

class GetCurrencyExchangeRatesUseCase @Inject constructor(private val repo: TaskRepository) {
    suspend operator fun invoke() = repo.getCurrencyExchangeRates()
}
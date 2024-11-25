package com.test.core.domain.usecases

import com.test.core.domain.models.UserData
import com.test.core.domain.repositories.AppPreferenceRepository
import javax.inject.Inject

private const val INITIALIZATION_BALANCE = 1000.0

class GetUserDataOrItFirstInitializationUseCase @Inject constructor(private val repo: AppPreferenceRepository) {

    suspend operator fun invoke(baseCurrency: String) =
        repo.getUserData() ?: firstUserDataInitialization(baseCurrency)

    private suspend fun firstUserDataInitialization(baseCurrency: String): UserData {
        val userData = UserData(
            exchangeCounter = 0,
            balance = mapOf(baseCurrency to INITIALIZATION_BALANCE)
        )
        repo.saveUserData(userData)
        return userData
    }
}
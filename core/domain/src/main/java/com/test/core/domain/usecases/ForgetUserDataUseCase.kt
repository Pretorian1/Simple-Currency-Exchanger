package com.test.core.domain.usecases

import com.test.core.domain.repositories.AppPreferenceRepository
import javax.inject.Inject

class ForgetUserDataUseCase @Inject constructor(private val repo: AppPreferenceRepository) {

    suspend operator fun invoke() = repo.forgetUserData()
}
package com.test.core.domain.repositories

import com.test.core.domain.models.UserData

interface AppPreferenceRepository {

    suspend fun saveUserData(data: UserData)

    suspend fun getUserData(): UserData?

    suspend fun forgetUserData()
}
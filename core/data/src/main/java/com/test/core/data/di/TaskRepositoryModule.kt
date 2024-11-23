package com.test.core.data.di

import com.test.core.data.repositories.TaskRepositoryImpl
import com.test.core.domain.repositories.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TaskRepositoryModule {

    @Binds
    fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

}
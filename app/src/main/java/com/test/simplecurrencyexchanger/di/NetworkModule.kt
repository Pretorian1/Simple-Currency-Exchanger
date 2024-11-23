package com.test.simplecurrencyexchanger.di

import com.test.core.data.network.qualifiers.BaseRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://developers.paysera.com"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @BaseRetrofit
    @Provides
    @Singleton
    fun provideNetworkClient(): Retrofit =
        createRetrofit(BASE_URL)
}

private fun createRetrofit(baseUrl: String) = Retrofit.Builder()
    .baseUrl(baseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
package com.test.simplecurrencyexchanger.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.core.domain.usecases.GetCurrencyExchangeRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TestViewModel @Inject constructor(
    private val useCase: GetCurrencyExchangeRatesUseCase
) : ViewModel() {

    init {//todo rework
        viewModelScope.launch {
            try {
                val result = useCase()
                println(result)
            } catch (e: Exception) {
                println(e.message)
            }
        }

    }
}
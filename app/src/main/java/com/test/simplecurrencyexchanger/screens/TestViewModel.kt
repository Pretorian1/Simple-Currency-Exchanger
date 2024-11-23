package com.test.simplecurrencyexchanger.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.core.domain.usecases.GetCurrencyExchangeRatesUseCase
import com.test.core.domain.usecases.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TestViewModel @Inject constructor(
    private val useCase: GetCurrencyExchangeRatesUseCase,
    private val userDataUseCase: GetUserDataUseCase,
) : ViewModel() {

    init {//todo rework
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = useCase()
                println(result)
                val user = userDataUseCase(result.base)
                println(user)
            } catch (e: Exception) {
                println(e.message)
            }
        }

    }
}
package com.test.simplecurrencyexchanger.screens.exchangecurrency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.core.domain.models.CurrencyExchangeRates
import com.test.core.domain.usecases.CurrencyExchangeUseCase
import com.test.core.domain.usecases.ForgetUserDataUseCase
import com.test.core.domain.usecases.GetCurrencyExchangeRatesUseCase
import com.test.core.domain.usecases.GetUserDataUseCase
import com.test.core.domain.usecases.SaveUserDataAfterCurrencyExchangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

private const val ANR_TIMEOUT = 5000L
private const val ONE_SECOND_IN_MILLISECONDS = 1000L

@HiltViewModel
class ExchangeCurrencyViewModel @Inject constructor(
    private val getCurrencyExchangeRatesUseCase: GetCurrencyExchangeRatesUseCase,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val currencyExchangeUseCase: CurrencyExchangeUseCase,
    private val saveUserDataAfterCurrencyExchangeUseCase: SaveUserDataAfterCurrencyExchangeUseCase,
    private val forgetUserDataUseCase: ForgetUserDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExchangeCurrencyContract.UIState())
    val uiState: StateFlow<ExchangeCurrencyContract.UIState> = _uiState.onStart {
        firstUserDataInitialization()
        collectCurrencyAmount()
    }.stateIn(
        viewModelScope + Dispatchers.IO,
        SharingStarted.WhileSubscribed(ANR_TIMEOUT),
        ExchangeCurrencyContract.UIState(isLoading = true)
    )

    private val _userInput = MutableStateFlow(0.0)

    private val userInput: StateFlow<Double> = _userInput

    private val _events = Channel<ExchangeCurrencyContract.Event>()
    val events = _events.receiveAsFlow()

    private var selectedCurrencyForSold: String? = null
    private var selectedCurrencyForBuy: String? = null
    private var selectedAmountForSold: Double? = null

    private var currencyExchangeRates: CurrencyExchangeRates? = null


    fun onCurrencyBalanceClicked(currency: String) {
        selectedCurrencyForSold = currency
        println(currency)
    }

    fun onCurrencyAmountChanged(amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _userInput.emit(amount)
        }
    }

    fun onCurrencyToByTypeChanged(currency: String) {
        selectedCurrencyForBuy = currency
        println(currency)
    }

    fun onForgetUserDataClicked() {
        viewModelScope.launch {
            _events.send(ExchangeCurrencyContract.Event.ForgotUserData)
        }
    }

    fun forgetUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { state ->
                state.copy(isLoading = true)
            }
            try {
                forgetUserDataUseCase()
                firstUserDataInitialization()
                selectedCurrencyForSold = null
                selectedCurrencyForBuy = null
            } finally {
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        }
    }


    private suspend fun firstUserDataInitialization() {
        try {
            currencyExchangeRates = getCurrencyExchangeRatesUseCase()
            println(currencyExchangeRates)
            val user = getUserDataUseCase(currencyExchangeRates!!.base)
            println(user)
            _uiState.update { state ->
                state.copy(
                    balanceItems = user.balance.map {
                        CurrencyBalance(
                            currency = it.key,
                            balance = it.value
                        )
                    },
                    availableCurrenciesForExchanging = currencyExchangeRates!!.rates.keys.toList()
                )
            }
        } catch (e: Exception) {
            println(e.message)
        } finally {
            _uiState.update { state -> state.copy(isLoading = false) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun collectCurrencyAmount() {
        viewModelScope.launch(Dispatchers.Default) {
            userInput.debounce(ONE_SECOND_IN_MILLISECONDS).distinctUntilChanged().collectLatest {
                selectedAmountForSold = it
                try {
                    _uiState.update { state -> state.copy(isLoading = true) }
                    if (selectedCurrencyForBuy != null && selectedCurrencyForSold != null
                        && selectedAmountForSold != null && currencyExchangeRates != null
                    ) {
                        val result = currencyExchangeUseCase(
                            fromCurrency = selectedCurrencyForSold!!,
                            fromCurrencyValue = selectedAmountForSold!!,
                            toCurrency = selectedCurrencyForBuy!!,
                            currencyExchangeRates = currencyExchangeRates!!
                        )
                        println(result)
                    }

                } catch (e: Exception) {


                } finally {
                    _uiState.update { state -> state.copy(isLoading = false) }
                }

            }
        }
    }

}
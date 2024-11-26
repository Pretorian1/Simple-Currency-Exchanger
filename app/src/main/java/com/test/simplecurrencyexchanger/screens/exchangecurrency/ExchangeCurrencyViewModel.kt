package com.test.simplecurrencyexchanger.screens.exchangecurrency

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.core.domain.models.CurrencyExchangeRates
import com.test.core.domain.models.CurrencyExchangeResult
import com.test.core.domain.usecases.CurrencyExchangeUseCase
import com.test.core.domain.usecases.ForgetUserDataUseCase
import com.test.core.domain.usecases.GetCurrencyExchangeRatesUseCase
import com.test.core.domain.usecases.GetUserDataOrItFirstInitializationUseCase
import com.test.core.domain.usecases.SaveUserDataAfterCurrencyExchangeUseCase
import com.test.simplecurrencyexchanger.R
import com.test.simplecurrencyexchanger.utils.extensions.launchPeriodic
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
import kotlin.concurrent.Volatile

private const val ANR_TIMEOUT = 5000L
private const val RATES_SYNCHRONIZATION_TIMEOUT = 5000L
private const val ONE_SECOND_IN_MILLISECONDS = 1000L

@HiltViewModel
class ExchangeCurrencyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCurrencyExchangeRatesUseCase: GetCurrencyExchangeRatesUseCase,
    private val getUserDataUseCase: GetUserDataOrItFirstInitializationUseCase,
    private val currencyExchangeUseCase: CurrencyExchangeUseCase,
    private val saveUserDataAfterCurrencyExchangeUseCase: SaveUserDataAfterCurrencyExchangeUseCase,
    private val forgetUserDataUseCase: ForgetUserDataUseCase
) : ViewModel() {

    private val coroutineScopeIO = viewModelScope + Dispatchers.IO

    private val _uiState = MutableStateFlow(ExchangeCurrencyContract.UIState())
    val uiState: StateFlow<ExchangeCurrencyContract.UIState> = _uiState.onStart {
        firstUserDataInitialization()
        collectCurrencyAmount()
    }.stateIn(
        coroutineScopeIO,
        SharingStarted.WhileSubscribed(ANR_TIMEOUT),
        ExchangeCurrencyContract.UIState(isLoading = true)
    )

    private val _userInput = MutableStateFlow(0.0)

    private val userInput: StateFlow<Double> = _userInput

    private val _events = Channel<ExchangeCurrencyContract.Event>()
    val events = _events.receiveAsFlow()

    private var selectedCurrencyForSell: String? = null
    private var selectedCurrencyForBuy: String? = null
    private var selectedAmountForSell: Double? = null

    @Volatile
    private var currencyExchangeRates: CurrencyExchangeRates? = null
        set(value) = synchronized(this) {
            field = value
        }
        get() = synchronized(this) {
            return@synchronized field
        }
    private var synchronizedRatesJob: Job? = null
    private var synchronizationEnable = true

    private var availableCurrencies: List<String>? = null
    private var intermediateCurrencyExchangeState: CurrencyExchangeResult.Success? = null

    init {
        runRateSynchronization(synchronizationEnable)
    }


    fun onCurrencyBalanceClicked(currency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { state -> state.copy(isLoading = true) }
            val currencies = availableCurrencies!!.filterNot { currency == it }
            selectedCurrencyForSell = currency
            println(currency)
            _uiState.update { state ->
                state.copy(
                    selectedCurrencyForSold = currency,
                    availableCurrenciesForExchanging = currencies
                )
            }
            calculateExchange()
        }

    }

    fun onCurrencyAmountChanged(amount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _userInput.emit(amount)
        }
    }

    fun onCurrencyToByTypeChanged(currency: String) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedCurrencyForBuy = currency
            calculateExchange()
            println(currency)
        }
    }

    fun onForgetUserDataClicked() {
        viewModelScope.launch {
            _events.send(ExchangeCurrencyContract.Event.ForgotUserData)
        }
    }

    private suspend fun firstUserDataInitialization() {
        try {
            currencyExchangeRates = getCurrencyExchangeRatesUseCase()
            println(currencyExchangeRates)
            val user = getUserDataUseCase(currencyExchangeRates!!.base)
            println(user)
            availableCurrencies = currencyExchangeRates!!.rates.keys.toList()
            _uiState.update { state ->
                state.copy(
                    balanceItems = user.balance.map {
                        CurrencyBalance(
                            currency = it.key,
                            balance = it.value
                        )
                    },
                    availableCurrenciesForExchanging = availableCurrencies!!
                )
            }
        } catch (e: Exception) {
            _events.send(
                ExchangeCurrencyContract.Event.ShowError(
                    message = e.message ?: context.getString(
                        R.string.not_supported_error
                    )
                )
            )
            println(e.message)
        } finally {
            _uiState.update { state -> state.copy(isLoading = false) }
        }
    }

    fun saveUserData() {
        intermediateCurrencyExchangeState?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    _uiState.update { state ->
                        state.copy(isLoading = true)
                    }
                    saveUserDataAfterCurrencyExchangeUseCase(
                        fromCurrency = it.fromCurrency,
                        fromCurrencyValue = it.fromCurrencyValue,
                        toCurrency = it.toCurrency,
                        toCurrencyValue = it.toCurrencyValue
                    )
                    val user = getUserDataUseCase(currencyExchangeRates?.base!!)
                    intermediateCurrencyExchangeState = null
                    _uiState.update { state ->
                        state.copy(
                            balanceItems = user.balance.map {
                                CurrencyBalance(
                                    currency = it.key,
                                    balance = it.value
                                )
                            },
                            selectedCurrencyForSold = null,
                            possibleSoldTip = null,
                            possibleBoughtTip = null,
                            exchangeEnabled = false
                        )
                    }
                    _events.send(
                        ExchangeCurrencyContract.Event.ShowInfo(
                            message = if (it.commission == 0.0)
                                context.getString(
                                    R.string.dsc_currency_converted_short, it.fromCurrencyValue,
                                    it.fromCurrency, it.toCurrencyValue, it.toCurrency
                                )
                            else
                                context.getString(
                                    R.string.dsc_currency_converted,
                                    it.fromCurrencyValue,
                                    it.fromCurrency,
                                    it.toCurrencyValue,
                                    it.toCurrency,
                                    it.commission,
                                    it.fromCurrency
                                )
                        )
                    )
                    intermediateCurrencyExchangeState = null
                } catch (e: Exception) {
                    _events.send(
                        ExchangeCurrencyContract.Event.ShowError(
                            message = e.message ?: context.getString(
                                R.string.not_supported_error
                            )
                        )
                    )
                } finally {
                    _uiState.update { state ->
                        state.copy(isLoading = false)
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun collectCurrencyAmount() {
        viewModelScope.launch(Dispatchers.Default) {
            userInput.debounce(ONE_SECOND_IN_MILLISECONDS).distinctUntilChanged().collectLatest {
                if (it != 0.0) {
                    selectedAmountForSell = it
                    calculateExchange()
                } else {
                    selectedAmountForSell = null
                    _uiState.update { state ->
                        state.copy(
                            possibleSoldTip = null,
                            possibleBoughtTip = null,
                            exchangeEnabled = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun calculateExchange() {
        try {
            _uiState.update { state -> state.copy(isLoading = true) }
            if (selectedCurrencyForBuy != null && selectedCurrencyForSell != null
                && selectedAmountForSell != null && currencyExchangeRates != null
            ) {
                val result = currencyExchangeUseCase(
                    fromCurrency = selectedCurrencyForSell!!,
                    fromCurrencyValue = selectedAmountForSell!!,
                    toCurrency = selectedCurrencyForBuy!!,
                    currencyExchangeRates = currencyExchangeRates!!
                )
                prepareResultFormCurrencyExchange(result)
                if (result is CurrencyExchangeResult.Success)
                    intermediateCurrencyExchangeState = result
                println(result)
                delay(ONE_SECOND_IN_MILLISECONDS)
            }
        } catch (e: Exception) {
            _events.send(
                ExchangeCurrencyContract.Event.ShowError(
                    message = e.message ?: context.getString(
                        R.string.not_supported_error
                    )
                )
            )
        } finally {
            _uiState.update { state -> state.copy(isLoading = false) }
        }
    }

    fun forgetUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { ExchangeCurrencyContract.UIState() }
                forgetUserDataUseCase()
                firstUserDataInitialization()
                selectedCurrencyForSell = null
                selectedCurrencyForBuy = null
                selectedAmountForSell = null
            } catch (e: Exception) {
                _events.send(
                    ExchangeCurrencyContract.Event.ShowError(
                        message = e.message ?: context.getString(
                            R.string.not_supported_error
                        )
                    )
                )
            } finally {
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        }
    }

    /* fun cancelSynchronization() {//for complicated cases
         if (synchronizedRatesJob?.isActive == true) {
             synchronizedRatesJob?.cancel()
         }
     }*/

    private fun runRateSynchronization(enable: Boolean) {
        synchronizedRatesJob =
            (coroutineScopeIO).launchPeriodic(
                enable = enable,
                repeatMillis = RATES_SYNCHRONIZATION_TIMEOUT
            ) {
                ratesSynchronization()
            }
    }

    private suspend fun ratesSynchronization() {
        try {
            println("Run Synchronization")
            val result = getCurrencyExchangeRatesUseCase()
            if (result.rates != currencyExchangeRates?.rates) {
                println("Not equal rates in synchronization")
                currencyExchangeRates = result
                availableCurrencies = result.rates.keys.toList()
                _uiState.update { state -> state.copy(availableCurrenciesForExchanging = availableCurrencies!!) }
            }
        } catch (e: Exception) {
            println("Error in synchronization")
            println(e)
        }
    }

    private fun prepareResultFormCurrencyExchange(data: CurrencyExchangeResult) {
        when (data) {
            is CurrencyExchangeResult.Success -> {
                _uiState.update { state ->
                    state.copy(
                        possibleSoldTip = if (data.commission == 0.0) context.getString(
                            R.string.tip_sold,
                            data.fromCurrencyValue,
                            data.fromCurrency
                        ) else
                            context.getString(
                                R.string.tip_sold_with_commission,
                                data.fromCurrencyValue,
                                data.fromCurrency,
                                data.commission,
                                data.fromCurrency
                            ),
                        possibleBoughtTip = context.getString(
                            R.string.tip_bought,
                            data.toCurrencyValue,
                            data.toCurrency
                        ),
                        exchangeEnabled = true
                    )
                }
            }

            is CurrencyExchangeResult.Failed -> {
                _uiState.update { state ->
                    state.copy(
                        possibleSoldTip = if (data.commission == 0.0) context.getString(
                            R.string.tip_sold,
                            data.fromCurrencyValue,
                            data.fromCurrency
                        ) else
                            context.getString(
                                R.string.tip_sold_with_commission,
                                data.fromCurrencyValue,
                                data.fromCurrency,
                                data.commission,
                                data.fromCurrency
                            ),
                        possibleBoughtTip = context.getString(
                            R.string.tip_bought,
                            data.toCurrencyValue,
                            data.toCurrency
                        ),
                        exchangeEnabled = false
                    )
                }
            }
        }
    }

}
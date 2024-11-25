package com.test.simplecurrencyexchanger.screens.exchangecurrency

interface ExchangeCurrencyContract {

    data class UIState(
        val isLoading: Boolean = false,
        val balanceItems: List<CurrencyBalance> = emptyList(),
        val availableCurrenciesForExchanging: List<String> = emptyList(),
        val exchangeEnabled: Boolean = false
    )

    sealed interface Event {
        data class ShowError(val cause: Throwable) : Event
        data object ForgotUserData : Event
    }
}
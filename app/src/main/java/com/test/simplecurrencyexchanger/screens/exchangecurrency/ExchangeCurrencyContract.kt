package com.test.simplecurrencyexchanger.screens.exchangecurrency

interface ExchangeCurrencyContract {

    data class UIState(
        val isLoading: Boolean = false,
        val balanceItems: List<CurrencyBalance> = emptyList(),
        val selectedCurrencyForSold: String? = null,
        val availableCurrenciesForExchanging: List<String> = emptyList(),
        val possibleSoldTip: String? = null,
        val possibleBoughtTip: String? = null,
        val exchangeEnabled: Boolean = false
    )

    sealed interface Event {
        data class ShowError(val message: String) : Event
        data class ShowInfo(val message: String) : Event
        data object ForgotUserData : Event
    }
}
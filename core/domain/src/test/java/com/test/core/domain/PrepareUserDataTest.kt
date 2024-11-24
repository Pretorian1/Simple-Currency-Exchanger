package com.test.core.domain

import com.test.core.domain.models.UserData
import com.test.core.domain.usecases.prepareUserData
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows

class PrepareUserDataTest {

    private val userData = UserData(
        exchangeCounter = 1, balance = mapOf(
            "USD" to 400.0,
            "UAH" to 1000.0, "GBP" to 339.3
        )
    )
    private val firstExpected = UserData(
        exchangeCounter = 2, balance = mapOf(
            "USD" to 299.0,
            "UAH" to 1000.0, "GBP" to 389.3
        )
    )

    private val secondExpected = UserData(
        exchangeCounter = 2, balance = mapOf(
            "USD" to 500.0,
            "UAH" to 108.0, "GBP" to 339.3
        )
    )

    @Test
    fun preparationOfUserDataShouldBeRight() {
        assertEquals(
            firstExpected, prepareUserData(
                fromCurrency = "USD", fromCurrencyValue = 101.0,
                toCurrency = "GBP", toCurrencyValue = 50.0, userData = userData
            )
        )
        assertEquals(
            secondExpected, prepareUserData(
                fromCurrency = "UAH", fromCurrencyValue = 892.0,
                toCurrency = "USD", toCurrencyValue = 100.0, userData = userData
            )
        )
    }

    @Test
    fun shouldFail() {
        assertThrows(NullPointerException::class.java) {
            prepareUserData(
                fromCurrency = "US", fromCurrencyValue = 10000.0,
                toCurrency = "GBP", toCurrencyValue = 50.0, userData = userData
            )
        }
    }
}
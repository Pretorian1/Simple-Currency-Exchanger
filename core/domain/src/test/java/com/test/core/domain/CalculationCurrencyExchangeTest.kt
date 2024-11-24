package com.test.core.domain

import com.test.core.domain.mappers.DataForCurrencyExchangeMapper
import com.test.core.domain.models.CurrencyExchangeRates
import com.test.core.domain.utils.extensions.CalculationsExchangeResult
import com.test.core.domain.utils.extensions.calculateExchange
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals


class CalculationCurrencyExchangeTest {
    private var mapper = DataForCurrencyExchangeMapper()
    private val currencyExchangeRates = CurrencyExchangeRates(
        base = "EUR", date = "", rates = mapOf(
            "USD" to 1.129031,
            "UAH" to 31.018778, "GBP" to 0.835342
        )
    )
    private val firstDataForCurrencyExchange = mapper.map(
        fromCurrency = "EUR",
        fromCurrencyValue = 100.0,
        toCurrency = "USD",
        currencyExchangeRates
    )
    private val firstExpected =
        CalculationsExchangeResult(fromValue = 100.7, toValue = 112.9031, commissionValue = 0.7)
    private val secondDataForCurrencyExchange = mapper.map(
        fromCurrency = "UAH",
        fromCurrencyValue = 200.0,
        toCurrency = "USD",
        currencyExchangeRates
    )
    private val secondExpected = CalculationsExchangeResult(
        fromValue = 201.4, toValue = 7.2796613715730505, commissionValue = 1.4
    )

    private val thirdDataForCurrencyExchange = mapper.map(
        fromCurrency = "GBP",
        fromCurrencyValue = 592.32,
        toCurrency = "EUR",
        currencyExchangeRates
    )
    private val thirdExpectedWithoutCommission = CalculationsExchangeResult(
        fromValue = 592.32,
        toValue = 709.0748459912228,
        commissionValue = 0.0
    )
    private val thirdExpected = CalculationsExchangeResult(
        fromValue = 657.4752000000001,
        toValue = 709.0748459912228,
        commissionValue = 65.15520000000001
    )


    @Test
    fun calculateExchangeShouldBeRight() {
        assertEquals(firstExpected, firstDataForCurrencyExchange.calculateExchange(0.7))
        assertEquals(secondExpected, secondDataForCurrencyExchange.calculateExchange(0.7))
        assertEquals(
            thirdExpectedWithoutCommission,
            thirdDataForCurrencyExchange.calculateExchange()
        )
        assertEquals(thirdExpected, thirdDataForCurrencyExchange.calculateExchange(11.0))
    }
}
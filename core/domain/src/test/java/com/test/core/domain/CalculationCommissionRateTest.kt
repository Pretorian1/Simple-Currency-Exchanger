package com.test.core.domain

import com.test.core.domain.usecases.calculateCommissionRate


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.Test

class CalculationCommissionRateTest {
    @Test
    fun calculateCommissionShouldBeRight() {
        assertEquals(0.0, calculateCommissionRate(2, 300.0, 1.0))
        assertEquals(0.0, calculateCommissionRate(7, 300.0, 1.0))
        assertEquals(0.7, calculateCommissionRate(10, 100.0, 1.0))
        assertEquals(0.7, calculateCommissionRate(10, 100.0, 31.0))
        assertEquals(0.0, calculateCommissionRate(15, 100.0, 31.0))
    }
}
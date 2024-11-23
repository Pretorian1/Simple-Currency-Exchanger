package com.test.core.domain.models

data class UserData(
    val exchangeCounter: Int,
    val balance: Map<String, Double>
)

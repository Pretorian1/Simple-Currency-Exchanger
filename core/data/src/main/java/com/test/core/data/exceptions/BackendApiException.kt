package com.test.core.data.exceptions

class BackendApiException(
    val httpCode: Int,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

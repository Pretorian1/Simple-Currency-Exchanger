package com.test.core.data.repositories

import com.test.core.data.exceptions.BackendApiException
import retrofit2.HttpException
import retrofit2.Response

abstract class BaseRetrofitRepository {

    suspend fun <R : Any> callApi(handler: suspend () -> Response<R>): R {
        return try {
            val response = handler()
            response.body() ?: throw BackendApiException(
                httpCode = 200,
                message = response.message()
            )
        } catch (exc: HttpException) {
            throw BackendApiException(
                httpCode = exc.code(),
                message = exc.localizedMessage ?: exc.message ?: "Unknown error",
                cause = exc
            )
        }
    }
}
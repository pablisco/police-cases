package uk.crimeapp.common

import retrofit2.Call
import retrofit2.HttpException

suspend fun <T> Call<T>.await(): T {
    val response = execute()
    return if (response.isSuccessful) {
        response.body() ?: throw IllegalStateException("Empty body")
    } else {
        throw HttpException(response)
    }
}
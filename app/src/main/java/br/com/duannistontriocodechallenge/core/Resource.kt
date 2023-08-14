package br.com.duannistontriocodechallenge.core

import java.io.IOException

sealed class Resource<out T : Any> {
    class Success<out T : Any>(val data: T) : Resource<T>()
    class Error(val error: ErrorType) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

sealed class ErrorType {
    class Business(val error: BusinessError) : ErrorType()
    data object Unauthorized : ErrorType()
    class Network(val error: IOException) : ErrorType()
    class Unknown(val error: Throwable?) : ErrorType()
}
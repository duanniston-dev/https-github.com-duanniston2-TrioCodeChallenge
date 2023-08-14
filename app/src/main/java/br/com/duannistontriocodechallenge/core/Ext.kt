package br.com.duannistontriocodechallenge.core

import androidx.lifecycle.MutableLiveData
import timber.log.Timber


fun <T : Any> MutableLiveData<Resource<T>>.postResourceGenericError(exception: Throwable) {
    Timber.w(exception, "postResourceGenericError")
    postValue(Resource.Error(ErrorType.Unknown(exception)))
}

fun ErrorType.toErrorDialog(): ErrorDialog {
    return when (this) {
        is ErrorType.Business -> {
            ErrorDialog(error.error?.title ?: "Ops!", error.error?.message ?: error.message)
        }

        is ErrorType.Network -> {
            ErrorDialog("Ops!", "Check your connection!.")
        }

        ErrorType.Unauthorized -> {
            ErrorDialog("Ops!", "Unauthorized.")
        }

        is ErrorType.Unknown -> {
            ErrorDialog("Ops!", "We have some error here. Try again.")
        }
    }
}
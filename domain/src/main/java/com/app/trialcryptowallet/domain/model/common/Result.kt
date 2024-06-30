package com.app.trialcryptowallet.domain.model.common

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val code: Int = 0, val exception: Throwable) : Result<Nothing>()
}
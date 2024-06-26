package com.app.trialcryptowallet.data.model

const val ERROR_CODE_RATE_LIMIT = 429

sealed class Error {
    data object ErrorLoadingData : Error()
    data object ErrorRateLimit : Error()
}
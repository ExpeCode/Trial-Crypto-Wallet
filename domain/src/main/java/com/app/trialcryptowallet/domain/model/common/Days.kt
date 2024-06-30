package com.app.trialcryptowallet.domain.model.common

sealed class Days(val value: Int) {
    data object Day : Days(1)
    data object Week : Days(7)
    data object Month : Days(30)
    data object HalfYear : Days(180)
    data object Year : Days(365)
}
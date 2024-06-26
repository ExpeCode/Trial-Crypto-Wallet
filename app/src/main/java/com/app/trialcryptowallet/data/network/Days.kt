package com.app.trialcryptowallet.data.network

import androidx.annotation.IntDef

const val DAY = 1
const val WEEK = 7
const val MONTH = 30
const val HALF_YEAR = 180
const val YEAR = 365

@IntDef(DAY, WEEK, MONTH, HALF_YEAR, YEAR)
@Retention(AnnotationRetention.SOURCE)
annotation class Days
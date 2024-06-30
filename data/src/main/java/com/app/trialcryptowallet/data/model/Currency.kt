package com.app.trialcryptowallet.data.model

import androidx.annotation.StringDef

const val USD = "usd"

@StringDef(USD)
@Retention(AnnotationRetention.SOURCE)
annotation class Currency
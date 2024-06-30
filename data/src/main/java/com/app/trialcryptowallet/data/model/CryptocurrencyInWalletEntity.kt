package com.app.trialcryptowallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
data class CryptocurrencyInWalletEntity(
    @PrimaryKey val id: String,
    val name: String,
    var amount: Double
)
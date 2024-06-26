package com.app.trialcryptowallet.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity

@Database(entities = [CryptocurrencyInWalletEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
}
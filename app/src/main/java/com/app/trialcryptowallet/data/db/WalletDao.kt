package com.app.trialcryptowallet.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet")
    suspend fun getAll(): List<CryptocurrencyInWalletEntity>

    @Query("SELECT * FROM wallet WHERE id LIKE :id")
    suspend fun findById(id: String): CryptocurrencyInWalletEntity?

    @Insert
    suspend fun insert(asset: CryptocurrencyInWalletEntity)

    @Delete
    suspend fun delete(asset: CryptocurrencyInWalletEntity)

    @Update
    suspend fun update(asset: CryptocurrencyInWalletEntity)
}
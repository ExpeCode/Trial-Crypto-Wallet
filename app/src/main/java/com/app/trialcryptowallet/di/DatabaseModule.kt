package com.app.trialcryptowallet.di

import android.app.Application
import androidx.room.Room
import com.app.trialcryptowallet.data.Constants
import com.app.trialcryptowallet.data.db.AppDatabase
import com.app.trialcryptowallet.data.db.WalletDao
import com.app.trialcryptowallet.data.network.CoinGeckoApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

fun provideDatabase(application: Application): AppDatabase = Room
    .databaseBuilder(application, AppDatabase::class.java, "app_database")
    .fallbackToDestructiveMigration()
    .build()

fun provideDao(database: AppDatabase): WalletDao = database.walletDao()

val databaseModule = module {
    singleOf(::provideDatabase)
    singleOf(::provideDao)
}
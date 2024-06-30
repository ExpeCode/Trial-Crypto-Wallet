package com.app.trialcryptowallet.data.di

import android.app.Application
import androidx.room.Room
import com.app.trialcryptowallet.data.db.AppDatabase
import com.app.trialcryptowallet.data.db.WalletDao

fun provideDatabase(application: Application): AppDatabase = Room
    .databaseBuilder(application, AppDatabase::class.java, "app_database")
    .fallbackToDestructiveMigration()
    .build()

fun provideDao(database: AppDatabase): WalletDao = database.walletDao()
package com.app.trialcryptowallet.data.di

import com.app.trialcryptowallet.data.api.CoinGeckoApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL_COIN_GECKO = "https://api.coingecko.com/api/v3/"

fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

fun provideHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
    .readTimeout(60, TimeUnit.SECONDS)
    .connectTimeout(60, TimeUnit.SECONDS)
    .addInterceptor(loggingInterceptor)
    /*.addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-CMC_PRO_API_KEY", "YOUR_API_KEY_HERE")
            .build()
        chain.proceed(request)
    }*/
    .build()

fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL_COIN_GECKO)
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun provideService(retrofit: Retrofit): CoinGeckoApiService = retrofit.create(CoinGeckoApiService::class.java)
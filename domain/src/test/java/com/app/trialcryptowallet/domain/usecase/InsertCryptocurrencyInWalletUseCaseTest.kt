package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.FakeWalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InsertCryptocurrencyInWalletUseCaseTest {

    private lateinit var walletRepository: FakeWalletRepository
    private lateinit var insertCryptocurrencyInWalletUseCase: InsertCryptocurrencyInWalletUseCase

    @Before
    fun setUp() {
        walletRepository = FakeWalletRepository()
        insertCryptocurrencyInWalletUseCase = InsertCryptocurrencyInWalletUseCase(walletRepository)
    }

    @Test
    fun `test insert cryptocurrency in wallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "btc", name = "Bitcoin", amount = 0.5)

        // When
        insertCryptocurrencyInWalletUseCase(mockCryptocurrency)
        val result = walletRepository.getAllCryptocurrenciesInWallet()

        // Then
        assertTrue(result.contains(mockCryptocurrency))
    }
}

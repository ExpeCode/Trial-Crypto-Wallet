package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.FakeWalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdateCryptocurrencyInWalletUseCaseTest {

    private lateinit var walletRepository: FakeWalletRepository
    private lateinit var updateCryptocurrencyInWalletUseCase: UpdateCryptocurrencyInWalletUseCase

    @Before
    fun setUp() {
        walletRepository = FakeWalletRepository()
        updateCryptocurrencyInWalletUseCase = UpdateCryptocurrencyInWalletUseCase(walletRepository)
    }

    @Test
    fun `test insert cryptocurrency in wallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "btc", name = "Bitcoin", amount = 0.5)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency)
        mockCryptocurrency.amount = 0.0
        updateCryptocurrencyInWalletUseCase(mockCryptocurrency)

        // When
        val result = walletRepository.findCryptocurrencyInWalletById(mockCryptocurrency.id)

        // Then
        assertTrue(result != null)
        assertTrue(result!!.amount == 0.0)
    }
}

package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.FakeWalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FindCryptocurrencyInWalletByIdUseCaseTest {

    private lateinit var walletRepository: FakeWalletRepository
    private lateinit var findCryptocurrencyInWalletByIdUseCase: FindCryptocurrencyInWalletByIdUseCase

    @Before
    fun setUp() {
        walletRepository = FakeWalletRepository()
        findCryptocurrencyInWalletByIdUseCase = FindCryptocurrencyInWalletByIdUseCase(walletRepository)
    }

    @Test
    fun `test insert cryptocurrency in wallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "btc", name = "Bitcoin", amount = 0.5)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency)

        // When
        val result = findCryptocurrencyInWalletByIdUseCase(mockCryptocurrency.id)

        // Then
        assertTrue(result != null)
    }
}

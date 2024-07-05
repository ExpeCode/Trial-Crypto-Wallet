package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.FakeWalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllCryptocurrenciesInWalletUseCaseTest {

    private lateinit var walletRepository: FakeWalletRepository
    private lateinit var getAllCryptocurrenciesInWalletUseCase: GetAllCryptocurrenciesInWalletUseCase

    @Before
    fun setUp() {
        walletRepository = FakeWalletRepository()
        getAllCryptocurrenciesInWalletUseCase = GetAllCryptocurrenciesInWalletUseCase(walletRepository)
    }

    @Test
    fun `test get all cryptocurrencies in wallet`() = runBlocking {
        // Given
        val mockCryptocurrency1 = CryptocurrencyInWallet(id = "bitcoin", name = "Bitcoin", amount = 0.02)
        val mockCryptocurrency2 = CryptocurrencyInWallet(id = "ethereum", name = "Ethereum", amount = 0.5)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency1)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency2)

        // When
        val result = getAllCryptocurrenciesInWalletUseCase()

        // Then
        assertEquals(listOf(mockCryptocurrency1, mockCryptocurrency2), result)
    }
}

package com.app.trialcryptowallet.data.repository

import com.app.trialcryptowallet.data.db.FakeWalletDao
import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.WalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WalletRepositoryImplTest {

    private lateinit var walletDao: FakeWalletDao
    private lateinit var walletRepository: WalletRepository

    @Before
    fun setUp() {
        walletDao = FakeWalletDao()
        walletRepository = WalletRepositoryImpl(walletDao)
    }

    @Test
    fun `test getAllCryptocurrenciesInWallet`() = runBlocking {
        // Given
        val mockCryptocurrency1 = CryptocurrencyInWallet(id = "1", name = "Bitcoin", amount = 0.5)
        val mockCryptocurrency2 = CryptocurrencyInWallet(id = "2", name = "Ethereum", amount = 0.1)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency1)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency2)

        // When
        val result = walletRepository.getAllCryptocurrenciesInWallet()

        // Then
        assertEquals(listOf(mockCryptocurrency1, mockCryptocurrency2), result)
    }

    @Test
    fun `test insertCryptocurrencyInWallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "1", name = "Bitcoin", amount = 0.1)

        // When
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency)
        val result = walletRepository.getAllCryptocurrenciesInWallet()

        // Then
        assertEquals(listOf(mockCryptocurrency), result)
    }

    @Test
    fun `test deleteCryptocurrencyInWallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "1", name = "Bitcoin", amount = 0.2)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency)

        // When
        walletRepository.deleteCryptocurrencyInWallet(mockCryptocurrency)
        val result = walletRepository.getAllCryptocurrenciesInWallet()

        // Then
        assertEquals(emptyList<CryptocurrencyInWallet>(), result)
    }

    @Test
    fun `test updateCryptocurrencyInWallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "1", name = "Bitcoin", amount = 0.4)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency)
        val updatedCryptocurrency = mockCryptocurrency.copy(name = "Updated Bitcoin")

        // When
        walletRepository.updateCryptocurrencyInWallet(updatedCryptocurrency)
        val result = walletRepository.getAllCryptocurrenciesInWallet()

        // Then
        assertEquals(listOf(updatedCryptocurrency), result)
    }
}

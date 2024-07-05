package com.app.trialcryptowallet.domain.usecase

import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.FakeWalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteCryptocurrencyInWalletUseCaseTest {

    private lateinit var walletRepository: FakeWalletRepository
    private lateinit var deleteCryptocurrencyInWalletUseCase: DeleteCryptocurrencyInWalletUseCase

    @Before
    fun setUp() {
        walletRepository = FakeWalletRepository()
        deleteCryptocurrencyInWalletUseCase = DeleteCryptocurrencyInWalletUseCase(walletRepository)
    }

    @Test
    fun `test insert cryptocurrency in wallet`() = runBlocking {
        // Given
        val mockCryptocurrency = CryptocurrencyInWallet(id = "btc", name = "Bitcoin", amount = 0.5)
        walletRepository.insertCryptocurrencyInWallet(mockCryptocurrency)

        // When
        deleteCryptocurrencyInWalletUseCase(mockCryptocurrency)

        val result = walletRepository.getAllCryptocurrenciesInWallet()

        // Then
        assertTrue(!result.contains(mockCryptocurrency))
    }
}

package com.app.trialcryptowallet.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.app.trialcryptowallet.data.db.AppDatabase
import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.WalletRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import org.junit.Assert.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class WalletRepositoryImplTest {

    private lateinit var db: AppDatabase
    private lateinit var walletRepository: WalletRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        val dao = db.walletDao()
        walletRepository = WalletRepositoryImpl(dao)
    }

    @Test
    fun insertAndReadData() = runBlocking {
        val cryptocurrency = CryptocurrencyInWallet("1", "Bitcoin", 1.0)
        walletRepository.insertCryptocurrencyInWallet(cryptocurrency)
        val retrievedEntity = walletRepository.findCryptocurrencyInWalletById("1")
        assertEquals(retrievedEntity?.name, "Bitcoin")
        assertEquals(retrievedEntity?.amount ?: 0.0, 1.0, 0.01)
    }

    @Test
    fun deleteData() = runBlocking {
        val cryptocurrency = CryptocurrencyInWallet("1", "Bitcoin", 1.0)
        walletRepository.insertCryptocurrencyInWallet(cryptocurrency)
        walletRepository.deleteCryptocurrencyInWallet(cryptocurrency)
        val retrievedEntity = walletRepository.findCryptocurrencyInWalletById("1")
        assertNull(retrievedEntity)
    }

    @Test
    fun updateData() = runBlocking {
        val cryptocurrency = CryptocurrencyInWallet("1", "Bitcoin", 1.0)
        walletRepository.insertCryptocurrencyInWallet(cryptocurrency)
        cryptocurrency.amount = 2.0
        walletRepository.updateCryptocurrencyInWallet(cryptocurrency)
        val retrievedEntity = walletRepository.findCryptocurrencyInWalletById("1")
        assertEquals(retrievedEntity?.amount ?: 0.0, 2.0, 0.01)
    }

    @Test
    fun getAllData() = runBlocking {
        val cryptocurrency1 = CryptocurrencyInWallet("1", "Bitcoin", 1.0)
        val cryptocurrency2 = CryptocurrencyInWallet("2", "Ethereum", 2.0)
        walletRepository.insertCryptocurrencyInWallet(cryptocurrency1)
        walletRepository.insertCryptocurrencyInWallet(cryptocurrency2)
        val allEntities = walletRepository.getAllCryptocurrenciesInWallet()
        assertEquals(allEntities.size, 2)
        val entityFromDb1 = allEntities.find { it.id == cryptocurrency1.id && it.name == cryptocurrency1.name && it.amount == cryptocurrency1.amount }
        val entityFromDb2 = allEntities.find { it.id == cryptocurrency2.id && it.name == cryptocurrency2.name && it.amount == cryptocurrency2.amount }
        assertNotNull(entityFromDb1)
        assertNotNull(entityFromDb2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}

package com.app.trialcryptowallet.screens.wallet

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.trialcryptowallet.domain.model.common.Result
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInWallet
import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.PreferencesRepository
import com.app.trialcryptowallet.domain.usecase.DeleteCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.FindCryptocurrencyInWalletByIdUseCase
import com.app.trialcryptowallet.domain.usecase.GetAllCryptocurrenciesInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.GetCoinsListWithMarketDataUseCase
import com.app.trialcryptowallet.domain.usecase.InsertCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.UpdateCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.utils.ConnectivityMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val getCoinsListWithMarketDataUseCase: GetCoinsListWithMarketDataUseCase,
    private val getAllCryptocurrenciesInWalletUseCase: GetAllCryptocurrenciesInWalletUseCase,
    private val findCryptocurrencyInWalletByIdUseCase: FindCryptocurrencyInWalletByIdUseCase,
    private val updateCryptocurrencyInWalletUseCase: UpdateCryptocurrencyInWalletUseCase,
    private val insertCryptocurrencyInWalletUseCase: InsertCryptocurrencyInWalletUseCase,
    private val deleteCryptocurrencyInWalletUseCase: DeleteCryptocurrencyInWalletUseCase,
    private val connectivityMonitor: ConnectivityMonitor
) : ViewModel() {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshCryptocurrenciesInWallet()
        }
    }

    private val _availableBalance = MutableStateFlow(preferencesRepository.getAvailableBalance())
    val availableBalance: StateFlow<Double> = _availableBalance

    private val _balanceCrypto = MutableStateFlow(0.0)
    val balanceCrypto: StateFlow<Double> = _balanceCrypto

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _cryptocurrenciesInWallet = MutableStateFlow<List<ItemCryptocurrencyInWallet>>(emptyList())
    val cryptocurrenciesInWallet: StateFlow<List<ItemCryptocurrencyInWallet>> = _cryptocurrenciesInWallet

    private val _showTopUpBalanceDialog = MutableStateFlow(false)
    val showTopUpBalanceDialog: StateFlow<Boolean> = _showTopUpBalanceDialog

    private val _showWithdrawFundsDialog = MutableStateFlow(false)
    val showWithdrawFundsDialog: StateFlow<Boolean> = _showWithdrawFundsDialog

    private val _itemCryptocurrencyInBuyCryptocurrencyDialog = MutableStateFlow<ItemCryptocurrencyInBuyCryptocurrencyDialog?>(null)
    val itemCryptocurrencyInBuyCryptocurrencyDialog: StateFlow<ItemCryptocurrencyInBuyCryptocurrencyDialog?> = _itemCryptocurrencyInBuyCryptocurrencyDialog

    private val _itemCryptocurrencyInSellCryptocurrencyDialog = MutableStateFlow<ItemCryptocurrencyInSellCryptocurrencyDialog?>(null)
    val itemCryptocurrencyInSellCryptocurrencyDialog: StateFlow<ItemCryptocurrencyInSellCryptocurrencyDialog?> = _itemCryptocurrencyInSellCryptocurrencyDialog

    init {
        if (!connectivityMonitor.isInternetConnected()) {
            connectivityMonitor.registerCallback(networkCallback)
        }
    }

    fun showTopUpBalanceDialog() {
        _showTopUpBalanceDialog.update { true }
    }
    fun onDismissTopUpBalanceDialog() {
        _showTopUpBalanceDialog.update { false }
    }
    fun topUpBalance(amount: Double) {
        val newBalance = _availableBalance.value + amount
        _availableBalance.update { newBalance }
        preferencesRepository.setAvailableBalance(newBalance)
    }

    fun showWithdrawFundsDialog() {
        _showWithdrawFundsDialog.update { true }
    }
    fun onDismissWithdrawFundsDialog() {
        _showWithdrawFundsDialog.update { false }
    }
    fun withdrawFunds(amount: Double) {
        if (availableBalance.value >= amount) {
            val newBalance = _availableBalance.value - amount
            _availableBalance.update { newBalance }
            preferencesRepository.setAvailableBalance(newBalance)
        }
    }

    fun refreshCryptocurrenciesInWallet() {
        if (!isLoading.value) {
            _isLoading.update { true }
            viewModelScope.launch {
                val newCryptocurrenciesInWallet = mutableListOf<ItemCryptocurrencyInWallet>()
                var newBalanceCrypto = 0.0

                val allCryptocurrenciesInWalletFromDB = getAllCryptocurrenciesInWalletUseCase()
                if (allCryptocurrenciesInWalletFromDB.isNotEmpty()) {

                    getCoinsListWithMarketDataUseCase().collect { resultCoinsListWithMarketData ->
                        if (resultCoinsListWithMarketData is Result.Success) {
                            allCryptocurrenciesInWalletFromDB.forEach { cryptocurrencyInWalletEntity ->
                                resultCoinsListWithMarketData.data.find { cryptocurrencyDto ->
                                    cryptocurrencyDto.id == cryptocurrencyInWalletEntity.id
                                }?.let { cryptocurrencyDto ->
                                    val itemCryptocurrencyInWallet = ItemCryptocurrencyInWallet(
                                        cryptocurrencyInWalletEntity.id,
                                        cryptocurrencyDto.symbol,
                                        cryptocurrencyInWalletEntity.name,
                                        cryptocurrencyDto.image,
                                        cryptocurrencyDto.current_price,
                                        cryptocurrencyInWalletEntity.amount
                                    )
                                    newCryptocurrenciesInWallet.add(itemCryptocurrencyInWallet)
                                    newBalanceCrypto += itemCryptocurrencyInWallet.amount * itemCryptocurrencyInWallet.current_price
                                }
                            }
                        }
                    }
                }

                _cryptocurrenciesInWallet.update { newCryptocurrenciesInWallet }
                _balanceCrypto.update { newBalanceCrypto }
                _isLoading.update { false }
            }
        }
    }

    fun onClickToBuy(itemCryptocurrencyInWallet: ItemCryptocurrencyInWallet) {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update {
            ItemCryptocurrencyInBuyCryptocurrencyDialog(
                id = itemCryptocurrencyInWallet.id,
                name = itemCryptocurrencyInWallet.name,
                current_price = itemCryptocurrencyInWallet.current_price)
        }
    }
    fun onDismissBuyCryptocurrencyDialog() {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { null }
    }
    fun buyCryptocurrency(itemCryptocurrencyInBuyCryptocurrencyDialog: ItemCryptocurrencyInBuyCryptocurrencyDialog, amount: Double, cost: Double) {
        if (availableBalance.value >= cost) {
            viewModelScope.launch {
                val newBalance = _availableBalance.value - cost
                _availableBalance.update { newBalance }
                preferencesRepository.setAvailableBalance(newBalance)

                findCryptocurrencyInWalletByIdUseCase(itemCryptocurrencyInBuyCryptocurrencyDialog.id)?.let {
                    it.amount += amount
                    updateCryptocurrencyInWalletUseCase(it)
                } ?: run {
                    val cryptocurrencyInWalletEntity = CryptocurrencyInWallet(
                        id = itemCryptocurrencyInBuyCryptocurrencyDialog.id,
                        name = itemCryptocurrencyInBuyCryptocurrencyDialog.name,
                        amount = amount)
                    insertCryptocurrencyInWalletUseCase(cryptocurrencyInWalletEntity)
                }

                refreshCryptocurrenciesInWallet()
            }
        }
    }

    fun onClickToSell(itemCryptocurrencyInWallet: ItemCryptocurrencyInWallet) {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update {
            ItemCryptocurrencyInSellCryptocurrencyDialog(
                id = itemCryptocurrencyInWallet.id,
                symbol = itemCryptocurrencyInWallet.symbol,
                name = itemCryptocurrencyInWallet.name,
                current_price = itemCryptocurrencyInWallet.current_price,
                available_amount = itemCryptocurrencyInWallet.amount)
        }
    }
    fun onDismissSellCryptocurrencyDialog() {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update { null }
    }
    fun sellCryptocurrency(itemCryptocurrencyInSellCryptocurrencyDialog: ItemCryptocurrencyInSellCryptocurrencyDialog, amount: Double, price: Double) {
        if (itemCryptocurrencyInSellCryptocurrencyDialog.available_amount >= amount) {
            viewModelScope.launch {
                val newBalance = _availableBalance.value + price
                _availableBalance.update { newBalance }
                preferencesRepository.setAvailableBalance(newBalance)

                findCryptocurrencyInWalletByIdUseCase(itemCryptocurrencyInSellCryptocurrencyDialog.id)?.let {
                    it.amount -= amount
                    if (it.amount >= 0) {
                        updateCryptocurrencyInWalletUseCase(it)
                    } else {
                        deleteCryptocurrencyInWalletUseCase(it)
                    }
                }

                refreshCryptocurrenciesInWallet()
            }
        }
    }

    override fun onCleared() {
        connectivityMonitor.unregisterCallback(networkCallback)
        super.onCleared()
    }
}
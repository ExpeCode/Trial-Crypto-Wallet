package com.app.trialcryptowallet.screens.wallet

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.trialcryptowallet.data.model.Result
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInWallet
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity
import com.app.trialcryptowallet.data.repository.RepositoryInterface
import com.app.trialcryptowallet.utils.ConnectivityMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel(
    private val repository: RepositoryInterface,
    private val connectivityMonitor: ConnectivityMonitor
) : ViewModel() {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshCryptocurrenciesInWallet()
        }
    }

    private val _availableBalance = MutableStateFlow(repository.getAvailableBalance())
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
        repository.setAvailableBalance(newBalance)
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
            repository.setAvailableBalance(newBalance)
        }
    }

    fun refreshCryptocurrenciesInWallet() {
        if (!isLoading.value) {
            _isLoading.update { true }
            viewModelScope.launch {
                val newCryptocurrenciesInWallet = mutableListOf<ItemCryptocurrencyInWallet>()
                var newBalanceCrypto = 0.0

                val allCryptocurrenciesInWalletFromDB = repository.getAllCryptocurrenciesInWallet()
                if (allCryptocurrenciesInWalletFromDB.isNotEmpty()) {

                    repository.getCoinsListWithMarketData().collect { resultCoinsListWithMarketData ->
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
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { ItemCryptocurrencyInBuyCryptocurrencyDialog(itemCryptocurrencyInWallet.id, itemCryptocurrencyInWallet.name, itemCryptocurrencyInWallet.current_price) }
    }
    fun onDismissBuyCryptocurrencyDialog() {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { null }
    }
    fun buyCryptocurrency(itemCryptocurrencyInBuyCryptocurrencyDialog: ItemCryptocurrencyInBuyCryptocurrencyDialog, amount: Double, cost: Double) {
        if (availableBalance.value >= cost) {
            viewModelScope.launch {
                val newBalance = _availableBalance.value - cost
                _availableBalance.update { newBalance }
                repository.setAvailableBalance(newBalance)

                repository.findCryptocurrencyInWalletById(itemCryptocurrencyInBuyCryptocurrencyDialog.id)?.let {
                    it.amount += amount
                    repository.updateCryptocurrencyInWallet(it)
                } ?: run {
                    val cryptocurrencyInWalletEntity = CryptocurrencyInWalletEntity(itemCryptocurrencyInBuyCryptocurrencyDialog.id, itemCryptocurrencyInBuyCryptocurrencyDialog.name, amount)
                    repository.insertCryptocurrencyInWallet(cryptocurrencyInWalletEntity)
                }

                refreshCryptocurrenciesInWallet()
            }
        }
    }

    fun onClickToSell(itemCryptocurrencyInWallet: ItemCryptocurrencyInWallet) {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update { ItemCryptocurrencyInSellCryptocurrencyDialog(itemCryptocurrencyInWallet.id, itemCryptocurrencyInWallet.symbol, itemCryptocurrencyInWallet.name, itemCryptocurrencyInWallet.current_price, itemCryptocurrencyInWallet.amount) }
    }
    fun onDismissSellCryptocurrencyDialog() {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update { null }
    }
    fun sellCryptocurrency(itemCryptocurrencyInSellCryptocurrencyDialog: ItemCryptocurrencyInSellCryptocurrencyDialog, amount: Double, price: Double) {
        if (itemCryptocurrencyInSellCryptocurrencyDialog.available_amount >= amount) {
            viewModelScope.launch {
                val newBalance = _availableBalance.value + price
                _availableBalance.update { newBalance }
                repository.setAvailableBalance(newBalance)

                repository.findCryptocurrencyInWalletById(itemCryptocurrencyInSellCryptocurrencyDialog.id)?.let {
                    it.amount -= amount
                    if (it.amount >= 0) {
                        repository.updateCryptocurrencyInWallet(it)
                    } else {
                        repository.deleteCryptocurrencyInWallet(it)
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
package com.app.trialcryptowallet.screens.market

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.trialcryptowallet.data.model.ERROR_CODE_RATE_LIMIT
import com.app.trialcryptowallet.data.model.Error
import com.app.trialcryptowallet.data.model.Result
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInMarket
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity
import com.app.trialcryptowallet.data.repository.RepositoryInterface
import com.app.trialcryptowallet.utils.ConnectivityMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketViewModel(
    private val repository: RepositoryInterface,
    private val connectivityMonitor: ConnectivityMonitor
) : ViewModel() {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            refreshCryptocurrenciesInMarket()
        }
    }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackBarErrorMessage = MutableSharedFlow<Error>()
    val snackBarErrorMessage: SharedFlow<Error> = _snackBarErrorMessage.asSharedFlow()

    private val _cryptocurrencies = MutableStateFlow<List<ItemCryptocurrencyInMarket>>(emptyList())
    val cryptocurrencies: StateFlow<List<ItemCryptocurrencyInMarket>> = _cryptocurrencies

    private val _itemCryptocurrencyInBuyCryptocurrencyDialog = MutableStateFlow<ItemCryptocurrencyInBuyCryptocurrencyDialog?>(null)
    val itemCryptocurrencyInBuyCryptocurrencyDialog: StateFlow<ItemCryptocurrencyInBuyCryptocurrencyDialog?> = _itemCryptocurrencyInBuyCryptocurrencyDialog

    init {
        if (!connectivityMonitor.isInternetConnected()) {
            connectivityMonitor.registerCallback(networkCallback)
        }
    }

    fun refreshCryptocurrenciesInMarket() {
        if (!isLoading.value) {
            _isLoading.update { true }
            viewModelScope.launch {
                val newCryptocurrenciesInMarket = mutableListOf<ItemCryptocurrencyInMarket>()

                repository.getCoinsListWithMarketData().collect { resultCoinsListWithMarketData ->
                    when (resultCoinsListWithMarketData) {
                        is Result.Success -> {
                            resultCoinsListWithMarketData.data.forEach { cryptocurrencyDto ->
                                val itemCryptocurrencyInMarket = ItemCryptocurrencyInMarket(
                                    cryptocurrencyDto.id,
                                    cryptocurrencyDto.symbol,
                                    cryptocurrencyDto.name,
                                    cryptocurrencyDto.image,
                                    cryptocurrencyDto.current_price
                                )
                                newCryptocurrenciesInMarket.add(itemCryptocurrencyInMarket)
                            }
                        }
                        is Result.Error -> {
                            if (resultCoinsListWithMarketData.code == ERROR_CODE_RATE_LIMIT) {
                                _snackBarErrorMessage.emit(Error.ErrorRateLimit)
                            } else {
                                _snackBarErrorMessage.emit(Error.ErrorLoadingData)
                            }
                        }
                    }
                }

                _cryptocurrencies.update { newCryptocurrenciesInMarket }
                _isLoading.update { false }
            }
        }
    }

    fun getAvailableBalance() = repository.getAvailableBalance()

    fun onClickToBuy(itemCryptocurrencyInMarket: ItemCryptocurrencyInMarket) {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { ItemCryptocurrencyInBuyCryptocurrencyDialog(itemCryptocurrencyInMarket.id, itemCryptocurrencyInMarket.name, itemCryptocurrencyInMarket.current_price) }
    }
    fun onDialogDismiss() {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { null }
    }
    fun buyCryptocurrency(itemCryptocurrencyInBuyCryptocurrencyDialog: ItemCryptocurrencyInBuyCryptocurrencyDialog, amount: Double, cost: Double, onComplete: () -> Unit) {
        if (getAvailableBalance() >= cost) {
            viewModelScope.launch {
                repository.setAvailableBalance(getAvailableBalance() - cost)

                repository.findCryptocurrencyInWalletById(itemCryptocurrencyInBuyCryptocurrencyDialog.id)?.let {
                    it.amount += amount
                    repository.updateCryptocurrencyInWallet(it)
                } ?: run {
                    val cryptocurrencyInWalletEntity = CryptocurrencyInWalletEntity(itemCryptocurrencyInBuyCryptocurrencyDialog.id, itemCryptocurrencyInBuyCryptocurrencyDialog.name, amount)
                    repository.insertCryptocurrencyInWallet(cryptocurrencyInWalletEntity)
                }

                onComplete.invoke()
            }
        }
    }

    override fun onCleared() {
        connectivityMonitor.unregisterCallback(networkCallback)
        super.onCleared()
    }

}
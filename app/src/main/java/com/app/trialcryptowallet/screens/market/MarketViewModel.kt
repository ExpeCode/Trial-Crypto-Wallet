package com.app.trialcryptowallet.screens.market

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.trialcryptowallet.domain.model.common.ERROR_CODE_RATE_LIMIT
import com.app.trialcryptowallet.domain.model.common.Error
import com.app.trialcryptowallet.domain.model.common.Result
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInMarket
import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.PreferencesRepository
import com.app.trialcryptowallet.domain.usecase.FindCryptocurrencyInWalletByIdUseCase
import com.app.trialcryptowallet.domain.usecase.GetCoinsListWithMarketDataUseCase
import com.app.trialcryptowallet.domain.usecase.InsertCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.UpdateCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.utils.ConnectivityMonitor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val getCoinsListWithMarketDataUseCase: GetCoinsListWithMarketDataUseCase,
    private val findCryptocurrencyInWalletByIdUseCase: FindCryptocurrencyInWalletByIdUseCase,
    private val updateCryptocurrencyInWalletUseCase: UpdateCryptocurrencyInWalletUseCase,
    private val insertCryptocurrencyInWalletUseCase: InsertCryptocurrencyInWalletUseCase,
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

                getCoinsListWithMarketDataUseCase().collect { resultCoinsListWithMarketData ->
                    when (resultCoinsListWithMarketData) {
                        is Result.Success -> {
                            _cryptocurrencies.update {
                                resultCoinsListWithMarketData.data.map { cryptocurrencyDto ->
                                    ItemCryptocurrencyInMarket(
                                        id = cryptocurrencyDto.id,
                                        symbol = cryptocurrencyDto.symbol,
                                        name = cryptocurrencyDto.name,
                                        image = cryptocurrencyDto.image,
                                        current_price = cryptocurrencyDto.current_price
                                    )
                                }
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

                _isLoading.update { false }
            }
        }
    }

    fun getAvailableBalance() = preferencesRepository.getAvailableBalance()

    fun onClickToBuy(itemCryptocurrencyInMarket: ItemCryptocurrencyInMarket) {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update {
            ItemCryptocurrencyInBuyCryptocurrencyDialog(
                id = itemCryptocurrencyInMarket.id,
                name = itemCryptocurrencyInMarket.name,
                current_price = itemCryptocurrencyInMarket.current_price)
        }
    }
    fun onDialogDismiss() {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { null }
    }
    fun buyCryptocurrency(itemCryptocurrencyInBuyCryptocurrencyDialog: ItemCryptocurrencyInBuyCryptocurrencyDialog, amount: Double, cost: Double, onComplete: () -> Unit) {
        if (getAvailableBalance() >= cost) {
            viewModelScope.launch {
                preferencesRepository.setAvailableBalance(getAvailableBalance() - cost)

                findCryptocurrencyInWalletByIdUseCase(itemCryptocurrencyInBuyCryptocurrencyDialog.id)?.let {
                    it.amount += amount
                    updateCryptocurrencyInWalletUseCase(it)
                } ?: run {
                    val cryptocurrencyInWallet = CryptocurrencyInWallet(
                        id = itemCryptocurrencyInBuyCryptocurrencyDialog.id,
                        name = itemCryptocurrencyInBuyCryptocurrencyDialog.name,
                        amount = amount)
                    insertCryptocurrencyInWalletUseCase(cryptocurrencyInWallet)
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
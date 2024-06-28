package com.app.trialcryptowallet.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.trialcryptowallet.data.model.ERROR_CODE_RATE_LIMIT
import com.app.trialcryptowallet.data.model.Error
import com.app.trialcryptowallet.data.model.Result
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInChartScreen
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartData
import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartPeriod
import com.app.trialcryptowallet.data.model.entity.CryptocurrencyInWalletEntity
import com.app.trialcryptowallet.data.network.DAY
import com.app.trialcryptowallet.data.repository.RepositoryInterface
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChartViewModel(
    private val repository: RepositoryInterface
) : ViewModel() {

    private val _snackBarErrorMessage = MutableSharedFlow<Error>()
    val snackBarErrorMessage: SharedFlow<Error> = _snackBarErrorMessage.asSharedFlow()

    private val _itemCryptocurrencyInChartScreen = MutableStateFlow<ItemCryptocurrencyInChartScreen?>(null)
    val itemCryptocurrencyInChartScreen: StateFlow<ItemCryptocurrencyInChartScreen?> = _itemCryptocurrencyInChartScreen

    private val _chartPeriod = MutableStateFlow(ItemHistoricalChartPeriod(DAY))
    val chartPeriod: StateFlow<ItemHistoricalChartPeriod> = _chartPeriod

    private val _historicalChartData = MutableStateFlow<List<ItemHistoricalChartData>>(listOf())
    val historicalChartData: StateFlow<List<ItemHistoricalChartData>> = _historicalChartData

    private val _amount = MutableStateFlow(0.0)
    val amount: StateFlow<Double> = _amount

    private val _itemCryptocurrencyInBuyCryptocurrencyDialog = MutableStateFlow<ItemCryptocurrencyInBuyCryptocurrencyDialog?>(null)
    val itemCryptocurrencyInBuyCryptocurrencyDialog: StateFlow<ItemCryptocurrencyInBuyCryptocurrencyDialog?> = _itemCryptocurrencyInBuyCryptocurrencyDialog

    private val _itemCryptocurrencyInSellCryptocurrencyDialog = MutableStateFlow<ItemCryptocurrencyInSellCryptocurrencyDialog?>(null)
    val itemCryptocurrencyInSellCryptocurrencyDialog: StateFlow<ItemCryptocurrencyInSellCryptocurrencyDialog?> = _itemCryptocurrencyInSellCryptocurrencyDialog

    fun setCryptocurrency(newItemCryptocurrencyInChartScreen: ItemCryptocurrencyInChartScreen, newAmount: Double) {
        _itemCryptocurrencyInChartScreen.update { newItemCryptocurrencyInChartScreen }
        if (newAmount > 0) {
            _amount.update { newAmount }
        } else {
            viewModelScope.launch {
                repository.findCryptocurrencyInWalletById(newItemCryptocurrencyInChartScreen.id)?.let { cryptocurrencyInWalletEntity ->
                    _amount.update { cryptocurrencyInWalletEntity.amount }
                }
            }
        }
    }

    fun setSelectedPeriod(itemHistoricalChartPeriod: ItemHistoricalChartPeriod) {
        _chartPeriod.update { itemHistoricalChartPeriod }
    }
    fun fetchHistoricalChartData() {
        itemCryptocurrencyInChartScreen.value?.let { itemCryptocurrencyInChartScreen ->
            viewModelScope.launch {
                repository.getCoinHistoricalChartDataById(id = itemCryptocurrencyInChartScreen.id, days = chartPeriod.value.days).collect { resultCoinsHistoricalChartData ->
                    when (resultCoinsHistoricalChartData) {
                        is Result.Success -> {
                            val historicalChartData = mutableListOf<ItemHistoricalChartData>()
                            resultCoinsHistoricalChartData.data.prices.forEach {
                                historicalChartData.add(ItemHistoricalChartData(it[0].toLong(), it[1]))
                            }

                            _historicalChartData.update { historicalChartData }
                        }
                        is Result.Error -> {
                            if (resultCoinsHistoricalChartData.code == ERROR_CODE_RATE_LIMIT) {
                                _snackBarErrorMessage.emit(Error.ErrorRateLimit)
                            } else {
                                _snackBarErrorMessage.emit(Error.ErrorLoadingData)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getAvailableBalance() = repository.getAvailableBalance()

    fun onClickToBuy(itemCryptocurrencyInChartScreen: ItemCryptocurrencyInChartScreen) {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update { ItemCryptocurrencyInBuyCryptocurrencyDialog(itemCryptocurrencyInChartScreen.id, itemCryptocurrencyInChartScreen.name, itemCryptocurrencyInChartScreen.current_price) }
    }
    fun onDismissBuyCryptocurrencyDialog() {
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

    fun onClickToSell(itemCryptocurrencyInChartScreen: ItemCryptocurrencyInChartScreen) {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update { ItemCryptocurrencyInSellCryptocurrencyDialog(itemCryptocurrencyInChartScreen.id, itemCryptocurrencyInChartScreen.symbol, itemCryptocurrencyInChartScreen.name, itemCryptocurrencyInChartScreen.current_price, amount.value) }
    }
    fun onDismissSellCryptocurrencyDialog() {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update { null }
    }
    fun sellCryptocurrency(itemCryptocurrencyInSellCryptocurrencyDialog: ItemCryptocurrencyInSellCryptocurrencyDialog, amount: Double, price: Double, onComplete: () -> Unit) {
        if (itemCryptocurrencyInSellCryptocurrencyDialog.available_amount >= amount) {
            viewModelScope.launch {
                repository.setAvailableBalance(getAvailableBalance() + price)

                repository.findCryptocurrencyInWalletById(itemCryptocurrencyInSellCryptocurrencyDialog.id)?.let {
                    it.amount -= amount
                    if (it.amount >= 0) {
                        repository.updateCryptocurrencyInWallet(it)
                    } else {
                        repository.deleteCryptocurrencyInWallet(it)
                    }
                }

                onComplete.invoke()
            }
        }
    }

}
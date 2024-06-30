package com.app.trialcryptowallet.screens.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.trialcryptowallet.domain.model.common.ERROR_CODE_RATE_LIMIT
import com.app.trialcryptowallet.domain.model.common.Error
import com.app.trialcryptowallet.domain.model.common.Result
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInBuyCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInChartScreen
import com.app.trialcryptowallet.data.model.domain.ItemCryptocurrencyInSellCryptocurrencyDialog
import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartData
import com.app.trialcryptowallet.data.model.domain.ItemHistoricalChartPeriod
import com.app.trialcryptowallet.domain.model.common.Days
import com.app.trialcryptowallet.domain.model.db.CryptocurrencyInWallet
import com.app.trialcryptowallet.domain.repository.PreferencesRepository
import com.app.trialcryptowallet.domain.usecase.DeleteCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.FindCryptocurrencyInWalletByIdUseCase
import com.app.trialcryptowallet.domain.usecase.GetCoinHistoricalChartDataByIdUseCase
import com.app.trialcryptowallet.domain.usecase.InsertCryptocurrencyInWalletUseCase
import com.app.trialcryptowallet.domain.usecase.UpdateCryptocurrencyInWalletUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChartViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val getCoinHistoricalChartDataByIdUseCase: GetCoinHistoricalChartDataByIdUseCase,
    private val findCryptocurrencyInWalletByIdUseCase: FindCryptocurrencyInWalletByIdUseCase,
    private val updateCryptocurrencyInWalletUseCase: UpdateCryptocurrencyInWalletUseCase,
    private val insertCryptocurrencyInWalletUseCase: InsertCryptocurrencyInWalletUseCase,
    private val deleteCryptocurrencyInWalletUseCase: DeleteCryptocurrencyInWalletUseCase
) : ViewModel() {

    private val _snackBarErrorMessage = MutableSharedFlow<Error>()
    val snackBarErrorMessage: SharedFlow<Error> = _snackBarErrorMessage.asSharedFlow()

    private val _itemCryptocurrencyInChartScreen = MutableStateFlow<ItemCryptocurrencyInChartScreen?>(null)
    val itemCryptocurrencyInChartScreen: StateFlow<ItemCryptocurrencyInChartScreen?> = _itemCryptocurrencyInChartScreen

    private val _chartPeriod = MutableStateFlow(ItemHistoricalChartPeriod(Days.Day))
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
                findCryptocurrencyInWalletByIdUseCase(newItemCryptocurrencyInChartScreen.id)?.let { cryptocurrencyInWalletEntity ->
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
                getCoinHistoricalChartDataByIdUseCase(id = itemCryptocurrencyInChartScreen.id, days = chartPeriod.value.days).collect { resultCoinsHistoricalChartData ->
                    when (resultCoinsHistoricalChartData) {
                        is Result.Success -> {
                            val historicalChartData = mutableListOf<ItemHistoricalChartData>()
                            resultCoinsHistoricalChartData.data.prices.forEach {
                                historicalChartData.add(ItemHistoricalChartData(unixTime = it[0].toLong(), price = it[1]))
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

    fun getAvailableBalance() = preferencesRepository.getAvailableBalance()

    fun onClickToBuy(itemCryptocurrencyInChartScreen: ItemCryptocurrencyInChartScreen) {
        _itemCryptocurrencyInBuyCryptocurrencyDialog.update {
            ItemCryptocurrencyInBuyCryptocurrencyDialog(
                id = itemCryptocurrencyInChartScreen.id,
                name = itemCryptocurrencyInChartScreen.name,
                current_price = itemCryptocurrencyInChartScreen.current_price)
        }
    }
    fun onDismissBuyCryptocurrencyDialog() {
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

    fun onClickToSell(itemCryptocurrencyInChartScreen: ItemCryptocurrencyInChartScreen) {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update {
            ItemCryptocurrencyInSellCryptocurrencyDialog(
                id = itemCryptocurrencyInChartScreen.id,
                symbol = itemCryptocurrencyInChartScreen.symbol,
                name = itemCryptocurrencyInChartScreen.name,
                current_price = itemCryptocurrencyInChartScreen.current_price,
                available_amount = amount.value)
        }
    }
    fun onDismissSellCryptocurrencyDialog() {
        _itemCryptocurrencyInSellCryptocurrencyDialog.update { null }
    }
    fun sellCryptocurrency(itemCryptocurrencyInSellCryptocurrencyDialog: ItemCryptocurrencyInSellCryptocurrencyDialog, amount: Double, price: Double, onComplete: () -> Unit) {
        if (itemCryptocurrencyInSellCryptocurrencyDialog.available_amount >= amount) {
            viewModelScope.launch {
                preferencesRepository.setAvailableBalance(getAvailableBalance() + price)

                findCryptocurrencyInWalletByIdUseCase(itemCryptocurrencyInSellCryptocurrencyDialog.id)?.let {
                    it.amount -= amount
                    if (it.amount >= 0) {
                        updateCryptocurrencyInWalletUseCase(it)
                    } else {
                        deleteCryptocurrencyInWalletUseCase(it)
                    }
                }

                onComplete.invoke()
            }
        }
    }

}
package com.lukaslechner.coroutineusecasesonandroid.usecases.flow.usecase1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import timber.log.Timber

class FlowUseCase1ViewModel(
    private val stockPriceDataSource: StockPriceDataSource
) : BaseViewModel<UiState>() {

    val currentStockPriceAsLiveData: LiveData<UiState> =
        stockPriceDataSource.latestStockList
            .map { stockList ->
                UiState.Success(stockList) as UiState
            }

            .onStart {
                emit(UiState.Loading)
            }

            .onCompletion {
                Timber.tag("Flow").d("Flow collection completed")
            }
            .asLiveData()


    /*val currentStockPriceAsLiveData: MutableLiveData<UiState> = MutableLiveData()

    var job: Job? = null


    fun startFlowCollection() {
        //way 1
//        viewModelScope.launch {
//            stockPriceDataSource.latestStockList.collect { stockList ->
//                currentStockPriceAsLiveData.value = UiState.Success(stockList)
//            }
//        }

        //way2 using launching terminal operator

        job = stockPriceDataSource.latestStockList
            .map { stockList ->
                UiState.Success(stockList) as UiState
            }
            .onStart {
                Timber.d("Flow starts to be collected")
                emit(UiState.Loading)
            }
            .onCompletion {
                Timber.d("flow completion")

            }
            .onEach { uiState ->
                currentStockPriceAsLiveData.value = uiState
            }
            .launchIn(viewModelScope)
    }

    fun stopFlowCollection() {
        job?.cancel()
    }*/


}
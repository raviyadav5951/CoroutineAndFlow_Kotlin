package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase3

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class PerformNetworkRequestsConcurrentlyViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val oreoFeature = mockApi.getAndroidVersionFeatures(27)
                val pieFeature = mockApi.getAndroidVersionFeatures(28)
                val android10Feature = mockApi.getAndroidVersionFeatures(29)

                val versionFeatures = listOf(oreoFeature, pieFeature, android10Feature)
                uiState.value = UiState.Success(versionFeatures)
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message!!)
            }


        }
    }

    fun performNetworkRequestsConcurrently() {

        uiState.value = UiState.Loading

        val oreoFDeferred = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(27)
        }
        val pieFDeferred = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(28)
        }
        val android10FDeferred = viewModelScope.async {
            mockApi.getAndroidVersionFeatures(29)
        }


        viewModelScope.launch {
            try {
//                val oreo = oreoFDeferred.await()
//                val pie = pieFDeferred.await()
//                val a10 = android10FDeferred.await()
//
//                val result = listOf(oreo, pie, a10)

                val resultAwaitAll= awaitAll(oreoFDeferred,pieFDeferred,android10FDeferred)


                uiState.value = UiState.Success(resultAwaitAll)
            } catch (e: Exception) {
                uiState.value = UiState.Error(e.message!!)
            }

        }


    }
}
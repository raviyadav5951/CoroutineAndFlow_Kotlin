package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase4

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class VariableAmountOfNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequestsSequentially() {
        uiState.value=UiState.Loading

        viewModelScope.launch {
            try {

                val recentVersions=mockApi.getRecentAndroidVersions()

                val answer=recentVersions.map {
                    androidVersion ->
                    mockApi.getAndroidVersionFeatures(androidVersion.apiLevel)
                }
                uiState.value=UiState.Success(answer)
            }
            catch (e:Exception){
                uiState.value=UiState.Error(e.message!!)
            }
        }
    }

    fun performNetworkRequestsConcurrently() {
        uiState.value=UiState.Loading
        try {

            viewModelScope.launch {
                val recentVersions=mockApi.getRecentAndroidVersions()
                val answer=recentVersions.map {
                    androidVersion ->

                    async {
                        mockApi.getAndroidVersionFeatures(androidVersion.apiLevel)
                    }
                }.awaitAll()

                uiState.value=UiState.Success(answer)
            }
        }
        catch (e:Exception){
            uiState.value=UiState.Error(e.message!!)
        }
    }
}
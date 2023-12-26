package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase2

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class Perform2SequentialNetworkRequestsViewModel(
    private val mockApi: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun perform2SequentialNetworkRequest() {
        // TODO: Exercise 1
        // switch to branch "coroutine_course_full" to see solution

        try {
            uiState.value=UiState.Loading
            viewModelScope.launch {

                val mostRecentVersion=mockApi.getRecentAndroidVersions().last()
                val features=mockApi.getAndroidVersionFeatures(mostRecentVersion.apiLevel)

                uiState.value=UiState.Success(features)
            }
        }
        catch (e :Exception){
            Timber.e(e)
            uiState.value=UiState.Error(e.localizedMessage)
        }

    }
}
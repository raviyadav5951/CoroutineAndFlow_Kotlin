package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase5

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class NetworkRequestWithTimeoutViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest(timeout: Long) {

        usingWithTimeoutOrNull(timeout)
    }

    private fun usingWithTimeoutOrNull(timeout: Long) {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val versions = withTimeoutOrNull(timeout) {
                    api.getRecentAndroidVersions()
                }

                if (versions != null) {
                    uiState.value = UiState.Success(versions)
                } else {
                    uiState.value = UiState.Error("Network time out!")
                }

            } catch (e: Exception) {

                uiState.value = UiState.Error(e.localizedMessage!!)
            }

        }
    }

    fun usingWithTimeout(timeout: Long) {
        uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val versions = withTimeout(timeout) {
                    api.getRecentAndroidVersions()
                }

                uiState.value = UiState.Success(versions)
            } catch (e: TimeoutCancellationException) {
                uiState.value = UiState.Error("Network time out!")
            } catch (e: Exception) {

                uiState.value = UiState.Error(e.localizedMessage!!)
            }

        }
    }
}

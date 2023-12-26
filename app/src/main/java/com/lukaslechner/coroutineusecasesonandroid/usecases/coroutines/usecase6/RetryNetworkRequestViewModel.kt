package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase6

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber

class RetryNetworkRequestViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading

        viewModelScope.launch {

            // execution with normal repeat

            /*
            val numberRetries = 2
            try {
                repeat(numberRetries) {
                    try {
                        loadRecentVersions()
                        return@launch

                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
                loadRecentVersions()
            } catch (exception: Exception) {
                uiState.value = UiState.Error(exception.localizedMessage!!)
            }*/

            //After implementing own retry higher order function

            retry(2, {
                loadRecentVersions()
            })

        }
    }

    private suspend fun loadRecentVersions() {
        val versions = api.getRecentAndroidVersions()
        uiState.value = UiState.Success(versions)
    }

    //creating a common higher order function that will execute a block of function
    //with specified number of retries

    private suspend fun <T> retry(
        numOfRetries: Int,
        block: suspend () -> T,
        initialDelayInMillis: Long = 100,
        maxDelayInMillis: Long = 1000,
        factor: Double = 2.0
    ): T {

        var currentDelay = initialDelayInMillis

        repeat(numOfRetries) {
            try {
                return block()
            } catch (exception: Exception) {
                Timber.e(exception)
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtLeast(maxDelayInMillis)
        }

        return block()
    }


    private suspend fun <T> retryWithExponentialBackoff(
        numOfRetries: Int,
        block: suspend () -> T,
        initialDelayInMillis: Long = 100,
        maxDelayInMillis: Long = 1000,
        factor: Double = 2.0
    ): T {

        var currentDelay = initialDelayInMillis

        repeat(numOfRetries) {
            try {
                return block()
            } catch (exception: Exception) {
                Timber.e(exception)
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtLeast(maxDelayInMillis)
        }

        return block()
    }


}
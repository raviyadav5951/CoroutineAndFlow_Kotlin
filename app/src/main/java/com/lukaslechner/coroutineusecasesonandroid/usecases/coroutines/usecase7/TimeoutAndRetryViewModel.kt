package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import com.lukaslechner.coroutineusecasesonandroid.mock.VersionFeatures
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {


    //create a retry function block

    private suspend fun <T> retry(
        numberRetries: Int,
        block: suspend () -> T
    ): T {

        repeat(numberRetries) {
            try {
                return block()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return block()
    }

    private suspend fun <T> retryWithTimeout(
        numberRetries: Int,
        timeout: Long,
        block: suspend () -> T
    ) = retry(numberRetries = numberRetries) {
        withTimeout(timeout) {
            block()
        }

    }


    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        val numberOfRetries = 2
        val timeout = 1000L

        // TODO: Exercise 3
        // switch to branch "coroutine_course_full" to see solution

        // run api.getAndroidVersionFeatures(27) and api.getAndroidVersionFeatures(28) in parallel


        val oreoDeferred = viewModelScope.async {
            retryWithTimeout(2, timeout) {

                api.getAndroidVersionFeatures(27)
            }
        }


        val pieDeferred = viewModelScope.async {
            retryWithTimeout(2, timeout) {

                api.getAndroidVersionFeatures(28)
            }
        }


        viewModelScope.launch {

            try {

//                val result = listOf<VersionFeatures>(oreoDeferred.await(), pieDeferred.await())

                val result = listOf(oreoDeferred, pieDeferred).awaitAll()

                uiState.value = UiState.Success(result)

            } catch (ex: Exception) {
                Timber.e(ex)
                uiState.value = UiState.Error("Network failed")

            }

        }
    }


}
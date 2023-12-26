package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase13

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber
import kotlin.coroutines.coroutineContext

class ExceptionHandlingViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun handleExceptionWithTryCatch() {

        uiState.value = UiState.Loading
        viewModelScope.launch {

            try {
                api.getAndroidVersionFeatures(27)

            } catch (exception: Exception) {
                if (exception is HttpException) {
                    if (exception.code() == 500) {
                        //show error message 1
                    } else {

                        //show error message 1
                    }
                }
                uiState.value = UiState.Error("Network error:$exception")
            }
        }
    }

    fun handleWithCoroutineExceptionHandler() {

        uiState.value = UiState.Loading

        val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            uiState.value = UiState.Error("Network failed")
        }

        viewModelScope.launch(exceptionHandler) {
            api.getAndroidVersionFeatures(27)
        }

    }

    fun showResultsEvenIfChildCoroutineFails() {
        uiState.value = UiState.Loading


        viewModelScope.launch {

            //on using this code if fist fails then all coroutines get failed and exception is reached

            // Because its launch inside launch and exception is thrown from inside to launch
            // And since launch is having a default plain Job , it will cancel all the coroutines

            // To overcome this approach:
            // What we want: We want that two CRs continue even if first one fails
            // Check the below uncommented solution

            /* val oreoF = async {
                 api.getAndroidVersionFeatures(27)
             }

             val pieF = async {
                 api.getAndroidVersionFeatures(28)
             }

             val android10F = async {
                 api.getAndroidVersionFeatures(29)
             }

             val oreoFeatures =
                 try {
                     oreoF.await()
                 } catch (exception: Exception) {
                     Timber.e("oreo exception:$exception")
                     null
                 }

             val pieFeatures =
                 try {
                     pieF.await()
                 } catch (exception: Exception) {
                     Timber.e("pie exception:$exception")
                     null
                 }

             val android10Features =
                 try {
                     android10F.await()
                 } catch (exception: Exception) {
                     Timber.e("android10Features exception:$exception")
                     null
                 }

             val featuresList = listOfNotNull(oreoFeatures, pieFeatures, android10Features)

             uiState.value = UiState.Success(featuresList)
             */

            supervisorScope {
                val oreoFDeferred = async {
                    api.getAndroidVersionFeatures(27)
                }

                val pieFDeferred = async {
                    api.getAndroidVersionFeatures(28)
                }

                val android10FDeferred = async {
                    api.getAndroidVersionFeatures(29)
                }

                val featuresList = listOf(oreoFDeferred, pieFDeferred, android10FDeferred).map {

                    try {
                        it.await()
                    } catch (exception: Exception) {
                        if(exception is CancellationException){
                            throw exception
                        }
                        Timber.e("error loading data:$exception")
                        null
                    }
                }.filterNotNull()

                uiState.value = UiState.Success(featuresList)
            }

        }

    }
}
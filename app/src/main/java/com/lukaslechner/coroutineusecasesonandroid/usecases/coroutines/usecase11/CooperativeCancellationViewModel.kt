package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase11

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CooperativeCancellationViewModel(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private var calculationJob: Job? = null

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading
        calculationJob = viewModelScope.launch {

            try {


                var result = BigInteger.ONE
                var resultStr = ""

                val computationDuration = measureTimeMillis {
                    result = calculateFactorial(factorialOf)
                }

                val strConversionDuration = measureTimeMillis {
                    resultStr = withContext(context = Dispatchers.Main) {
                        result.toString()
                    }
                }

                uiState.value = UiState.Success(
                    resultStr, computationDuration = computationDuration,
                    stringConversionDuration = strConversionDuration
                )
            } catch (exception: Exception) {
                uiState.value = if (exception is CancellationException) {
                    UiState.Error("Calculation was cancelled")
                } else {
                    UiState.Error("Error while calculating result")
                }
            }


        }
    }

    fun cancelCalculation() {

        calculationJob?.cancel()
    }

    fun uiState(): LiveData<UiState> = uiState

    private val uiState: MutableLiveData<UiState> = MutableLiveData()

    private suspend fun calculateFactorial(number: Int): BigInteger {
        return withContext(context = Dispatchers.Default) {
            var factorial = BigInteger.ONE

            for (i in 1..number) {
                // yield enables cooperative cancellations
                // alternatives:
                // - ensureActive()
                // - isActive() - possible to do clean up tasks with

                yield()
                factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            }
            Timber.d("calculation factorial completed!!")
            factorial
        }


    }
}
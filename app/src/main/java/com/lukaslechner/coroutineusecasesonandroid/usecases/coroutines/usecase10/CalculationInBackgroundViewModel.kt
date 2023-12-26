package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase10

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import kotlin.system.measureTimeMillis

class CalculationInBackgroundViewModel : BaseViewModel<UiState>() {

    fun performCalculation(factorialOf: Int) {
        uiState.value = UiState.Loading
        viewModelScope.launch() {

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
        }
    }

    private suspend fun calculateFactorial(number: Int): BigInteger {
        return withContext(context = Dispatchers.Default) {
            var factorial = BigInteger.ONE

            for (i in 1..number) {
                factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            }

            factorial
        }


    }
}
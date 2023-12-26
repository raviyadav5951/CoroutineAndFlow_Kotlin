package com.lukaslechner.coroutineusecasesonandroid.playground.coroutinebuilders

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(){

//    GlobalScope.launch {
//        delay(500)
//        print("printed from within coroutine")
//    }


    runBlocking<Unit> {
        launch {
            delay(500)
            print("printed from within coroutine")
        }
    }
}
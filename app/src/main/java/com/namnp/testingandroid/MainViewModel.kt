package com.namnp.testingandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch

class MainViewModel(
) : ViewModel() {

    val countDownFlow = flow<Int> { // HOT
        val startingValue = 5
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }
//        .flowOn(dispatchers.main)

    private val _stateFlow = MutableStateFlow(0) // COLD
    val stateFlow = _stateFlow.asStateFlow()

    // one-time event, collector fires once, support multiple collectors
    // lost emissions if emit with no collectors -> if want cache emissions -> use replay in constructor (ex: replay = 4)
    // ex: won't trigger again: show snackBar, toast
    private val _sharedFlow = MutableSharedFlow<Int>(replay = 4) // COLD
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {

        // BAD: SharedFlow: lost events/emissions when emitting before collecting
        // if want to cache emissions -> use replace in ShareFlow constructor
//        squareNumber(3)
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW: The received number is $it")
            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number is $it")
            }
        }
        // GOOD
        squareNumber(3)
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    private fun flowOperators() {


        // onEach
//        1. countDownFlow.onEach {
//            println(it)
//        }.launchIn(viewModelScope)
//
//        2. viewModelScope.launch {
//            countDownFlow.collect {
//                println(it)
//            }
//        }
//        --> 1 = 2

        viewModelScope.launch {
            countDownFlow
                .filter { time ->
                    time % 2 == 0
                }
                .map { time ->
                    time * time
                }
                .onEach { time ->
                    println("Do nothing, just pre-process $time")
                }
                .collect { time ->
                    println("Time: $time")
                }
        }
    }

    fun terminalFlowOperators() {
        // take the whole result of the flow, all emission together -> do something with these

        viewModelScope.launch {
//            flowOf(1,2,3,4)
            val count = (1..4).asFlow()
                .count {
                    it % 2 == 0
                }
            println("The count is: $count") // 4+2 = 6
            val reduceSum = (1..4).asFlow()
                .reduce { accumulator, value ->
                    accumulator + value
                }
            println("The reduceSum is: $reduceSum") // 1+2+3+4 = 10

            val foldSum = (1..4).asFlow()
                .fold(5) { accumulator, value ->
                    accumulator + value
                }
            // only difference is the initial value
            println("The foldSum is: $reduceSum") // 5 + 1+2+3+4 = 15
        }
    }

    fun flatteningFlowOperators() {

    }

    private fun collectFlow() {
        // collect, buffer, conflate, collectLatest
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }
        // NORMAL COLLECT {}
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }.collect {
                println("FLOW: Now eating $it")
                delay(1500L)
                println("FLOW: Finished eating $it")
            }
        }
//        -> wait for consumer to finish consume before emit next values
//        FLOW: Appetizer is delivered
//        FLOW: Now eating Appetizer
//        FLOW: Finished eating Appetizer
//
//        FLOW: Main dish is delivered
//        FLOW: Now eating Main dish
//        FLOW: Finished eating Appetizer
//
//        FLOW: Dessert is delivered
//        FLOW: Now eating Dessert
//        FLOW: Finished eating Dessert

        // COLLECT WITH buffer {}
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }
                .buffer() // make consumer run on a separate coroutine from producer coroutine
                .collect {
                println("FLOW: Now eating $it")
                delay(1500L)
                println("FLOW: Finished eating $it")
            }
        }
//        not wait for consumer to consume, let consumer run on a separate coroutine from producer coroutine
//        FLOW: Appetizer is delivered
//        FLOW: Now eating Appetizer
//        FLOW: Main dish is delivered
//        FLOW: Dessert is delivered
//        FLOW: Finished eating Appetizer
//        FLOW: Now eating Main dish
//        FLOW: Finished eating Main dish
//        FLOW: Now eating Dessert
//        FLOW: Finished eating Dessert


        // COLLECT WITH conflate {}
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }
                .conflate() // let consumer run on a separate coroutine from producer coroutine
                .collect {
                    println("FLOW: Now eating $it")
                    delay(1500L)
                    println("FLOW: Finished eating $it")
                }
        }
//        similar to buffer, except: when finish consuming value, then go directly to the latest emission (drop/skip all previous emissions)
//        FLOW: Appetizer is delivered
//        FLOW: Now eating Appetizer
//        FLOW: Main dish is delivered
//        FLOW: Dessert is delivered
//        FLOW: Finished eating Appetizer
//  X:  skip      FLOW: Now eating Main dish
//  X:  skip      FLOW: Finished eating Main dish
//        FLOW: Now eating Dessert
//        FLOW: Finished eating Dessert


        // collectLatest
        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }.collectLatest {
                println("FLOW: Now eating $it")
                delay(1500L)
                println("FLOW: Finished eating $it")
            }
        }
//        -> only care about the last meals, don't care (skip/throw away) all previous meals
//        FLOW: Appetizer is delivered
//        FLOW: Now eating Appetizer
//        FLOW: Main dish is delivered
//        FLOW: Now eating Main dish
//        FLOW: Dessert is delivered
//        FLOW: Now eating Dessert
//        FLOW: Finished eating Dessert
//  X:  skip      FLOW: Finished eating Appetizer
//  X:  skip      FLOW: Finished eating Main dish
    }
}
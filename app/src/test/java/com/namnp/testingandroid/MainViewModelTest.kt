package com.namnp.testingandroid

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    private lateinit var testDispatchers: TestDispatchers

    @Before
    fun setUp() {
        testDispatchers = TestDispatchers()
        viewModel = MainViewModel(testDispatchers)
    }

    @Test
    fun `countDownFlow, properly counts downs from 5 to 0`() = runBlocking {
        viewModel.countDownFlow.test {
//            val emission = awaitItem() // wait flow to emit the first value
//            awaitItem() // wait for the second one

            for(i in 5 downTo 0) {
//                testDispatchers.testDispatcher.advanceTimeBy(1000L)
                testDispatchers.testDispatcher.scheduler.apply { advanceTimeBy(1000L); runCurrent() }
//                -> don't have to wait 1s to get result as in VM
                val emission = awaitItem()
                assertThat(emission).isEqualTo(i)
//                -> wait for 5s to see the result -> BAD -> use advanceTimeBy
//                -> run test fail, cause local unit tests run on JVM (not have main dispatcher as androidTest folder)
//                -> pass/inject dispatcher -> tests can run and skip delay()
            }
            // if there are emissions coming after the last awaitItem() -> exception -> use:
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `square number, get right output`() = runBlocking {
//        viewModel.sharedFlow.test {
//            val emission = awaitItem()
//            assertThat(emission).isEqualTo(9)
//        }
//        viewModel.squareNumber(3)
//        -> BAD: `viewModel.sharedFlow.test` block the test fun

        // GOOD: let the `viewModel.sharedFlow.test`runs on separate coroutine
        val job = launch { // 1
            println("A")
            viewModel.sharedFlow.test {
                val emission = awaitItem()
                assertThat(emission).isEqualTo(9)
                cancelAndConsumeRemainingEvents()
                println("D")
            }
        }
        viewModel.squareNumber(3) // 2
//        -> run both 1,2 simultaneously

        println("B")
        job.join()
        println("C")
        job.cancel()
//        -> B -> A -> D -> C
    }
}
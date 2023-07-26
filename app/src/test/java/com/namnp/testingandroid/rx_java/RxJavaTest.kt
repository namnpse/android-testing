package com.namnp.testingandroid.rx_java

import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.TestSubscriber
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit


@RunWith(JUnit4::class)
class RxJavaTest {

    // traditional way
    @Test
    fun `test zipWith normally`() {
        val letters = listOf("A", "B", "C", "D", "E")
        val results = arrayListOf<String>()
        val observable: Observable<String> = Observable
            .fromIterable(letters)
            .zipWith(
                Observable.range(1, Int.MAX_VALUE)
            ) { string, index -> "$index-$string" }

        observable.subscribe(results::add)

        assertThat(results, notNullValue())
        assert(results.size == 5)
        assertThat(results, hasItems("1-A", "2-B", "3-C", "4-D", "5-E"))
    }

    // using TestSubscriber
    @Test
    fun `test using TestObserver`() {
        val letters = listOf("A", "B", "C", "D", "E")
//        val subscriber = TestSubscriber<String>()
        val subscriber = TestObserver<String>()

        val observable: Observable<String> = Observable
            .fromIterable(letters)
            .zipWith(
                Observable.range(1, Int.MAX_VALUE)
            ) { string, index -> "$index-$string" }

        observable.subscribe(subscriber)

//        subscriber.assertCompleted()
        subscriber.assertComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(5)
        assertThat(
//            subscriber.getOnNextEvents(),
            subscriber.values(),
            hasItems("1-A", "2-B", "3-C", "4-D", "5-E")
        )
    }

    // Testing Expected Exceptions
    @Test
    fun `testing expected exceptions`() {
        val letters = listOf("A", "B", "C", "D", "E")
//        val subscriber = TestSubscriber<String>()
        val subscriber = TestObserver<String>()

        val observable: Observable<String> = Observable
            .fromIterable(letters)
            .zipWith(
                Observable.range(1, Int.MAX_VALUE)
            ) { string, index -> "$index-$string" }
            .concatWith(Observable.error(RuntimeException("error in Observable")))

        observable.subscribe(subscriber)

        subscriber.assertError(RuntimeException::class.java)
//        subscriber.assertNotCompleted()
        subscriber.assertNotComplete()
    }

    // Testing Time-Based Observable
    @Test
    fun `test time-based observable`() {
        val letters = listOf("A", "B", "C", "D", "E")
        val scheduler = TestScheduler()
//        val subscriber = TestSubscriber<String>()
        val subscriber = TestObserver<String>()

        // emit new values every second
        val tick = Observable.interval(1, TimeUnit.SECONDS, scheduler)

        val observable: Observable<String> = Observable.fromIterable(letters)
            .zipWith(tick) { string, index -> "$index-$string" }

        observable.subscribeOn(scheduler)
            .subscribe(subscriber)

        // At the beginning of a test, at time zero, TestSubscriber will not be completed
        subscriber.assertNoValues()
        subscriber.assertNotComplete()

        // To emulate time passing in test -> use a TestScheduler class.
        // Can simulate that one-second pass by calling the advanceTimeBy() method on a TestScheduler
        // advanceTimeBy() method makes an observable produce one event.
        // Assert that one event was produced by calling an assertValueCount() method
        scheduler.advanceTimeBy(1, TimeUnit.SECONDS)

        subscriber.assertNoErrors()
        subscriber.assertValueCount(1)
        subscriber.assertValues("0-A")

        // letters has 5 elements -> want to cause an observable to emit all events, 6 seconds of processing needs to pass.
        // to emulate that 6 seconds, we use the advanceTimeTo() method
        scheduler.advanceTimeTo(6, TimeUnit.SECONDS)

        subscriber.assertComplete()
        subscriber.assertNoErrors()
        subscriber.assertValueCount(5)
//        assertThat(subscriber.getOnNextEvents(), hasItems("0-A", "1-B", "2-C", "3-D", "4-E"));
        assertThat(subscriber.values(), hasItems("0-A", "1-B", "2-C", "3-D", "4-E"));
    }
}
package com.namnp.testingandroid.mockk

import com.google.common.truth.Truth.assertThat
import com.namnp.testingandroid.feature.mockk.Dependency1
import com.namnp.testingandroid.feature.mockk.Dependency2
import com.namnp.testingandroid.feature.mockk.SystemUnderTest
import io.mockk.Runs
import io.mockk.called
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import io.mockk.verifySequence
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.math.abs


class SystemUnderTestMockk {

    @Test // using Mockk
    fun calculateSumUsingMockk() {
        val doc1 = mockk<Dependency1>() // mock -> fake logic/implementation using stubs
        val doc2 = mockk<Dependency2>()
        val sut = SystemUnderTest(doc1, doc2) // without mock -> real logic/implementation

        every { doc1.value1 }   returns 6
        every { doc2.value2 }   returns "8"

        assertThat(sut.calculate()).isEqualTo(14) // Google Truth lib
        assert(sut.calculate() == 14) // JUnit lib

    }

    @Test // using Mockito
    fun calculateSumUsingMockito() {
        val doc1 = mock<Dependency1>() // Mockito lib = Mockito.mock(Dependency1::class.java)
        val doc2 = mock<Dependency2>()
        val sut = SystemUnderTest(doc1, doc2)

        doReturn(6).`when`(doc1).value1 // C1: use `` for when (it's Mockito's when, not Kotlin's)
        Mockito.`when`(doc2.value2).thenReturn("8") // C2: alternative way

        assertThat(sut.calculate()).isEqualTo(14) // Google Truth lib
        assert(sut.calculate() == 14) // JUnit lib

    }

    // Argument matching and Expected Answers
    // 1. Argument matching
    @Test
    fun `test argument matcher for abs(number) fun using Mockk`() {
        val doc = mockk<Dependency1>() // mock -> fake logic/implementation using stubs

        // return fixed values (1, -1) using "returns"
        /*every { doc.calculateAbs(or(more(0), eq(0))) } returns 1
        every { doc.calculateAbs(less(0)) } returns -1*/

        /*  return the argument (dynamic values) passed in using "answers"
            if the argument >= 0, return the argument
            else return the abs of the argument
        */
        every { doc.calculateAbs(less(0)) } answers {
            val arg = firstArg<Int>()
            arg * (-1) // Return the abs of argument
        }
        every { doc.calculateAbs(or(more(0), eq(0))) } answers {
            val arg = firstArg<Int>()
            arg // Return the argument directly
        }

        assertThat(doc.calculateAbs(-5)).isEqualTo(5)
        assertThat(doc.calculateAbs(-4)).isEqualTo(4)
        assertThat(doc.calculateAbs(8)).isEqualTo(8)
    }

    // 2. Expected answers
    @Test
    fun `test expected answers for abs(number) fun using Mockk`() {
        val doc = mockk<Dependency1>() // mock -> fake logic/implementation using stubs

        /*
            C1: "returnsMany": specify a number of values that are used one by one
            i.e: first matched call returns first element,
                 second — second element,
                 e.t.c.
            C2: using returns + andThen
        */
        every { doc.calculateAbs(6) } returnsMany listOf(1,2,3)
        every { doc.calculateAbs(6) } returns 1 andThen 2 andThen 3
        // throws throw an exception if the call is matched
        every { doc.calculateAbs(-6) } throws RuntimeException("Invalid argument")
        // used in case return value is Unit
        every { doc.callReturningUnit(10) } just Runs
        // answers allow specifying custom lambda function returning an answer.
        // arg<Int>(0)/firstArg() stands for the value of the first argument (index: 0,1,2,...)
        every { doc.calculateAbs(10) } answers { arg<Int>(0) }
        every { doc.calculateAbs(10) } answers { firstArg() }
    }

    // 3. Behavior verification (verify function call)
    // 4 types: unordered, ordered, sequential and all
    @Test
    fun calculateSumAndVerifyFunctionsCall() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()
        val doc3 = mockk<Dependency2>()

        every { doc1.value1 } returns 5 // in "every" block, only 1 call is allowed.
        every { doc2.value2 } returns "6"
        every { doc1.calculateAbs(any()) }  returns 1

        val sut = SystemUnderTest(doc1, doc2)
        sut.callOneFunctionInDependency1()

        assertEquals(sut.calculate(), 11)
        assertThat(sut.calculate()).isEqualTo(11)
        // Unlike in "every", in "verify" block -> can have several calls going one after another.
        // 1. Unordered
        verify {
            doc1.value1
            doc2.value2
            doc1.calculateAbs(5) // check if calculateAbs(5) is called at least once.
        }
        // By default, atLeast is 1 and atMost is Int.MAX_VALUE
        verify(atLeast = 1, atMost = 10) {
            doc1.calculateAbs(5)
        }
        verify(exactly = 2) {// call 2 times since sut.calculate() 2 times assert
            doc1.value1
        }

        verify(exactly = 0) {
            doc1.functionNeverCalled(any())
        }
        // used for mock object call, not a function call
        verify { doc3 wasNot called }

        /* 2. All
        verifyAll same as verify
        except it additionally checks that all matched calls are the only calls happened to mentioned mocks.
        similar to `verifyNoMoreInteractions` in Mockito.
        i.e: only calculateAbs and value1 are called, no other functions are called in doc1 anymore.
        */
        verifyAll {
            doc1.calculateAbs(5)
            doc1.value1
        }

        every { doc2.doWork1() } just Runs
        every { doc2.doWork2() } just Runs
        every { doc2.doWork3() } just Runs

        sut.doWork() // Work 1 -> 2 -> 3
        // verify that the exact sequence happened -> strict order
        verifySequence {
            doc2.value2
            doc2.value2
            doc2.doWork1()
            doc2.doWork2()
            doc2.doWork3()
        }
        // verifies only the order of calls that happened,
        // allowing gaps in the sequence of calls. (missing doWork2() is ok)
        verifyOrder {
            doc2.doWork1()
            doc2.doWork3()
        }
    }

}
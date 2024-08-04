package com.namnp.testingandroid.mockk

import com.google.common.truth.Truth.assertThat
import com.namnp.testingandroid.feature.mockk.Dependency1
import com.namnp.testingandroid.feature.mockk.Dependency2
import com.namnp.testingandroid.feature.mockk.SystemUnderTest
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock


class SystemUnderTestMockk {

    @Test // using Mockk
    fun calculateSumUsingMockk() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()
        val sut = SystemUnderTest(doc1, doc2)

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
}
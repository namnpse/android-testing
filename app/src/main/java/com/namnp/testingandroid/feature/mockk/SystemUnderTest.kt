package com.namnp.testingandroid.feature.mockk

import kotlin.math.abs

// Dependent-On Component (DOC)
class Dependency1(val value1: Int) {
    fun calculateAbs(number: Int) = abs(number)
    fun callReturningUnit(number: Int) {}
    fun functionNeverCalled(number: Int) {}
}
class Dependency2(val value2: String) {
    fun doWork1() {}
    fun doWork2() {}
    fun doWork3() {}
}

// System Under Test (SUT)
class SystemUnderTest(
    val dependency1: Dependency1,
    val dependency2: Dependency2
) {

    /* init {
        callOneFunctionInDependency1()
    } */

    fun calculate() = dependency1.value1 + (dependency2.value2.toIntOrNull() ?: 0)

    fun callOneFunctionInDependency1() {
        dependency1.calculateAbs(5)
    }

    fun doWork() {
        dependency2.doWork1()
        dependency2.doWork2()
        dependency2.doWork3()
    }
}
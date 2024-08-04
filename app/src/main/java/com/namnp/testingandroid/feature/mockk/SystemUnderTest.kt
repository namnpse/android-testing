package com.namnp.testingandroid.feature.mockk

import kotlin.math.abs

// Dependent-On Component (DOC)
class Dependency1(val value1: Int) {
    fun calculateAbs(number: Int) = abs(number)
    fun callReturningUnit(number: Int) {}
}
class Dependency2(val value2: String)

// System Under Test (SUT)
class SystemUnderTest(
    val dependency1: Dependency1,
    val dependency2: Dependency2
) {
    fun calculate() = dependency1.value1 + (dependency2.value2.toIntOrNull() ?: 0)
}
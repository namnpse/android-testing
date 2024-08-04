package com.namnp.testingandroid.feature.mockk

// Dependent-On Component (DOC)
class Dependency1(val value1: Int)
class Dependency2(val value2: String)

// System Under Test (SUT)
class SystemUnderTest(
    val dependency1: Dependency1,
    val dependency2: Dependency2
) {
    fun calculate() = dependency1.value1 + (dependency2.value2.toIntOrNull() ?: 0)
}
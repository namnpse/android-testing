package com.namnp.testingandroid.feature.flow


// interface for heavy computation job, which returns the string
interface HeavyComputationRepository {
    suspend fun doComputation(): String
    fun doNormalComputation(): String
}
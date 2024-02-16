package com.namnp.testingandroid.feature.flow

sealed class VmState {
    object Waiting: VmState()
    object Running: VmState()
    data class Finished(val data: String): VmState()
}
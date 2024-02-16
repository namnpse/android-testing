package com.namnp.testingandroid.feature.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel
class MultipleFlowViewModel
//@Inject constructor
 (
    private val computationRepo: HeavyComputationRepository,
) : ViewModel() {

    private val _vmState: MutableStateFlow<VmState> = MutableStateFlow(VmState.Waiting)
    val vmState = _vmState.asStateFlow()

    // duplicate StateFlow to track
    private val _secondVmState: MutableStateFlow<VmState> = MutableStateFlow(VmState.Waiting)
    val secondVmState = _secondVmState.asStateFlow()

    fun onEvent(event: VmEvent): Job = when (event) {
        VmEvent.OnLaunch -> onLaunch()
    }

    private fun onLaunch() = viewModelScope.launch(Dispatchers.Main) {
        // running the jobs asynchronously
        awaitAll(
            async { processFirstJob() },
            async { processSecondJob() }
        )
    }

    private suspend fun processFirstJob() {
        _vmState.value = VmState.Running
        val result = computationRepo.doComputation()
        _vmState.value = VmState.Finished(result)
        _vmState.value = VmState.Waiting
    }

    private suspend fun processSecondJob() {
        _secondVmState.value = VmState.Running
        val result = computationRepo.doComputation()
        _secondVmState.value = VmState.Finished(result)
        _secondVmState.value = VmState.Waiting
    }

}
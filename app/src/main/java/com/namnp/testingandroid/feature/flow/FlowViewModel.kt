package com.namnp.testingandroid.feature.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Hilt annotations are not needed in the example
// because we will manually inject mocks - in real app with hilt, they are essential
//@HiltViewModel
// if want to use Hilt and Inject, need to write a fake repo that implements HeavyComputationRepository
// then swap the fake repo with real repo in Module class
class FlowViewModel
//@Inject constructor
    (
    private val computationRepo: HeavyComputationRepository,
) : ViewModel() {

    private val _vmState: MutableStateFlow<VmState> = MutableStateFlow(VmState.Waiting)
    val vmState = _vmState.asStateFlow()

    fun onEvent(event: VmEvent): Job = when (event) {
        VmEvent.OnLaunch -> onLaunch()
    }

    private fun onLaunch() = viewModelScope.launch(Dispatchers.Main) {
        _vmState.value = VmState.Running
        val result = computationRepo.doComputation()
        _vmState.value = VmState.Finished(result)
        _vmState.value = VmState.Waiting
    }

}
package com.namnp.testingandroid.flow

import app.cash.turbine.test
import com.namnp.testingandroid.feature.flow.FlowViewModel
import com.namnp.testingandroid.feature.flow.HeavyComputationRepository
import com.namnp.testingandroid.feature.flow.VmEvent
import com.namnp.testingandroid.feature.flow.VmState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.stub

@RunWith(JUnit4::class)
class FlowViewModelUsingTurbineTest {

    // 1.
    @Mock
    lateinit var repository: HeavyComputationRepository
    /* An interface HeavyComputationRepository abstracts the logic for the ViewModel.
      Since it is an interface, the dependency can be mocked with annotation @Mock at the beginning
      without any specific implementation or stubs.*/
    // if don't want to mock, write TestRepository and swap implementation in Hilt Module

    // 2.
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Test
    fun `Given the sut is initialized, then it waits for event`() {

        // 3.
        val viewModel = FlowViewModel(repository)

        // 4.
        assertTrue(viewModel.vmState.value == VmState.Waiting)
    }

    @Test
    fun `Given the ViewModel waits - When the event OnLaunch comes, then execute heavy computation with result`() =
        // 1. runTest is similar to runBlocking, but it skips delay within the coroutine
        runTest {
            // ARRANGE
            val expectedString = "Namnpse"
            // 2. prepares the mock to expect the call on itself (behavior)
            repository.stub {
                // doComputation is suspend -> use onBlocking
                onBlocking { doComputation() } doAnswer { expectedString }
                // if not suspend fun -> use `on`
                on { doNormalComputation() } doAnswer { expectedString }
            }
            val viewModel = FlowViewModel(repository)

            // 3.
            viewModel.vmState.test {

                // ACTION
                viewModel.onEvent(VmEvent.OnLaunch)

                // CHECK
                // 4.
                assertEquals(awaitItem(), VmState.Waiting)
                assertEquals(awaitItem(), VmState.Running)
                assertEquals(awaitItem(), VmState.Finished(expectedString))
                assertEquals(awaitItem(), VmState.Waiting)

                // the test will finish on its own, because of lambda usage
            }
        }

}
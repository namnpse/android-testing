package com.namnp.testingandroid.flow

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.namnp.testingandroid.feature.flow.FlowViewModel
import com.namnp.testingandroid.feature.flow.HeavyComputationRepository
import com.namnp.testingandroid.feature.flow.MultipleFlowViewModel
import com.namnp.testingandroid.feature.flow.VmEvent
import com.namnp.testingandroid.feature.flow.VmState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.stub

/*
- Error: Module with the Main dispatcher had failed to initialize.
  For tests Dispatchers.setMain from kotlinx-coroutines-test
- Reason: In code, the app uses the main thread by calling the Dispatchers.Main.
  However, the unit test does not provide any main thread.
- Solution: To go around it, the main thread can be mocked by setting the test dispatcher as the main dispatcher for coroutines.
  It has to be called before executing the test and disposed after finishing the test as follows:
@Before
fun setUp() {
    // setting up test dispatcher as main dispatcher for coroutines
    Dispatchers.setMain(StandardTestDispatcher())
}

@After
fun tearDown() {
    // removing the test dispatcher
    Dispatchers.resetMain()
}*/

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
//    val dispatcherRule = MainCoroutineRule() // use custom rule
//    val dispatcherRule = StandardDispatcherRule() // use custom rule

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        // setting up test dispatcher as main dispatcher for coroutines
        Dispatchers.setMain(StandardTestDispatcher())
    }

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

    @Test
    fun `Given the ViewModel waits - When the event OnLaunch comes, then both computations runs successfully`() =
        runTest {
            // testing with multiple state -> need to use `turbineScope`
            // Every state needs to be tested in the test case, can construct a receiver
            turbineScope {
                // ARRANGE
                val expectedString = "Result"
                repository.stub {
                    onBlocking { doComputation() } doAnswer { expectedString }
                }

                // sut: System Under Test
                val sut = MultipleFlowViewModel(repository)

                val firstStateReceiver = sut.vmState.testIn(backgroundScope) // similar to .stateIn(viewModelScope)
                val secondStateReceiver = sut.secondVmState.testIn(backgroundScope)

                // ACTION
                sut.onEvent(VmEvent.OnLaunch)

                // CHECK
                assertEquals(VmState.Waiting, firstStateReceiver.awaitItem())
                assertEquals(VmState.Waiting, secondStateReceiver.awaitItem())

                assertEquals(VmState.Running, firstStateReceiver.awaitItem())
                assertEquals(VmState.Running, secondStateReceiver.awaitItem())

                assertEquals(VmState.Finished(expectedString), firstStateReceiver.awaitItem())
                assertEquals(VmState.Finished(expectedString), secondStateReceiver.awaitItem())

                assertEquals(VmState.Waiting, firstStateReceiver.awaitItem())
                assertEquals(VmState.Waiting, secondStateReceiver.awaitItem())

                // cancel or complete the flows at the end of the test.
                // Otherwise, the test will hang/timeout will fail the test.
                firstStateReceiver.cancel()
                secondStateReceiver.cancel()
            }
        }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
    fun tearDown() {
        // removing the test dispatcher
        Dispatchers.resetMain()
    }
}
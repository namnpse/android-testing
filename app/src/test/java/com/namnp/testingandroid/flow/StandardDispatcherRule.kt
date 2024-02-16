package com.namnp.testingandroid.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// This setup can be extracted into a rule as below to make it reusable across tests
@OptIn(ExperimentalCoroutinesApi::class)
class StandardDispatcherRule: TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(StandardTestDispatcher())
        super.starting(description)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
        super.finished(description)
    }

}
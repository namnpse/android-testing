@file:OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)

package com.namnp.testingandroid.fire_store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.namnp.testingandroid.feature.fire_store.UserViewModel
import com.namnp.testingandroid.utils.LiveDataTestUtil
import com.namnp.testingandroid.utils.testUser
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.namnp.testingandroid.R

class UserViewModelTest {

    private lateinit var userViewModel: UserViewModel
    private val mainThread = newSingleThreadContext("Main Thread")

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThread)
        val userRepository = FakeUserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)

    }

    @Test
    fun getUserTest() = runTest {
        userViewModel.getUserInformation(testUser.id)
        val user = LiveDataTestUtil.getValue(userViewModel.user)
        assertEquals(user.id, testUser.id)
        assertEquals(user.username, testUser.username)
    }

    @Test
    fun createUserTest() = runTest {
        userViewModel.createUserToFireStore(testUser.username, testUser.email, testUser.phoneNumber)
        val message = LiveDataTestUtil.getValue(userViewModel.errorMessage)
        assertEquals(message, R.string.user_created)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThread.close()
    }
}
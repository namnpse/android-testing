package com.namnp.testingandroid.feature.fire_store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.namnp.testingandroid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import com.namnp.testingandroid.feature.utils.Result
import java.util.UUID

class UserViewModel(private val userRepository: UserRepository) : ViewModel(), CoroutineScope {

    // set coroutine context
    private val compositeJob = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + compositeJob

    // -- Coroutine jobs
    private var getUserJob: Job? = null
    private var createUserJob: Job? = null

    // -- Live data
    private val _errorMessage = MutableLiveData<Int>()
    val errorMessage: LiveData<Int> = _errorMessage

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun getUserInformation(userId: String) {
        // cancel previous job if any
        if (getUserJob?.isActive == true) getUserJob?.cancel()
        getUserJob = launch {
            when (val result = userRepository.getUserFromFireStore(userId)) {
                is Result.Success -> _user.value = result.data
                is Result.Error -> _errorMessage.value = R.string.error_fetching
                is Result.Canceled -> _errorMessage.value = R.string.canceled
            }
        }
    }

    fun createUserToFireStore(
        username: String = "Bryan",
        email: String = "namnpse@gmail.com",
        phoneNumber: String = "09382323xxx"
    ) {
        val id = UUID.randomUUID().toString()
        val user = User(
            id = id,
            username = username,
            phoneNumber = phoneNumber,
            email = email,
        )
        // cancel previous job if any
        if (createUserJob?.isActive == true) createUserJob?.cancel()
        createUserJob = launch {
            when (userRepository.createUserInFireStore(user)) {
                is Result.Success -> _errorMessage.value = R.string.user_created
                is Result.Error -> _errorMessage.value = R.string.error_creating
                is Result.Canceled -> _errorMessage.value = R.string.canceled
            }
        }
    }
}
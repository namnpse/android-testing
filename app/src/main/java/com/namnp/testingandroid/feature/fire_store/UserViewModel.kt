package com.namnp.testingandroid.feature.fire_store

import androidx.lifecycle.ViewModel

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private fun getUserInformation(userId: String){
        userRepository.getUserFromFireStore(userId)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){

                } else {

                }
            }
    }

    private fun createUserToFireStore(user: User){
        userRepository.createUserInFireStore(user)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){

                } else {

                }
            }
    }
}
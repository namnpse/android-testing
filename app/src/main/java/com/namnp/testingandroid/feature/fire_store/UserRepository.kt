package com.namnp.testingandroid.feature.fire_store

import com.google.android.gms.tasks.Task

interface UserRepository {
    fun createUserInFireStore(user: User): Task<Void>

    fun getUserFromFireStore(userId: String): Task<User>
}
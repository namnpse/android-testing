package com.namnp.testingandroid.feature.fire_store

import com.google.android.gms.tasks.Task
import com.namnp.testingandroid.feature.utils.Result

interface UserRepository {
    suspend fun createUserInFireStore(user: User): Result<Void>

    suspend fun getUserFromFireStore(userId: String): Result<User?>
}
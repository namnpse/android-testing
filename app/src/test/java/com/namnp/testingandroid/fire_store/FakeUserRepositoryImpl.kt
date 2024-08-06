package com.namnp.testingandroid.fire_store

import com.namnp.testingandroid.feature.fire_store.User
import com.namnp.testingandroid.feature.fire_store.UserRepository
import com.namnp.testingandroid.feature.utils.Result
import com.namnp.testingandroid.utils.testUser

open class FakeUserRepositoryImpl : UserRepository {

    override suspend fun getUserFromFireStore(userId: String): Result<User> = Result.Success(testUser)

    override suspend fun createUserInFireStore(user: User): Result<Void?> = Result.Success(null)

}
package com.namnp.testingandroid.feature.fire_store

import com.google.firebase.firestore.FirebaseFirestore
import com.namnp.testingandroid.feature.utils.await
import com.namnp.testingandroid.feature.utils.Result

class UserRepositoryImpl: UserRepository {

    private val USER_COLLECTION_NAME = "users_collection"

    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection(USER_COLLECTION_NAME)

    override suspend fun createUserInFireStore(user: User): Result<Void> {
        return userCollection.document(user.id).set(user).await()
    }

    override suspend fun getUserFromFireStore(userId: String): Result<User?>{
        val result = userCollection.document(userId).get().await()
        return when(result){
            is Result.Success -> {
                val user = result.data.toObject(User::class.java)
                Result.Success(user)
            }
            is Result.Error -> Result.Error(result.exception)
            is Result.Canceled -> Result.Canceled(result.exception)
        }
    }
}
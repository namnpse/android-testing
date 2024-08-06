package com.namnp.testingandroid.feature.fire_store

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl: UserRepository {

    private val USER_COLLECTION_NAME = "users"

    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection(USER_COLLECTION_NAME)

    override fun createUserInFireStore(user: User): Task<Void> {
        return userCollection.document(user.id).set(user)
    }

    override fun getUserFromFireStore(userId: String): Task<User>{
        val documentSnapshot = userCollection.document(userId).get()
        return documentSnapshot.continueWith {
            if (documentSnapshot.isSuccessful){
                return@continueWith documentSnapshot.result?.toObject(User::class.java)
            } else {
                return@continueWith null
            }
        }

    }
}
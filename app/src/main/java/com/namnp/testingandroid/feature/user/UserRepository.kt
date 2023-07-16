package com.namnp.testingandroid.feature.user

import javax.inject.Inject

class UserRepository @Inject constructor(val apiService: UserService){
    suspend fun getPersonalDetailsApi() = apiService.getPersonalDetails()
}
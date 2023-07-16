package com.namnp.testingandroid.feature.user

import retrofit2.Response
import retrofit2.http.GET

interface UserService {

    @GET("personalDetails.json")
    suspend fun getPersonalDetails(): Response<PersonalDetailsResponse>
}
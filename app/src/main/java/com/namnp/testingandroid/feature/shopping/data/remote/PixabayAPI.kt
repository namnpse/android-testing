package com.namnp.testingandroid.feature.shopping.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.namnp.testingandroid.BuildConfig

interface PixabayAPI {

    @GET("/api/")
    suspend fun searchForImage(
        @Query("q") searchQuery: String,
        @Query("key") apiKey: String = BuildConfig.API_KEY
    ): Response<ImageResponse>
}
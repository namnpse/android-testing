package com.namnp.testingandroid.feature.live_data

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {

    @GET("/users/{username}")
    fun getGithubAccountObservable(
        @Path("username") username: String,
    ): Observable<Response<GithubAccount>>
}
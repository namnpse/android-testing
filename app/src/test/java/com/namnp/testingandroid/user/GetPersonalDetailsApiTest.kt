package com.namnp.testingandroid.user

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser.*
import com.namnp.testingandroid.feature.user.UserRepository
import com.namnp.testingandroid.feature.user.UserService
import com.namnp.testingandroid.user.util.MockResponseFileReader
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(JUnit4::class)
class GetPersonalDetailsApiTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val server = MockWebServer()
    private lateinit var repository: UserRepository
    private lateinit var mockedResponse: String
    private lateinit var gson: Gson

    @Before
    fun init() {
        gson = GsonBuilder()
            .setLenient()
            .create()
        server.start(8000)
        var BASE_URL = server.url("/").toString()
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val service = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
            .create(UserService::class.java)
        repository = UserRepository(service)
    }

    @Test
    fun testApiSuccess() {
        mockedResponse = MockResponseFileReader("user/success_response.json").content
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
        val response = runBlocking { repository.apiService.getPersonalDetails() }
        val json = gson.toJson(response.body())
        val resultResponse = parseString(json)
        val expectedResponse = parseString(mockedResponse)
        Assert.assertNotNull(response)
        Assert.assertTrue(resultResponse.equals(expectedResponse))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}
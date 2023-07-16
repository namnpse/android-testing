package com.namnp.testingandroid.feature.user

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule{
    var BASE_URL = "https://raw.githubusercontent.com/GaneshDivekar/RetrofitWithUnitTest/master/"
    @Provides
    fun provideBaseUrl() = BASE_URL
    @Singleton
    @Provides
//    fun provideOkHttpClient() = if (BuildConfig.DEBUG){
    fun provideOkHttpClient(): OkHttpClient = if (true){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }else{
        OkHttpClient
            .Builder()
            .build()
    }
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL:String): Retrofit =
        Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)
}
package com.example.safetymanagement2022.di

import com.example.safetymanagement2022.data.remote.model.request.CreateBuildingRequest
import com.example.safetymanagement2022.data.remote.model.request.LoginRequest
import com.example.safetymanagement2022.data.remote.model.request.RegisterRequest
import com.example.safetymanagement2022.data.remote.model.response.LoginResponse
import com.example.safetymanagement2022.data.remote.model.response.RegisterResponse
import com.example.safetymanagement2022.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiClient {
    // accounts
    @POST("?")
    suspend fun postLogin(@Body body: LoginRequest): LoginResponse
    @POST("?")
    suspend fun postRegister(@Body body: RegisterRequest): RegisterResponse

    // ApiClient 객체 생성
    companion object {

        private const val baseUrl = "http://ec2-15-164-166-79.ap-northeast-2.compute.amazonaws.com"

        fun create(): ApiClient {
            // okhttp에서 로그 확인용 client
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            // addConverterFactory: http 응답 결과을 프로젝트에서 사용하는 객체로 변환하는 방법
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiClient::class.java)
        }
    }
}
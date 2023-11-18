package com.example.flo

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://edu-api-test.softsquared.com"

fun getRetrofit(): Retrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    // 레트로핏 객체를 리턴
    return retrofit
}
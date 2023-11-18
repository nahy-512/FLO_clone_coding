package com.example.flo

import android.util.Log
import android.view.View
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    fun signUp(user: User) {

        val authService = getRetrofit().create(AuthProfileInterface::class.java)

        authService.signUp(user).enqueue(object: Callback<AuthResponse> {
            // 응답이 왔을 때 실행하는 부분
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                Log.d("SIGNUP", response.body().toString())
                val resp: AuthResponse = response.body()!!
                when (resp.code) {
                    1000, 2016, 2018 -> signUpView.onSignUpSuccess(resp) // 문제 없이 성공한 경우
                    else -> signUpView.onSignUpFailure() // 의도한 실패인 경우
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString())
            }
        })
        Log.d("SIGNUP", "HELLO")
    }
}
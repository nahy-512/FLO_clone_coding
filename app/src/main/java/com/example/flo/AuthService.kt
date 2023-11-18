package com.example.flo

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var signUpView: SignUpView
    private lateinit var loginView: LoginView
    private lateinit var splashView: SplashView

    fun setSignUpView(signUpView: SignUpView) {
        this.signUpView = signUpView
    }

    fun setLoginView(loginView: LoginView) {
        this.loginView = loginView
    }

    fun setSplashView(splashView: SplashView) {
        this.splashView = splashView
    }

    fun signUp(user: User) {

        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        authService.signUp(user).enqueue(object: Callback<AuthResponse> {
            // 응답이 왔을 때 실행하는 부분
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("SIGNUP/SUCCESS", response.toString())
                Log.d("SIGNUP", response.body().toString())
                val resp: AuthResponse = response.body()!!
                when (resp.code) {
                    1000 -> signUpView.onSignUpSuccess(resp) // 문제 없이 성공한 경우
                    else -> signUpView.onSignUpFailure(resp.message) // 의도한 실패인 경우
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("SIGNUP/FAILURE", t.message.toString())
            }
        })
        Log.d("SIGNUP", "HELLO")
    }

    fun login(user: User) {

        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        authService.login(user).enqueue(object: Callback<AuthResponse> {
            // 응답이 왔을 때 실행하는 부분
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("LOGIN/SUCCESS", response.toString())
                Log.d("LOGIN", response.body().toString())
                val resp: AuthResponse = response.body()!!
                when (val code = resp.code) {
                    1000 -> loginView.onLoginSuccess(code, resp.result!!)
                    else -> loginView.onLoginFailure(resp.message)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("LOGIN/FAILURE", t.message.toString())
            }
        })
    }

    fun autoLogin(jwt: String) {

        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        authService.autoLogin(jwt).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("AUTO-LOGIN/SUCCESS", response.toString())
                val resp: AuthResponse = response.body()!!
                when (resp.code) {
                    1000 -> splashView.onAutoLoginSuccess()
                    else -> splashView.onAutoLoginFailure(resp.message) // 자동 로그인에 실패한 경우
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Log.d("AUTO-LOGIN/FAILURE", t.message.toString())
            }
        })
    }
}
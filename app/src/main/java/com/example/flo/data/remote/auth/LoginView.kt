package com.example.flo.data.remote.auth

import com.example.flo.data.remote.auth.Result

interface LoginView {
    fun onLoginSuccess(code: Int, result: Result)
    fun onLoginFailure(message: String)
}

interface SplashView {
    fun onAutoLoginSuccess()
    fun onAutoLoginFailure(message: String)
}
package com.example.flo

interface LoginView {
    fun onLoginSuccess(code: Int, result: Result)
    fun onLoginFailure(message: String)
}

interface SplashView {
    fun onAutoLoginSuccess()
    fun onAutoLoginFailure(message: String)
}
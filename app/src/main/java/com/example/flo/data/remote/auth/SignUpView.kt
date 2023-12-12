package com.example.flo.data.remote.auth

import com.example.flo.data.remote.auth.AuthResponse

interface SignUpView { // Activity와 Auth Service 연결시켜주기 위한 것
    //TODO: 로딩 상태 추가
    fun onSignUpSuccess(response: AuthResponse)
    fun onSignUpFailure(message: String)
}
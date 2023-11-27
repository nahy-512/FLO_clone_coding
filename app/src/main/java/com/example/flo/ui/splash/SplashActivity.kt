package com.example.flo.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.data.remote.AuthService
import com.example.flo.databinding.ActivitySplashBinding
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.signin.LoginActivity
import com.example.flo.ui.signin.SplashView

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity(), SplashView {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        autoLogin()

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
        }, 1000)
    }

    private fun autoLogin() {
        val authService = AuthService()
        authService.setSplashView(this)
        authService.autoLogin(getJwt().toString())
    }

    private fun getJwt(): String? {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)

        return spf!!.getString("jwt", "")
    }

    override fun onAutoLoginSuccess() {
        Toast.makeText(this, "자동 로그인 성공!", Toast.LENGTH_SHORT).show()
        // 성공 시 메인 액티비티로 이동
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onAutoLoginFailure(message: String) {
        Log.d("SplashActivity", message)
        // 실패 시 로그인 화면으로 이동
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
package com.example.flo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity(), LoginView {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.loginCloseIv.setOnClickListener {
            finish()
        }
        /* 회원가입 */
        binding.loginSignupTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        /* 로그인 */
        binding.loginLoginBtn.setOnClickListener {
            // 로그인 진행
            login()
        }
    }

    private fun login() {
        // 이메일
        if (binding.loginEmailIdEt.text.isEmpty() ||  binding.loginEmailSelectEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        // 비밀번호
        if (binding.signupPwdEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        // 사용자가 입력한 이메일, 비밀번호
        val email: String = binding.loginEmailIdEt.text.toString() + "@" + binding.loginEmailSelectEt.text.toString()
        val password: String = binding.signupPwdEt.text.toString()

        // 8주차 (roomDB)
//        // DB 사용자 정보 확인
//        val userDB = SongDatabase.getInstance(this)!!
//        val user = userDB.userDao().getUser(email, pwd)
//
//        // 사용자 정보가 DB에 있다면(DB에서 유저를 찾았다면) 로그인 진행
//        user?.let {
//            Toast.makeText(this, "로그인에 성공하셨습니다!", Toast.LENGTH_SHORT).show()
//            Log.d("LoginActivity", "userId: ${user.id}, $user")
////            saveJwt(user.id)
//            // 로그인이 완료됐다면 메인 화면으로 이동
//            startMainActivity()
//            return
//        }
//        // 찾지 못했다면 토스트 메시지 출력
//        Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()

        // 9주차 (서버 연동)
        val authService = AuthService()
        authService.setLoginView(this)
        // API 호출
        authService.login(User(email, password, ""))
    }

    // 8주차 (roomDB)
//    private fun saveJwt(jwt: Int) {
//        val spf = getSharedPreferences("auth", MODE_PRIVATE)
//        val editor = spf.edit()
//
//        // jwt 저장
//        editor.putInt("jwt", jwt).apply()
//    }

    // 9주차 (서버 연동)
    private fun saveJwt(userIdx: Int, jwt: String) {
        val spf = getSharedPreferences("auth", MODE_PRIVATE)
        val editor = spf.edit()

        // jwt 저장
        editor
            .putInt("userIdx", userIdx)
            .putString("jwt", jwt)
            .apply()
    }

    private fun startMainActivity() {
        finish()
//        startActivity( Intent(this, MainActivity::class.java))
    }

    override fun onLoginSuccess(code: Int, result: Result) {
        when (code) {
            1000 -> {
                Toast.makeText(this, "로그인에 성공하셨습니다!", Toast.LENGTH_SHORT).show()
                saveJwt(result.userIdx, result.jwt)
                startMainActivity()
            }
        }
    }

    override fun onLoginFailure(message: String) {
        // 실패 처리
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
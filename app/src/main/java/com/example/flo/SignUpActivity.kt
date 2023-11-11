package com.example.flo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySignupBinding

class SignUpActivity: AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initClickListener()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.signupBackIv.setOnClickListener {
            finish()
        }
        /* 회원가입 */
        binding.signupDoneBtn.setOnClickListener {
            signUp()
            finish()
        }
    }

    private fun getUser(): User {
        // 사용자가 입력한 이메일, 비밀번호
        val email: String = binding.signupEmailIdEt.text.toString() + "@" + binding.signupEmailSelectEt.text.toString()
        val pwd: String = binding.signupPwdEt.text.toString()

        return User(email, pwd)
    }

    private fun signUp() {
        // 이메일
        if (binding.signupEmailIdEt.text.isEmpty() ||  binding.signupEmailSelectEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // 비밀번호
        if (binding.signupPwdEt.text.toString() != binding.signupPwdCheckEt.text.toString()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 정보를 DB에 저장
        val userDB = SongDatabase.getInstance(this)!!
        userDB.userDao().insert(getUser())

        // 잘 저장되었는지 확인
        val user = userDB.userDao().getAllUsers()
        Log.d("SignupActivity", user.toString())

        Toast.makeText(this, "회원 가입에 성공하셨습니다!", Toast.LENGTH_SHORT).show()
    }
}
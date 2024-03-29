package com.smk.wherewasi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smk.wherewasi.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var userEditText: EditText
    private lateinit var passEditText: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignup: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        initViews()

        setBtnListeners()

        initObservers()

    }

    private fun initViews() {
        btnLogin = findViewById(R.id.btn_login)
        btnSignup = findViewById(R.id.btn_signup)
        userEditText = findViewById(R.id.edt_txt_username)
        passEditText = findViewById(R.id.edt_txt_password)
    }

    private fun setBtnListeners() {
        val username = userEditText.text.toString()
        val password = passEditText.text.toString()

        btnLogin.setOnClickListener {
            viewModel.onLoginClicked(username, password)
        }

        btnSignup.setOnClickListener {
            viewModel.onSignupClicked(username, password)
        }
    }

    private fun initObservers() {
        observerLoginResult()
        observeSignupResult()
    }

    private fun observerLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            if (result == "Success") {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeSignupResult(){
        viewModel.signupResult.observe(this){result ->
            Toast.makeText(this,result,Toast.LENGTH_SHORT).show()
        }
    }
}
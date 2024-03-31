package com.smk.wherewasi.view.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.smk.wherewasi.R
import com.smk.wherewasi.model.MyRealm
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

        //if (MyRealm.getLoggedInUser() != null) startMainActivity()
        if (MyRealm.getLoggedInUser() != null) startDrawerActivity()
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
        btnLogin.setOnClickListener {
            viewModel.onLoginClicked(userEditText.text.toString(), passEditText.text.toString())
        }

        btnSignup.setOnClickListener {
            viewModel.onSignupClicked(userEditText.text.toString(), passEditText.text.toString())
        }
    }

    private fun initObservers() {
        observerLoginResult()
        observeSignupResult()
    }

    private fun observerLoginResult() {
        viewModel.loginResult.observe(this) { result ->
            if (result == "Success") {
                //startMainActivity()
                startDrawerActivity()
            } else {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMainActivity() { //TODO: remove MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun startDrawerActivity() {
        val intent = Intent(this, DrawerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun observeSignupResult() {
        viewModel.signupResult.observe(this) { result ->
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "Login Activity"
    }
}

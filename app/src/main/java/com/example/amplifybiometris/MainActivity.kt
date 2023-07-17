package com.example.amplifybiometris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button

    private lateinit var edtLoginUser: EditText
    private lateinit var edtLoginPass: EditText
    private lateinit var btnLogIn: Button

    private lateinit var edtCode: EditText
    private lateinit var btnConfirmCode: Button

    private val authHelper by lazy {
        AuthHelper()
    }

    private val localStorageHelper by lazy {
        LocalStorageHelper(this)
    }

    private var username: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authHelper.validateSession { hasSession ->
            if (hasSession)
                goToWelcome(localStorageHelper.getUsername())
        }

        if (localStorageHelper.getUsername().isNotEmpty() &&
            localStorageHelper.getPassword().isNotEmpty()) {
            if (isBiometricSupported()) {
                showBiometricPrompt()
            }
        }

        edtUsername = findViewById(R.id.edt_username)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btn_signUp)

        edtLoginUser = findViewById(R.id.edt_login_username)
        edtLoginPass = findViewById(R.id.edt_login_password)
        btnLogIn = findViewById(R.id.btn_login)

        edtCode = findViewById(R.id.edt_code)
        btnConfirmCode = findViewById(R.id.btn_confirm)

        btnSignUp.setOnClickListener {
            username = edtUsername.text.toString()
            password = edtPassword.text.toString()

            authHelper.registerUser(
                username = edtUsername.text.toString(),
                password = edtPassword.text.toString(),
                email = edtEmail.text.toString(),
                ::handleRegistration
            )
        }

        btnLogIn.setOnClickListener {
            username = edtLoginUser.text.toString()
            password = edtLoginPass.text.toString()

            authHelper.login(
                username = edtLoginUser.text.toString(),
                password = edtLoginPass.text.toString(),
                ::handleAuthorization
            )
        }
    }

    private fun handleAuthorization(isCompleted: Boolean?) {
        runOnUiThread {
            when(isCompleted) {
                true -> {
                    if (isBiometricSupported()) {
                        showBiometricPrompt()
                    }
                }
                else -> showMessage("An unexpected error happens, try again!")
            }
        }
    }

    private fun handleRegistration(isCompleted: Boolean) {
        runOnUiThread {
            if (isCompleted) {
                edtCode.visibility = View.VISIBLE
                btnConfirmCode.visibility = View.VISIBLE

                btnConfirmCode.setOnClickListener {
                    authHelper.confirmUser(
                        username = edtUsername.text.toString(),
                        code = edtCode.text.toString(),
                        ::handleAuthorization
                    )
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // Handle authentication error
                    showMessage("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // Handle authentication success
                    showMessage("Authentication succeeded!")
                    if (localStorageHelper.getUsername().isEmpty() &&
                        localStorageHelper.getPassword().isEmpty()) {

                        localStorageHelper.setUsername(username)
                        localStorageHelper.setPassword(password)

                        goToWelcome(username)
                    } else {
                        authHelper.login(
                            username = localStorageHelper.getUsername(),
                            password = localStorageHelper.getPassword(),
                        ) {
                            if (it == true) goToWelcome(username)
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Handle authentication failure
                    showMessage("Authentication failed.")
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        when (canAuthenticate) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // The user can authenticate with biometrics, continue with the authentication process
                return true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE, BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE, BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                // Handle the error cases as needed in your app
                return false
            }

            else -> {
                // Biometric status unknown or another error occurred
                return false
            }
        }
    }

    private fun goToWelcome(username: String) {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.putExtra(DATA_USERNAME, username)
        startActivity(intent)
    }
}
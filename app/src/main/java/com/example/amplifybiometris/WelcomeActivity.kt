package com.example.amplifybiometris

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

const val DATA_USERNAME = "DATA_USERNAME"
class WelcomeActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var btnLogout: Button

    private val authHelper by lazy {
        AuthHelper()
    }

    private val localStorageHelper by lazy {
        LocalStorageHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        tvTitle = findViewById(R.id.tv_welcome)
        btnLogout = findViewById(R.id.btn_logout)

        val username = intent.getStringExtra(DATA_USERNAME)
        tvTitle.text = "Welcome $username"

        btnLogout.setOnClickListener {
            authHelper.logOut {
                if (it) {
                    localStorageHelper.setUsername("")
                    localStorageHelper.setPassword("")
                    this.finish()
                }
            }
        }
    }
}
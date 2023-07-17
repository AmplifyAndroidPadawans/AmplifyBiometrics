package com.example.amplifybiometris

import android.content.Context

const val USERNAME = "USERNAME"
const val PASSWORD = "PASSWORD"

class LocalStorageHelper(private val context: Context) {

    private val sharedPref by lazy {
        context.getSharedPreferences("AmplifyBiometris", Context.MODE_PRIVATE)
    }

    fun setUsername(username: String) {
        with(sharedPref.edit()) {
            putString(USERNAME, username)
            apply()
        }
    }

    fun getUsername(): String = sharedPref.getString(USERNAME, "") ?: ""

    fun setPassword(password: String) {
        with(sharedPref.edit()) {
            putString(PASSWORD, password)
            apply()
        }
    }

    fun getPassword(): String = sharedPref.getString(PASSWORD, "") ?: ""

}
package com.example.amplifybiometris

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify

class AmplifyBiometrisApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext)
            Log.i("AmplifyBiometris", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("AmplifyBiometris", "Could not initialize Amplify", error)
        }
    }
}
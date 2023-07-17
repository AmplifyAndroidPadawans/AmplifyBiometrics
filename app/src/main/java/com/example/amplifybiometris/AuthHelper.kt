package com.example.amplifybiometris

import android.util.Log
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

class AuthHelper() {

    fun registerUser(
        username: String,
        password: String,
        email: String,
        completion: (isCompleted: Boolean) -> Unit,
    ) {
        val options = AuthSignUpOptions.builder()
            .userAttribute(AuthUserAttributeKey.email(), email)
            .build()
        Amplify.Auth.signUp(username, password, options,
            {
                completion(true)
                Log.i("CognitoHelper", "Sign up succeeded: $it")
            },
            {
                completion(false)
                Log.e ("CognitoHelper", "Sign up failed", it)
            }
        )
    }

    fun confirmUser(
        username: String,
        code: String,
        completion: (isCompleted: Boolean?) -> Unit,
    ) {
        Amplify.Auth.confirmSignUp(
            username, code,
            { result ->
                if (result.isSignUpComplete) {
                    completion(true)
                    Log.i("CognitoHelper", "Confirm signUp succeeded")
                } else {
                    completion(false)
                    Log.i("CognitoHelper","Confirm sign up not complete")
                }
            },
            {
                completion(null)
                Log.e("CognitoHelper", "Failed to confirm sign up", it)
            }
        )
    }

    fun validateSession(completion: (hasSession: Boolean) -> Unit) {
        Amplify.Auth.fetchAuthSession(
            {
                Log.i("AmplifyQuickstart", "Auth session = $it")
                completion(it.isSignedIn)
            },
            {
                    error -> Log.e("AmplifyQuickstart", "Failed to fetch auth session", error)
                completion(false)
            }
        )
    }

    fun login(
        username: String,
        password: String,
        completion: (isCompleted: Boolean?) -> Unit,
    ) {
        Amplify.Auth.signIn(username, password,
            { result ->
                if (result.isSignedIn) {
                    Log.i("AuthQuickstart", "Sign in succeeded")
                    completion(true)
                } else {
                    Log.i("AuthQuickstart", "Sign in not complete")
                    completion(false)
                }
            },
            {
                Log.e("AuthQuickstart", "Failed to sign in", it)
                completion(null)
            }
        )
    }

    fun logOut(
        completion: (isCompleted: Boolean) -> Unit,
    ) {
        Amplify.Auth.signOut { signOutResult ->
            when(signOutResult) {
                is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                    // Sign Out completed fully and without errors.
                    Log.i("AuthQuickStart", "Signed out successfully")
                    completion(true)
                }
                is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                    // Sign Out completed with some errors. User is signed out of the device.
                    signOutResult.hostedUIError?.let {
                        Log.e("AuthQuickStart", "HostedUI Error", it.exception)
                        // Optional: Re-launch it.url in a Custom tab to clear Cognito web session.

                    }
                    signOutResult.globalSignOutError?.let {
                        Log.e("AuthQuickStart", "GlobalSignOut Error", it.exception)
                        // Optional: Use escape hatch to retry revocation of it.accessToken.
                    }
                    signOutResult.revokeTokenError?.let {
                        Log.e("AuthQuickStart", "RevokeToken Error", it.exception)
                        // Optional: Use escape hatch to retry revocation of it.refreshToken.
                    }
                    completion(false)
                }
                is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                    // Sign Out failed with an exception, leaving the user signed in.
                    Log.e("AuthQuickStart", "Sign out Failed", signOutResult.exception)
                    completion(false)
                }
            }
        }
    }
}
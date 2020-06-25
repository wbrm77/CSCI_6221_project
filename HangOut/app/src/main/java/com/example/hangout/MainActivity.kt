package com.example.hangout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {


    private lateinit var username: EditText

    private lateinit var password: EditText

    private lateinit var login: Button

    private lateinit var signUp: Button

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        login.setOnClickListener {
            firebaseAnalytics.logEvent("login_clicked", null)

            val inputtedUsername: String = username.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()

            firebaseAuth
                .signInWithEmailAndPassword(inputtedUsername, inputtedPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        firebaseAnalytics.logEvent("login_success", null)

                        val currentUser: FirebaseUser? = firebaseAuth.currentUser
                        val email = currentUser?.email
                        Toast.makeText(this, "Logged in as $email", Toast.LENGTH_SHORT).show()


                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(inputtedUsername,inputtedPassword)

                        val exception = task.exception

                        // Example of logging some extra metadata (the error reason) with our analytic
                        val reason = if (exception is FirebaseAuthInvalidCredentialsException) "invalid_credentials" else "connection_failure"
                        val bundle = Bundle()
                        bundle.putString("error_type", reason)

                        firebaseAnalytics.logEvent("login_failed", bundle)

                        Toast.makeText(this, "Registration failed: $exception", Toast.LENGTH_SHORT).show()

                    }
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                }
        }
    }
}



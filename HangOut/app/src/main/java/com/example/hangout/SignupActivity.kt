package com.example.hangout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var username: EditText

    private lateinit var password: EditText

    private lateinit var name: EditText

    private lateinit var signup: Button

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        name = findViewById(R.id.name)
        signup = findViewById(R.id.signup)

        val intent = intent
        val inputtedUsername: String? = intent.getStringExtra("inputtedUsername")
        val inputtedPassword: String? = intent.getStringExtra("inputtedPassword")

        if (inputtedUsername != null) {
            username.setText(inputtedUsername)
        }

        if (inputtedPassword != null) {
            password.setText(inputtedPassword)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        signup.setOnClickListener {
            val newUsername: String = username.text.toString().trim()
            val newPassword: String = password.text.toString().trim()
            val newName: String = name.text.toString().trim()

            firebaseAuth
                .createUserWithEmailAndPassword(newUsername, newPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                Toast.makeText(this, "Created user: ${user!!.email}",Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
            } else {
                val exception = task.exception
                Toast.makeText(this, "Failed: $exception", Toast.LENGTH_SHORT).show() }
        }
        }
    }
}
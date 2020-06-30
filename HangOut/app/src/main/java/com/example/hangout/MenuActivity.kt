package com.example.hangout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    private lateinit var map1: Button
    private lateinit var map2: ImageView
    private lateinit var friends: Button
    private lateinit var friends_icon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        map1 = findViewById(R.id.map)
        map2 = findViewById(R.id.map_icon)
        friends = findViewById(R.id.friends)
        friends_icon = findViewById(R.id.friends_icon)

        map1.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        map2.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        friends.setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }
        friends_icon.setOnClickListener {
            val intent = Intent(this, FriendsActivity::class.java)
            startActivity(intent)
        }
    }
}

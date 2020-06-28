package com.example.hangout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView


    private lateinit var firebaseDatabase: FirebaseDatabase

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        recyclerView = findViewById(R.id.recycler)

        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = FriendsAdapter(getFakeFriends())

        val reference = firebaseDatabase.getReference("friends")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                firebaseAnalytics.logEvent("tweets_retrieval_failed", null)
                Toast.makeText(
                    this@FriendsActivity,
                    "Failed to retrieve friends: $error!",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onDataChange(data: DataSnapshot) {
                // Log an analytic with how many Tweets were retrieved
                val bundle = Bundle()
                val numFriends = data.children.count()
                bundle.putInt("count", numFriends)
                firebaseAnalytics.logEvent("friends_retrieval_success", bundle)

                val friends = mutableListOf<Friend>()
                data.children.forEach { child ->
                    val friend = child.getValue(Friend::class.java)
                    if (friend != null) {
                        friends.add(friend)
                    }
                }

                recyclerView.adapter = FriendsAdapter(friends)
            }
        })
    }

    fun getFakeFriends(): Array<Friend> {
        return arrayOf(
            Friend(
                userId = "1",
                name = "Billy Miller",
                email = "billy@gwu.edu",
                location = "Tiki TNT",
                friends = emptyList()
            ),
            Friend(
                userId = "1",
                name = "Allison DeCicco",
                email = "allison@gwu.edu",
                location = "Tonic",
                friends = emptyList()
            ),
            Friend(
                userId = "1",
                name = "Kyle Rood",
                email = "kyle@gwu.edu",
                location = "Bills",
                friends = emptyList()
            ),
            Friend(
                userId = "1",
                name = "Suraj Sha",
                email = "suraj@gwu.edu",
                location = "Tonic",
                friends = emptyList()
            ),
            Friend(
                userId = "1",
                name = "Ben Fernandez",
                email = "ben@gwu.edu",
                location = "hill country",
                friends = emptyList()
            )
        )
    }
}

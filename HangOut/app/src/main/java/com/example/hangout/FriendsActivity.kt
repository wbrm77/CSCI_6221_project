package com.example.hangout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FriendsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        viewManager = LinearLayoutManager(this)
        viewAdapter = FriendsAdapter(getFakeFriends())

        recyclerView = findViewById<RecyclerView>(R.id.recycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

        }
    }

    fun getFakeFriends(): Array<Friend> {
        return arrayOf(
            Friend(
                userId = "1",
                name = "Billy Miller",
                email = "billy@gwu.edu",
                location = "Tiki TNT",
                friends = emptyArray()
            ),
            Friend(
                userId = "1",
                name = "Allison DeCicco",
                email = "allison@gwu.edu",
                location = "Tonic",
                friends = emptyArray()
            ),
            Friend(
                userId = "1",
                name = "Kyle Rood",
                email = "kyle@gwu.edu",
                location = "Bills",
                friends = emptyArray()
            ),
            Friend(
                userId = "1",
                name = "Suraj Sha",
                email = "suraj@gwu.edu",
                location = "Tonic",
                friends = emptyArray()
            ),
            Friend(
                userId = "1",
                name = "Ben Fernandez",
                email = "ben@gwu.edu",
                location = "hill country",
                friends = emptyArray()
            )
        )
    }
}

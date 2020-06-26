package com.example.hangout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendsAdapter(private val dataset: Array<Friend>) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        // Open & parse our XML file
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_friends, parent, false)

        // Create a new ViewHolder
        return FriendsViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = dataset[position]

        holder.name.text = friend.name
        holder.email.text = friend.email
        holder.location.text = friend.location

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size

    class FriendsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.name)

        val email: TextView = view.findViewById(R.id.username)

        val location: TextView = view.findViewById(R.id.location)
    }
}

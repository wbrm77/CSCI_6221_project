package com.example.hangout

import java.io.Serializable


    data class FriendsList (
        val name: String,
        val checkedIn: String
    ): Serializable {
        // Required by Firebase to cast to a custom object
        constructor() : this("","")
    }

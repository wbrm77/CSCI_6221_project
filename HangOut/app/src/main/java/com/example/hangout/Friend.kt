package com.example.hangout

data class Friend (
    val userId: String,
    val name: String,
    val email: String,
    val location: String,
    val friends: List<String>
) {
    // Required by Firebase to cast to a custom object
    constructor() : this("","","","", emptyList())
}
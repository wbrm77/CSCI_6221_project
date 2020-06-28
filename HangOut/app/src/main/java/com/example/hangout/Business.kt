package com.example.hangout

data class Business(
    val name: String,
    val rating: Int,
    val bId: String,
    val url: String,
    val lat: Double,
    val long: Double,
    var checkin: Boolean
)
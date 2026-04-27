package com.example.vrp.order.domain

import com.example.vrp.shared.Location

data class Customer(
    val id: String,
    val name: String,
    val contactEmail: String,
    val location: Location,
)

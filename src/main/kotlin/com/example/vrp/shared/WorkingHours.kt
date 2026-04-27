package com.example.vrp.shared

import java.time.LocalTime

data class WorkingHours(
    val start: LocalTime,
    val end: LocalTime,
) {
    init {
        require(!start.isAfter(end)) { "WorkingHours start must not be after end" }
    }
}

package com.example.vrp.shared

import java.time.LocalDateTime

data class TimeWindow(
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    init {
        require(!start.isAfter(end)) { "TimeWindow start must not be after end" }
    }

    fun overlaps(other: TimeWindow): Boolean =
        start.isBefore(other.end) && end.isAfter(other.start)
}

package com.vrp.domain.model

/**
 * Geographic location with latitude and longitude coordinates.
 */
data class Location(
    val latitude: Double,
    val longitude: Double
) {
    init {
        require(latitude in MIN_LATITUDE..MAX_LATITUDE) { "Latitude must be between -90 and 90" }
        require(longitude in MIN_LONGITUDE..MAX_LONGITUDE) { "Longitude must be between -180 and 180" }
    }

    private companion object {
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
    }
}

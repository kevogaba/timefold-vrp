package com.vrp.solver.distance

import com.vrp.domain.model.Location
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Euclidean (Haversine) distance calculator using great-circle distance formula.
 */
class EuclideanDistanceCalculator : DistanceCalculator {
    companion object {
        private const val EARTH_RADIUS_METERS = 6_371_000.0 // Earth's radius in meters
    }

    override fun distanceBetween(
        a: Location,
        b: Location
    ): Long {
        val lat1Rad = Math.toRadians(a.latitude)
        val lat2Rad = Math.toRadians(b.latitude)
        val deltaLat = Math.toRadians(b.latitude - a.latitude)
        val deltaLon = Math.toRadians(b.longitude - a.longitude)

        val haversineA =
            sin(deltaLat / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2)

        val centralAngle = 2 * asin(sqrt(haversineA))

        return (EARTH_RADIUS_METERS * centralAngle).roundToLong()
    }
}

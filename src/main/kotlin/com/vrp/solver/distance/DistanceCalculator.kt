package com.vrp.solver.distance

import com.vrp.domain.model.Location

/**
 * Interface for calculating distances between locations.
 */
interface DistanceCalculator {
    /**
     * Calculate distance between two locations in meters.
     */
    fun distanceBetween(a: Location, b: Location): Long
}

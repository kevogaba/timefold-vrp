package com.vrp.solver.distance

import com.vrp.domain.model.Location
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class EuclideanDistanceCalculatorTest {

    private val calculator = EuclideanDistanceCalculator()

    @Test
    fun `should calculate distance between two locations`() {
        val sanFrancisco = Location(37.7749, -122.4194)
        val oakland = Location(37.8044, -122.2712)

        val distance = calculator.distanceBetween(sanFrancisco, oakland)

        // Distance should be approximately 13-14 km (13000-14000 meters)
        assertTrue(distance > 12000 && distance < 15000, "Expected distance ~13km, got ${distance}m")
    }

    @Test
    fun `should return zero for same location`() {
        val location = Location(37.7749, -122.4194)

        val distance = calculator.distanceBetween(location, location)

        assertTrue(distance == 0L, "Expected 0, got $distance")
    }

    @Test
    fun `should calculate distance across equator`() {
        val north = Location(1.0, 0.0)
        val south = Location(-1.0, 0.0)

        val distance = calculator.distanceBetween(north, south)

        // Should be approximately 222 km
        assertTrue(distance > 200000 && distance < 230000, "Expected ~222km, got ${distance}m")
    }

    @Test
    fun `should calculate distance across prime meridian`() {
        val west = Location(0.0, -1.0)
        val east = Location(0.0, 1.0)

        val distance = calculator.distanceBetween(west, east)

        // Should be approximately 222 km
        assertTrue(distance > 200000 && distance < 230000, "Expected ~222km, got ${distance}m")
    }
}

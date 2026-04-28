package com.vrp.solver.distance

import com.vrp.domain.model.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EuclideanDistanceCalculatorTest {
    private val calculator = EuclideanDistanceCalculator()

    @Test
    fun `should calculate distance between same location as zero`() {
        val location = Location(40.7128, -74.0060)

        val distance = calculator.distanceBetween(location, location)

        assertThat(distance).isEqualTo(0L)
    }

    @Test
    fun `should calculate distance between New York and Los Angeles approximately`() {
        val newYork = Location(40.7128, -74.0060)
        val losAngeles = Location(34.0522, -118.2437)

        val distance = calculator.distanceBetween(newYork, losAngeles)

        // Expected distance: approximately 3,944 km = 3,944,000 meters
        // Allow 1% tolerance for rounding
        assertThat(distance).isBetween(3_900_000L, 4_000_000L)
    }

    @Test
    fun `should calculate distance between London and Paris approximately`() {
        val london = Location(51.5074, -0.1278)
        val paris = Location(48.8566, 2.3522)

        val distance = calculator.distanceBetween(london, paris)

        // Expected distance: approximately 344 km = 344,000 meters
        // Allow 5% tolerance for rounding
        assertThat(distance).isBetween(320_000L, 370_000L)
    }

    @Test
    fun `should calculate symmetric distance`() {
        val locationA = Location(40.7128, -74.0060)
        val locationB = Location(34.0522, -118.2437)

        val distanceAB = calculator.distanceBetween(locationA, locationB)
        val distanceBA = calculator.distanceBetween(locationB, locationA)

        assertThat(distanceAB).isEqualTo(distanceBA)
    }

    @Test
    fun `should calculate distance for nearby locations accurately`() {
        // Two locations approximately 1 km apart
        val locationA = Location(40.7128, -74.0060)
        val locationB = Location(40.7218, -74.0060) // ~1 km north

        val distance = calculator.distanceBetween(locationA, locationB)

        // Should be around 1000 meters
        assertThat(distance).isBetween(900L, 1100L)
    }

    @Test
    fun `should handle equator crossing`() {
        val northernHemisphere = Location(10.0, 0.0)
        val southernHemisphere = Location(-10.0, 0.0)

        val distance = calculator.distanceBetween(northernHemisphere, southernHemisphere)

        // 20 degrees of latitude at equator is approximately 2,222 km
        assertThat(distance).isBetween(2_000_000L, 2_500_000L)
    }

    @Test
    fun `should handle prime meridian crossing`() {
        val west = Location(0.0, -10.0)
        val east = Location(0.0, 10.0)

        val distance = calculator.distanceBetween(west, east)

        // 20 degrees of longitude at equator is approximately 2,222 km
        assertThat(distance).isBetween(2_000_000L, 2_500_000L)
    }
}

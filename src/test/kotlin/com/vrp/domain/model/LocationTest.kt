package com.vrp.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class LocationTest {
    @Test
    fun `should create valid location`() {
        val location = Location(37.7749, -122.4194)

        assertEquals(37.7749, location.latitude)
        assertEquals(-122.4194, location.longitude)
    }

    @Test
    fun `should reject latitude above 90`() {
        assertThrows<IllegalArgumentException> {
            Location(91.0, 0.0)
        }
    }

    @Test
    fun `should reject latitude below -90`() {
        assertThrows<IllegalArgumentException> {
            Location(-91.0, 0.0)
        }
    }

    @Test
    fun `should reject longitude above 180`() {
        assertThrows<IllegalArgumentException> {
            Location(0.0, 181.0)
        }
    }

    @Test
    fun `should reject longitude below -180`() {
        assertThrows<IllegalArgumentException> {
            Location(0.0, -181.0)
        }
    }

    @Test
    fun `should accept boundary values`() {
        val north = Location(90.0, 0.0)
        val south = Location(-90.0, 0.0)
        val east = Location(0.0, 180.0)
        val west = Location(0.0, -180.0)

        assertEquals(90.0, north.latitude)
        assertEquals(-90.0, south.latitude)
        assertEquals(180.0, east.longitude)
        assertEquals(-180.0, west.longitude)
    }
}

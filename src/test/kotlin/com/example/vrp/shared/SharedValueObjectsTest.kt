package com.example.vrp.shared

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Currency
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SharedValueObjectsTest {

    @Test
    fun `TimeWindow rejects start after end`() {
        val now = LocalDateTime.now()
        assertThrows<IllegalArgumentException> {
            TimeWindow(now.plusHours(1), now)
        }
    }

    @Test
    fun `TimeWindow detects overlap`() {
        val base = LocalDateTime.of(2025, 1, 1, 8, 0)
        val tw1 = TimeWindow(base, base.plusHours(4))
        val tw2 = TimeWindow(base.plusHours(2), base.plusHours(6))
        assertTrue(tw1.overlaps(tw2))
    }

    @Test
    fun `TimeWindow detects no overlap`() {
        val base = LocalDateTime.of(2025, 1, 1, 8, 0)
        val tw1 = TimeWindow(base, base.plusHours(2))
        val tw2 = TimeWindow(base.plusHours(3), base.plusHours(5))
        assertFalse(tw1.overlaps(tw2))
    }

    @Test
    fun `Capacity canFit returns true when demand fits`() {
        val cap = Capacity(1000.0, 5000.0)
        val demand = Capacity(500.0, 2000.0)
        assertTrue(cap.canFit(demand))
    }

    @Test
    fun `Capacity canFit returns false when weight exceeded`() {
        val cap = Capacity(100.0, 5000.0)
        val demand = Capacity(200.0, 100.0)
        assertFalse(cap.canFit(demand))
    }

    @Test
    fun `Money rejects negative amount`() {
        assertThrows<IllegalArgumentException> {
            Money(BigDecimal("-1"), Currency.getInstance("USD"))
        }
    }

    @Test
    fun `Money addition works for same currency`() {
        val usd = Currency.getInstance("USD")
        val m1 = Money(BigDecimal("10.00"), usd)
        val m2 = Money(BigDecimal("5.50"), usd)
        assertEquals(BigDecimal("15.50"), (m1 + m2).amount)
    }

    @Test
    fun `Money addition fails for different currencies`() {
        val m1 = Money(BigDecimal("10"), Currency.getInstance("USD"))
        val m2 = Money(BigDecimal("10"), Currency.getInstance("EUR"))
        assertThrows<IllegalArgumentException> { m1 + m2 }
    }

    @Test
    fun `Location computes non-zero distance`() {
        val london = Location("London", 51.5074, -0.1278)
        val paris = Location("Paris", 48.8566, 2.3522)
        val dist = london.distanceTo(paris)
        assertTrue(dist > 300.0 && dist < 400.0, "Expected ~340 km but got $dist")
    }

    @Test
    fun `WorkingHours rejects start after end`() {
        assertThrows<IllegalArgumentException> {
            WorkingHours(LocalTime.of(18, 0), LocalTime.of(8, 0))
        }
    }
}

package com.vrp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class LineItemTest {
    @Test
    fun `should create line item with all properties`() {
        val id = UUID.randomUUID()

        val lineItem =
            LineItem(
                id = id,
                name = "Widget",
                quantity = 10,
                weight = BigDecimal("5.5"),
                volume = BigDecimal("2.0"),
                price = BigDecimal("99.99")
            )

        assertThat(lineItem.id).isEqualTo(id)
        assertThat(lineItem.name).isEqualTo("Widget")
        assertThat(lineItem.quantity).isEqualTo(10)
        assertThat(lineItem.weight).isEqualByComparingTo(BigDecimal("5.5"))
        assertThat(lineItem.volume).isEqualByComparingTo(BigDecimal("2.0"))
        assertThat(lineItem.price).isEqualByComparingTo(BigDecimal("99.99"))
    }

    @Test
    fun `should fail when name is blank`() {
        assertThatThrownBy {
            LineItem(
                id = UUID.randomUUID(),
                name = "",
                quantity = 1,
                weight = BigDecimal("1.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("10.00")
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Line item name cannot be blank")
    }

    @Test
    fun `should fail when quantity is zero`() {
        assertThatThrownBy {
            LineItem(
                id = UUID.randomUUID(),
                name = "Widget",
                quantity = 0,
                weight = BigDecimal("1.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("10.00")
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Quantity must be positive")
    }

    @Test
    fun `should fail when quantity is negative`() {
        assertThatThrownBy {
            LineItem(
                id = UUID.randomUUID(),
                name = "Widget",
                quantity = -1,
                weight = BigDecimal("1.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("10.00")
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Quantity must be positive")
    }

    @Test
    fun `should fail when weight is negative`() {
        assertThatThrownBy {
            LineItem(
                id = UUID.randomUUID(),
                name = "Widget",
                quantity = 1,
                weight = BigDecimal("-1.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("10.00")
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Weight cannot be negative")
    }

    @Test
    fun `should fail when volume is negative`() {
        assertThatThrownBy {
            LineItem(
                id = UUID.randomUUID(),
                name = "Widget",
                quantity = 1,
                weight = BigDecimal("1.0"),
                volume = BigDecimal("-1.0"),
                price = BigDecimal("10.00")
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Volume cannot be negative")
    }

    @Test
    fun `should fail when price is negative`() {
        assertThatThrownBy {
            LineItem(
                id = UUID.randomUUID(),
                name = "Widget",
                quantity = 1,
                weight = BigDecimal("1.0"),
                volume = BigDecimal("1.0"),
                price = BigDecimal("-10.00")
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Price cannot be negative")
    }
}

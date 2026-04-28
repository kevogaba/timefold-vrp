package com.vrp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class CustomerTest {
    @Test
    fun `should create customer with all properties`() {
        val id = UUID.randomUUID()
        val organizationId = UUID.randomUUID()
        val location = Location(40.7589, -73.9851)

        val customer =
            Customer(
                id = id,
                organizationId = organizationId,
                name = "John Doe",
                phoneNumber = "+1234567890",
                email = "john@example.com",
                location = location
            )

        assertThat(customer.id).isEqualTo(id)
        assertThat(customer.organizationId).isEqualTo(organizationId)
        assertThat(customer.name).isEqualTo("John Doe")
        assertThat(customer.phoneNumber).isEqualTo("+1234567890")
        assertThat(customer.email).isEqualTo("john@example.com")
        assertThat(customer.location).isEqualTo(location)
    }

    @Test
    fun `should create customer without optional fields`() {
        val customer =
            Customer(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "Jane Doe",
                phoneNumber = null,
                email = null,
                location = Location(40.7589, -73.9851)
            )

        assertThat(customer.phoneNumber).isNull()
        assertThat(customer.email).isNull()
    }

    @Test
    fun `should fail when name is blank`() {
        assertThatThrownBy {
            Customer(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "",
                phoneNumber = "+1234567890",
                email = "test@example.com",
                location = Location(40.7589, -73.9851)
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Customer name cannot be blank")
    }
}

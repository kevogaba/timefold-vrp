package com.vrp.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.UUID

class DriverTest {
    @Test
    fun `should create driver with all properties`() {
        val id = UUID.randomUUID()
        val organizationId = UUID.randomUUID()

        val driver =
            Driver(
                id = id,
                organizationId = organizationId,
                name = "John Driver",
                licenseNumber = "DL123456",
                phoneNumber = "+1234567890",
                email = "driver@example.com"
            )

        assertThat(driver.id).isEqualTo(id)
        assertThat(driver.organizationId).isEqualTo(organizationId)
        assertThat(driver.name).isEqualTo("John Driver")
        assertThat(driver.licenseNumber).isEqualTo("DL123456")
        assertThat(driver.phoneNumber).isEqualTo("+1234567890")
        assertThat(driver.email).isEqualTo("driver@example.com")
    }

    @Test
    fun `should create driver without optional fields`() {
        val driver =
            Driver(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "Jane Driver",
                licenseNumber = "DL654321",
                phoneNumber = null,
                email = null
            )

        assertThat(driver.phoneNumber).isNull()
        assertThat(driver.email).isNull()
    }

    @Test
    fun `should fail when name is blank`() {
        assertThatThrownBy {
            Driver(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "",
                licenseNumber = "DL123456",
                phoneNumber = "+1234567890",
                email = "test@example.com"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Driver name cannot be blank")
    }

    @Test
    fun `should fail when license number is blank`() {
        assertThatThrownBy {
            Driver(
                id = UUID.randomUUID(),
                organizationId = UUID.randomUUID(),
                name = "John Driver",
                licenseNumber = "",
                phoneNumber = "+1234567890",
                email = "test@example.com"
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("License number cannot be blank")
    }
}

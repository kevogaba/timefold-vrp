package com.example.vrp.order.adapter.out.persistence

import com.example.vrp.order.domain.Customer
import com.example.vrp.order.domain.Order
import com.example.vrp.order.port.out.OrderRepository
import com.example.vrp.shared.Location
import com.example.vrp.shared.TimeWindow
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class JpaOrderAdapter(private val repo: OrderJpaRepository) : OrderRepository {

    override fun findById(id: String): Order? = repo.findById(id).orElse(null)?.toDomain()

    override fun findAllByOrganizationId(organizationId: String): List<Order> =
        repo.findAllByOrganizationId(organizationId).map { it.toDomain() }

    override fun save(order: Order): Order = repo.save(order.toEntity()).toDomain()

    private fun OrderJpaEntity.toDomain() = Order(
        id = id,
        organizationId = organizationId,
        customer = Customer(
            id = customerId,
            name = customerName,
            contactEmail = customerEmail,
            location = Location(customerAddress, customerLatitude, customerLongitude),
        ),
        lineItems = emptyList(), // line items fetched separately in full use case
        deliveryWindow = TimeWindow(
            start = LocalDateTime.ofInstant(deliveryWindowStart, ZoneOffset.UTC),
            end = LocalDateTime.ofInstant(deliveryWindowEnd, ZoneOffset.UTC),
        ),
        createdAt = createdAt ?: Instant.now(),
    )

    private fun Order.toEntity() = OrderJpaEntity(
        id = id,
        organizationId = organizationId,
        customerId = customer.id,
        customerName = customer.name,
        customerEmail = customer.contactEmail,
        customerAddress = customer.location.address,
        customerLatitude = customer.location.latitude,
        customerLongitude = customer.location.longitude,
        deliveryWindowStart = deliveryWindow.start.toInstant(ZoneOffset.UTC),
        deliveryWindowEnd = deliveryWindow.end.toInstant(ZoneOffset.UTC),
    )
}

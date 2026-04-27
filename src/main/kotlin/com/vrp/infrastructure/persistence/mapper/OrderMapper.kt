package com.vrp.infrastructure.persistence.mapper

import com.vrp.domain.model.*
import com.vrp.infrastructure.persistence.entity.LineItemEntity
import com.vrp.infrastructure.persistence.entity.OrderEntity
import org.springframework.stereotype.Component

@Component
class OrderMapper {

    fun toDomain(entity: OrderEntity): Order {
        return Order(
            id = entity.id,
            organizationId = entity.organizationId,
            customer = Customer(
                id = entity.customerId,
                organizationId = entity.organizationId,
                name = entity.customerName,
                phoneNumber = entity.customerPhone,
                email = entity.customerEmail,
                location = Location(entity.customerLat, entity.customerLon)
            ),
            lineItems = entity.lineItems.map { lineItem ->
                LineItem(
                    id = lineItem.id,
                    name = lineItem.name,
                    quantity = lineItem.quantity,
                    weight = lineItem.weight,
                    volume = lineItem.volume,
                    price = lineItem.price
                )
            },
            pickupLocation = entity.pickupLat?.let { lat ->
                entity.pickupLon?.let { lon ->
                    Location(lat, lon)
                }
            },
            deliveryLocation = Location(entity.deliveryLat, entity.deliveryLon),
            timeWindowStart = entity.timeWindowStart,
            timeWindowEnd = entity.timeWindowEnd,
            serviceDurationMinutes = entity.serviceDurationMinutes,
            priority = entity.priority,
            notes = entity.notes,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: Order): OrderEntity {
        val entity = OrderEntity(
            id = domain.id,
            organizationId = domain.organizationId,
            customerId = domain.customer.id,
            customerName = domain.customer.name,
            customerPhone = domain.customer.phoneNumber,
            customerEmail = domain.customer.email,
            customerLat = domain.customer.location.latitude,
            customerLon = domain.customer.location.longitude,
            pickupLat = domain.pickupLocation?.latitude,
            pickupLon = domain.pickupLocation?.longitude,
            deliveryLat = domain.deliveryLocation.latitude,
            deliveryLon = domain.deliveryLocation.longitude,
            timeWindowStart = domain.timeWindowStart,
            timeWindowEnd = domain.timeWindowEnd,
            serviceDurationMinutes = domain.serviceDurationMinutes,
            priority = domain.priority,
            notes = domain.notes,
            createdAt = domain.createdAt
        )

        entity.lineItems.addAll(domain.lineItems.map { lineItem ->
            LineItemEntity(
                id = lineItem.id,
                order = entity,
                name = lineItem.name,
                quantity = lineItem.quantity,
                weight = lineItem.weight,
                volume = lineItem.volume,
                price = lineItem.price
            )
        })

        return entity
    }
}

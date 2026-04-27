package com.vrp.infrastructure.persistence.entity

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "line_items")
data class LineItemEntity(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val guid: UUID = UuidCreator.getTimeOrderedEpoch(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: OrderEntity,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val quantity: Int,

    @Column(nullable = false, precision = 10, scale = 2)
    val weight: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val volume: BigDecimal,

    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal
)

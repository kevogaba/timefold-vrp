package com.example.vrp.shared

import java.math.BigDecimal
import java.util.Currency

data class Money(
    val amount: BigDecimal,
    val currency: Currency,
) {
    init {
        require(amount >= BigDecimal.ZERO) { "Money amount must not be negative" }
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add amounts in different currencies" }
        return Money(amount + other.amount, currency)
    }
}

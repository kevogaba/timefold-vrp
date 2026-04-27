package com.example.vrp.shared

data class Capacity(
    val weightKg: Double,
    val volumeLiters: Double,
) {
    init {
        require(weightKg >= 0) { "weightKg must not be negative" }
        require(volumeLiters >= 0) { "volumeLiters must not be negative" }
    }

    fun canFit(demand: Capacity): Boolean =
        demand.weightKg <= weightKg && demand.volumeLiters <= volumeLiters

    operator fun minus(demand: Capacity): Capacity =
        Capacity(weightKg - demand.weightKg, volumeLiters - demand.volumeLiters)
}

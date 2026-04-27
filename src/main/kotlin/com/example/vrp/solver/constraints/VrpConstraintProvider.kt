package com.example.vrp.solver.constraints

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import com.example.vrp.solver.domain.SolverVisit

class VrpConstraintProvider : ConstraintProvider {

    override fun defineConstraints(factory: ConstraintFactory): Array<Constraint> = arrayOf(
        vehicleCapacityWeight(factory),
        vehicleCapacityVolume(factory),
        deliveryTimeWindow(factory),
        minimizeTotalDistance(factory),
    )

    private fun vehicleCapacityWeight(factory: ConstraintFactory): Constraint =
        factory.forEach(SolverVisit::class.java)
            .filter { it.vehicle != null }
            .groupBy({ it.vehicle }, { it.demandWeightKg })
            .filter { vehicle, totalWeight ->
                totalWeight > vehicle.capacity.weightKg
            }
            .penalize(HardSoftScore.ONE_HARD) { _, excess ->
                (excess * 1000).toInt()
            }
            .asConstraint("Vehicle weight capacity exceeded")

    private fun vehicleCapacityVolume(factory: ConstraintFactory): Constraint =
        factory.forEach(SolverVisit::class.java)
            .filter { it.vehicle != null }
            .groupBy({ it.vehicle }, { it.demandVolumeLiters })
            .filter { vehicle, totalVolume ->
                totalVolume > vehicle.capacity.volumeLiters
            }
            .penalize(HardSoftScore.ONE_HARD) { _, excess ->
                (excess * 1000).toInt()
            }
            .asConstraint("Vehicle volume capacity exceeded")

    private fun deliveryTimeWindow(factory: ConstraintFactory): Constraint =
        factory.forEach(SolverVisit::class.java)
            .filter { visit ->
                visit.vehicle != null && visit.timeWindow.end.isBefore(
                    java.time.LocalDateTime.now()
                )
            }
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Delivery outside time window")

    private fun minimizeTotalDistance(factory: ConstraintFactory): Constraint =
        factory.forEach(SolverVisit::class.java)
            .filter { it.vehicle != null }
            .reward(HardSoftScore.ONE_SOFT) { visit ->
                val depot = visit.vehicle!!.depotLocation
                val dist = depot.distanceTo(visit.location)
                (dist * -1000).toInt().coerceAtLeast(Int.MIN_VALUE / 2)
            }
            .asConstraint("Minimize total distance")
}

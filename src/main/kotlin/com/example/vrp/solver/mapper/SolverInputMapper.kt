package com.example.vrp.solver.mapper

import com.example.vrp.order.domain.Order
import com.example.vrp.shared.TimeWindow
import com.example.vrp.solver.domain.SolverVisit
import com.example.vrp.solver.domain.SolverVehicle
import com.example.vrp.solver.domain.VrpSolution
import com.example.vrp.vehicle.domain.Vehicle
import org.springframework.stereotype.Component

@Component
class SolverInputMapper {

    fun toSolution(orders: List<Order>, vehicles: List<Vehicle>): VrpSolution {
        val solverVehicles = vehicles.map { v ->
            SolverVehicle(
                id = v.id,
                depotLocation = v.driver.let {
                    com.example.vrp.shared.Location("depot-${v.id}", 0.0, 0.0)
                },
                capacity = v.capacity,
                workingHours = v.workingHours,
            )
        }

        val solverVisits = orders.flatMap { order ->
            order.lineItems.map { item ->
                SolverVisit(
                    id = "${order.id}-${item.id}",
                    orderId = order.id,
                    location = order.customer.location,
                    timeWindow = order.deliveryWindow,
                    demandWeightKg = item.demand.weightKg,
                    demandVolumeLiters = item.demand.volumeLiters,
                )
            }
        }

        return VrpSolution(vehicles = solverVehicles, visits = solverVisits)
    }
}

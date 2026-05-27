package com.lksnext.ParkingAVillegas.data

import com.lksnext.ParkingAVillegas.model.ParkingSpot
import com.lksnext.ParkingAVillegas.model.SpotType

object ParkingData {

    val allSpots = buildList {

        for (i in 1..15) {
            add(
                ParkingSpot(
                    "A-${String.format("%02d", i)}",
                    SpotType.NORMAL
                )
            )
        }

        for (i in 1..8) {
            add(
                ParkingSpot(
                    "E-${String.format("%02d", i)}",
                    SpotType.ELECTRIC
                )
            )
        }

        for (i in 1..4) {
            add(
                ParkingSpot(
                    "D-${String.format("%02d", i)}",
                    SpotType.DISABLED
                )
            )
        }

        for (i in 1..3) {
            add(
                ParkingSpot(
                    "M-${String.format("%02d", i)}",
                    SpotType.MOTORCYCLE
                )
            )
        }
    }
}
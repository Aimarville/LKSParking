package com.lksnext.ParkingAVillegas.model

import com.google.firebase.firestore.PropertyName

enum class VehicleType {
    AUTOMOBILE, MOTORCYCLE
}

data class Vehicle(
    val plate: String = "",
    val brand: String = "",
    val model: String = "",
    val type: VehicleType = VehicleType.AUTOMOBILE,
    @get:PropertyName("isElectric")
    @set:PropertyName("isElectric")
    var isElectric: Boolean = false,
    @get:PropertyName("isDisabled")
    @set:PropertyName("isDisabled")
    var isDisabled: Boolean = false
)

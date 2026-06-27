package com.lksnext.ParkingAVillegas.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val departamento: String = "",
    val vehiculos: List<Vehicle> = emptyList(),
    val profilePhotoUri: String? = null
)

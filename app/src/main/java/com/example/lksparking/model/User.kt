package com.example.lksparking.model

data class User(
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val departamento: String = "",
    val password: String = "",
    val vehiculos: List<Vehicle> = emptyList()
)

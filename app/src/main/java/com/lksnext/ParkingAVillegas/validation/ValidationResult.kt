package com.lksnext.ParkingAVillegas.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
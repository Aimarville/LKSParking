package com.lksnext.ParkingAVillegas.validation

object AuthValidator {

    fun validateRegister(
        name: String,
        email: String,
        phone: String,
        department: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        if (
            name.isBlank() ||
            email.isBlank() ||
            phone.isBlank() ||
            department.isBlank() ||
            password.isBlank()
        ) {
            return ValidationResult(false, "Por favor rellena todos los campos")
        }

        if (!email.endsWith("@lks.com")) {
            return ValidationResult(false, "El correo debe terminar en @lks.com")
        }

        if (password.length < 6) {
            return ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
        }

        return ValidationResult(true)
    }

    fun validateLogin(
        email: String,
        password: String
    ): ValidationResult {
        if (email.isBlank() || password.isBlank()) {
            return ValidationResult(false, "Por favor rellena todos los campos")
        }

        if (!email.endsWith("@lks.com")) {
            return ValidationResult(false, "El correo debe terminar en @lks.com")
        }

        return ValidationResult(true)
    }
}
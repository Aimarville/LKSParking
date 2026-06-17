package com.lksnext.ParkingAVillegas.validation

import java.util.Calendar

object ReservationValidator {

    fun validateReservationTime(
        startTime: Calendar,
        endTime: Calendar
    ): ValidationResult {

        val now = Calendar.getInstance()

        if (startTime.before(now)) {
            return ValidationResult(
                false,
                "La hora de entrada debe ser posterior a la actual"
            )
        }

        if (!endTime.after(startTime)) {
            return ValidationResult(
                false,
                "La salida debe ser después de la entrada"
            )
        }

        val maxEnd = (startTime.clone() as Calendar).apply {
            add(Calendar.HOUR_OF_DAY, 9)
        }

        if (endTime.after(maxEnd)) {
            return ValidationResult(
                false,
                "La reserva no puede superar las 9 horas"
            )
        }

        return ValidationResult(true)
    }
}
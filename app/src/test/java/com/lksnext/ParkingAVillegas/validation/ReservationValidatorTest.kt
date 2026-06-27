package com.lksnext.ParkingAVillegas.validation

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ReservationValidatorTest {

    @Test
    fun `validateReservationTime with past start time is invalid`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.MINUTE, -10)
        }
        val end = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertFalse(result.isValid)
    }

    @Test
    fun `validateReservationTime with past start time returns correct error message`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.MINUTE, -10)
        }
        val end = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertEquals("La hora de entrada debe ser posterior a la actual", result.errorMessage)
    }

    @Test
    fun `validateReservationTime with end time before start time is invalid`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }
        val end = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 30) // End is before start
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertFalse(result.isValid)
    }

    @Test
    fun `validateReservationTime with end time before start time returns correct error message`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }
        val end = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 30) // End is before start
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertEquals("La salida debe ser después de la entrada", result.errorMessage)
    }

    @Test
    fun `validateReservationTime exceeding 9 hours is invalid`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.HOUR_OF_DAY, 10)
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertFalse(result.isValid)
    }

    @Test
    fun `validateReservationTime exceeding 9 hours returns correct error message`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.HOUR_OF_DAY, 10)
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertEquals("La reserva no puede superar las 9 horas", result.errorMessage)
    }

    @Test
    fun `validateReservationTime with valid window is valid`() {
        val start = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }
        val end = (start.clone() as Calendar).apply {
            add(Calendar.HOUR_OF_DAY, 2)
        }

        val result = ReservationValidator.validateReservationTime(start, end)

        assertTrue(result.isValid)
    }
}

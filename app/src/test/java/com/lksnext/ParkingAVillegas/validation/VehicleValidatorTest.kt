package com.lksnext.ParkingAVillegas.validation

import com.lksnext.ParkingAVillegas.model.SpotType
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import org.junit.Assert.*
import org.junit.Test

class VehicleValidatorTest {

    @Test
    fun `validateVehicle with empty plate returns error`() {
        val result = VehicleValidator.validateVehicle("", "Toyota", "Corolla")
        assertFalse(result.isValid)
        assertEquals("La matrícula es obligatoria", result.errorMessage)
    }

    @Test
    fun `validateVehicle with empty brand returns error`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "", "Corolla")
        assertFalse(result.isValid)
        assertEquals("La marca es obligatoria", result.errorMessage)
    }

    @Test
    fun `validateVehicle with empty model returns error`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "Toyota", "")
        assertFalse(result.isValid)
        assertEquals("El modelo es obligatorio", result.errorMessage)
    }

    @Test
    fun `validateVehicle with valid data returns success`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "Toyota", "Corolla")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateVehicleForSpot MOTORCYCLE spot with car returns error`() {
        val car = Vehicle(plate = "123", brand = "T", model = "C", type = VehicleType.AUTOMOBILE)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.MOTORCYCLE)
        assertFalse(result.isValid)
        assertEquals("Solo motocicletas pueden usar esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot ELECTRIC spot with non-electric car returns error`() {
        val car = Vehicle(plate = "123", brand = "T", model = "C", type = VehicleType.AUTOMOBILE, isElectric = false)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.ELECTRIC)
        assertFalse(result.isValid)
        assertEquals("Solo vehículos eléctricos pueden usar esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot DISABLED spot with non-disabled car returns error`() {
        val car = Vehicle(plate = "123", brand = "T", model = "C", type = VehicleType.AUTOMOBILE, isDisabled = false)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.DISABLED)
        assertFalse(result.isValid)
        assertEquals("Solo vehículos para minusválidos pueden usar esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot NORMAL spot with motorcycle returns error`() {
        val moto = Vehicle(plate = "123", brand = "Y", model = "M", type = VehicleType.MOTORCYCLE)
        val result = VehicleValidator.validateVehicleForSpot(moto, SpotType.NORMAL)
        assertFalse(result.isValid)
        assertEquals("Vehículo no válido para esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot MOTORCYCLE spot with motorcycle returns success`() {
        val moto = Vehicle(plate = "123", brand = "Y", model = "M", type = VehicleType.MOTORCYCLE)
        val result = VehicleValidator.validateVehicleForSpot(moto, SpotType.MOTORCYCLE)
        assertTrue(result.isValid)
    }

    @Test
    fun `validateVehicleForSpot NORMAL spot with car returns success`() {
        val car = Vehicle(plate = "123", brand = "T", model = "C", type = VehicleType.AUTOMOBILE)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.NORMAL)
        assertTrue(result.isValid)
    }

    @Test
    fun `validateVehicleForSpot ELECTRIC spot with electric car returns success`() {
        val car = Vehicle(plate = "123", brand = "T", model = "C", type = VehicleType.AUTOMOBILE, isElectric = true)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.ELECTRIC)
        assertTrue(result.isValid)
    }

    @Test
    fun `validateVehicleForSpot DISABLED spot with disabled car returns success`() {
        val car = Vehicle(plate = "123", brand = "T", model = "C", type = VehicleType.AUTOMOBILE, isDisabled = true)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.DISABLED)
        assertTrue(result.isValid)
    }
}

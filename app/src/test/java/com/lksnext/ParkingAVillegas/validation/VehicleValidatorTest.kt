package com.lksnext.ParkingAVillegas.validation

import com.lksnext.ParkingAVillegas.model.SpotType
import com.lksnext.ParkingAVillegas.model.Vehicle
import com.lksnext.ParkingAVillegas.model.VehicleType
import org.junit.Assert.*
import org.junit.Test

class VehicleValidatorTest {

    @Test
    fun `validateVehicle with empty plate is invalid`() {
        val result = VehicleValidator.validateVehicle("", "Toyota", "Corolla")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateVehicle with empty plate returns correct error message`() {
        val result = VehicleValidator.validateVehicle("", "Toyota", "Corolla")
        assertEquals("La matrícula es obligatoria", result.errorMessage)
    }

    @Test
    fun `validateVehicle with empty brand is invalid`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "", "Corolla")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateVehicle with empty brand returns correct error message`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "", "Corolla")
        assertEquals("La marca es obligatoria", result.errorMessage)
    }

    @Test
    fun `validateVehicle with empty model is invalid`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "Toyota", "")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateVehicle with empty model returns correct error message`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "Toyota", "")
        assertEquals("El modelo es obligatorio", result.errorMessage)
    }

    @Test
    fun `validateVehicle with valid data is valid`() {
        val result = VehicleValidator.validateVehicle("1234ABC", "Toyota", "Corolla")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateVehicleForSpot NORMAL spot with car is valid`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE)
        assertTrue(VehicleValidator.validateVehicleForSpot(car, SpotType.NORMAL).isValid)
    }

    @Test
    fun `validateVehicleForSpot NORMAL spot with motorcycle is invalid`() {
        val moto = Vehicle(type = VehicleType.MOTORCYCLE)
        assertFalse(VehicleValidator.validateVehicleForSpot(moto, SpotType.NORMAL).isValid)
    }

    @Test
    fun `validateVehicleForSpot NORMAL spot with motorcycle returns correct error message`() {
        val moto = Vehicle(type = VehicleType.MOTORCYCLE)
        val result = VehicleValidator.validateVehicleForSpot(moto, SpotType.NORMAL)
        assertEquals("Vehículo no válido para esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot ELECTRIC spot with electric car is valid`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE, isElectric = true)
        assertTrue(VehicleValidator.validateVehicleForSpot(car, SpotType.ELECTRIC).isValid)
    }

    @Test
    fun `validateVehicleForSpot ELECTRIC spot with non-electric car is invalid`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE, isElectric = false)
        assertFalse(VehicleValidator.validateVehicleForSpot(car, SpotType.ELECTRIC).isValid)
    }

    @Test
    fun `validateVehicleForSpot ELECTRIC spot with non-electric car returns correct error message`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE, isElectric = false)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.ELECTRIC)
        assertEquals("Solo vehículos eléctricos pueden usar esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot ELECTRIC spot with electric moto is invalid`() {
        val moto = Vehicle(type = VehicleType.MOTORCYCLE, isElectric = true)
        assertFalse(VehicleValidator.validateVehicleForSpot(moto, SpotType.ELECTRIC).isValid)
    }

    @Test
    fun `validateVehicleForSpot DISABLED spot with disabled car is valid`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE, isDisabled = true)
        assertTrue(VehicleValidator.validateVehicleForSpot(car, SpotType.DISABLED).isValid)
    }

    @Test
    fun `validateVehicleForSpot DISABLED spot with non-disabled car is invalid`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE, isDisabled = false)
        assertFalse(VehicleValidator.validateVehicleForSpot(car, SpotType.DISABLED).isValid)
    }

    @Test
    fun `validateVehicleForSpot DISABLED spot with non-disabled car returns correct error message`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE, isDisabled = false)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.DISABLED)
        assertEquals("Solo vehículos para minusválidos pueden usar esta plaza", result.errorMessage)
    }

    @Test
    fun `validateVehicleForSpot MOTORCYCLE spot with motorcycle is valid`() {
        val moto = Vehicle(type = VehicleType.MOTORCYCLE)
        assertTrue(VehicleValidator.validateVehicleForSpot(moto, SpotType.MOTORCYCLE).isValid)
    }

    @Test
    fun `validateVehicleForSpot MOTORCYCLE spot with car is invalid`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE)
        assertFalse(VehicleValidator.validateVehicleForSpot(car, SpotType.MOTORCYCLE).isValid)
    }

    @Test
    fun `validateVehicleForSpot MOTORCYCLE spot with car returns correct error message`() {
        val car = Vehicle(type = VehicleType.AUTOMOBILE)
        val result = VehicleValidator.validateVehicleForSpot(car, SpotType.MOTORCYCLE)
        assertEquals("Solo motocicletas pueden usar esta plaza", result.errorMessage)
    }
}

package com.lksnext.ParkingAVillegas.validation

import org.junit.Assert.*
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun `validateRegister with blank fields returns error`() {
        val result = AuthValidator.validateRegister(
            name = "",
            email = "test@lks.com",
            phone = "123456789",
            department = "IT",
            password = "password",
            confirmPassword = "password"
        )
        assertFalse(result.isValid)
        assertEquals("Por favor rellena todos los campos", result.errorMessage)
    }

    @Test
    fun `validateRegister with invalid email domain returns error`() {
        val result = AuthValidator.validateRegister(
            name = "Test User",
            email = "test@gmail.com",
            phone = "123456789",
            department = "IT",
            password = "password",
            confirmPassword = "password"
        )
        assertFalse(result.isValid)
        assertEquals("El correo debe terminar en @lks.com", result.errorMessage)
    }

    @Test
    fun `validateRegister with short password returns error`() {
        val result = AuthValidator.validateRegister(
            name = "Test User",
            email = "test@lks.com",
            phone = "123456789",
            department = "IT",
            password = "12345",
            confirmPassword = "12345"
        )
        assertFalse(result.isValid)
        assertEquals("La contraseña debe tener al menos 6 caracteres", result.errorMessage)
    }

    @Test
    fun `validateRegister with valid data returns success`() {
        val result = AuthValidator.validateRegister(
            name = "Test User",
            email = "test@lks.com",
            phone = "123456789",
            department = "IT",
            password = "password123",
            confirmPassword = "password123"
        )
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateLogin with blank fields returns error`() {
        val result = AuthValidator.validateLogin("", "password")
        assertFalse(result.isValid)
        assertEquals("Por favor rellena todos los campos", result.errorMessage)
    }

    @Test
    fun `validateLogin with valid fields returns success`() {
        val result = AuthValidator.validateLogin("test@lks.com", "password")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }
}
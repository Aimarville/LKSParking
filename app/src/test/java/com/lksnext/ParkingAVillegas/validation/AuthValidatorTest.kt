package com.lksnext.ParkingAVillegas.validation

import org.junit.Assert.*
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun `validateRegister with blank fields is invalid`() {
        val result = AuthValidator.validateRegister("", "test@lks.com", "123", "IT", "pass", "pass")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateRegister with blank fields returns correct error message`() {
        val result = AuthValidator.validateRegister("", "test@lks.com", "123", "IT", "pass", "pass")
        assertEquals("Por favor rellena todos los campos", result.errorMessage)
    }

    @Test
    fun `validateRegister with invalid email domain is invalid`() {
        val result = AuthValidator.validateRegister("User", "test@gmail.com", "123", "IT", "pass", "pass")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateRegister with invalid email domain returns correct error message`() {
        val result = AuthValidator.validateRegister("User", "test@gmail.com", "123", "IT", "pass", "pass")
        assertEquals("El correo debe terminar en @lks.com", result.errorMessage)
    }

    @Test
    fun `validateRegister with short password is invalid`() {
        val result = AuthValidator.validateRegister("User", "test@lks.com", "123", "IT", "123", "123")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateRegister with short password returns correct error message`() {
        val result = AuthValidator.validateRegister("User", "test@lks.com", "123", "IT", "123", "123")
        assertEquals("La contraseña debe tener al menos 6 caracteres", result.errorMessage)
    }

    @Test
    fun `validateRegister with valid data is valid`() {
        val result = AuthValidator.validateRegister("User", "test@lks.com", "123", "IT", "password", "password")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateLogin with blank fields is invalid`() {
        val result = AuthValidator.validateLogin("", "password")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateLogin with invalid email domain is invalid`() {
        val result = AuthValidator.validateLogin("test@gmail.com", "password")
        assertFalse(result.isValid)
    }

    @Test
    fun `validateLogin with valid fields is valid`() {
        val result = AuthValidator.validateLogin("test@lks.com", "password")
        assertTrue(result.isValid)
    }
}

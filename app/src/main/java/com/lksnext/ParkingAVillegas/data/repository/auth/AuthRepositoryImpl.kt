package com.lksnext.ParkingAVillegas.data.repository.auth

import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.validation.AuthValidator

class AuthRepositoryImpl(
    private val userRepository: UserRepository
): AuthRepository {
    override fun login(
        email: String,
        password: String
    ): Result<User> {
        val validation = AuthValidator.validateLogin(
            email,
            password
        )

        if (!validation.isValid) {
            return Result.failure(
                Exception(validation.errorMessage)
            )
        }

        val user = userRepository.getUserByEmail(email)

        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(
                Exception("Correo o contraseña incorrectos")
            )
        }
    }

    override fun register(
        name: String,
        email: String,
        phone: String,
        department: String,
        password: String,
        confirmPassword: String
    ): Result<Unit> {
        val validation = AuthValidator.validateRegister(
            name = name,
            email = email,
            phone = phone,
            department = department,
            password = password,
            confirmPassword = confirmPassword,
        )

        if (!validation.isValid) {
            return Result.failure(
                Exception(validation.errorMessage)
            )
        }

        val user = User(
            nombre = name,
            email = email,
            telefono = phone,
            departamento = department,
            password = password
        )

        val saved = userRepository.saveUser(user)

        return if (saved) {
            Result.success(Unit)
        } else {
            Result.failure(
                Exception("El correo ya está registrado")
            )
        }
    }
}
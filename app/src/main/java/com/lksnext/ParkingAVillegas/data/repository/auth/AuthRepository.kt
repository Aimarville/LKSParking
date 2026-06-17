package com.lksnext.ParkingAVillegas.data.repository.auth

import com.lksnext.ParkingAVillegas.model.User

interface AuthRepository {
    fun login(
        email: String,
        password: String
    ): Result<User>

    fun register(
        name: String,
        email: String,
        phone: String,
        department: String,
        password: String,
        confirmPassword: String
    ): Result<Unit>
}
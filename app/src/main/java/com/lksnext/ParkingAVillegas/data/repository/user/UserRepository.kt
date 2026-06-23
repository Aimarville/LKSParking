package com.lksnext.ParkingAVillegas.data.repository.user

import com.lksnext.ParkingAVillegas.model.User

interface UserRepository {

    suspend fun getUserByEmail(
        email: String
    ): User?

    suspend fun getUserByUid(
        uid: String
    ): User?

    suspend fun updateUser(
        user: User
    ): Boolean

    suspend fun updateProfile(
        email: String,
        newName: String,
        newPhone: String,
        newDept: String,
        photoUri: String?
    ): Boolean
}
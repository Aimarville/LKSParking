package com.lksnext.ParkingAVillegas.data.repository.user

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.lksnext.ParkingAVillegas.model.User

interface UserRepository {

    val users: SnapshotStateList<User>

    fun getUserByEmail(
        email: String
    ): User?

    fun saveUser(
        user: User
    ): Boolean

    fun updateUser(user: User): Boolean

    fun updateProfile(
        email: String,
        newName: String,
        newPhone: String,
        newDept: String,
        photoUri: String?
    ): Boolean
}
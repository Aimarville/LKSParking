package com.lksnext.ParkingAVillegas.data.repository.user

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.reflect.TypeToken
import com.lksnext.ParkingAVillegas.data.local.JsonStorage
import com.lksnext.ParkingAVillegas.model.User

class UserRepositoryImpl(
    private val storage: JsonStorage
): UserRepository {

    companion object {
        private const val FILE_NAME =
            "users.json"
    }

    private val _users =
        mutableStateListOf<User>()

    override val users: SnapshotStateList<User>
        get() = _users

    init {
        load()
    }

    private fun load() {

        val type =
            object : TypeToken<List<User>>() {}.type

        val loadedUsers: List<User> =
            storage.read(
                FILE_NAME,
                type
            ) ?: emptyList()

        _users.clear()
        _users.addAll(loadedUsers)
    }

    private fun save() {

        storage.write(
            FILE_NAME,
            _users
        )
    }

    override fun getUserByEmail(
        email: String
    ): User? {

        return _users.find {
            it.email == email
        }
    }

    override fun saveUser(
        user: User
    ): Boolean {

        val exists =
            _users.any {
                it.email == user.email
            }

        if (exists) {
            return false
        }

        _users.add(user)

        save()

        return true
    }

    override fun updateUser(user: User): Boolean {
        val index =
            _users.indexOfFirst {
                it.email == user.email
            }

        if (index == -1) {
            return false
        }

        _users[index] = user

        save()

        return true
    }

    override fun updateProfile(
        email: String,
        newName: String,
        newPhone: String,
        newDept: String,
        photoUri: String?
    ): Boolean {

        val index =
            _users.indexOfFirst {
                it.email == email
            }

        if (index == -1) {
            return false
        }

        _users[index] =
            _users[index].copy(
                nombre = newName,
                telefono = newPhone,
                departamento = newDept,
                profilePhotoUri = photoUri
            )

        save()

        return true
    }
}
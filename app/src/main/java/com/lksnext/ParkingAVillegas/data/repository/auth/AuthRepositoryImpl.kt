package com.lksnext.ParkingAVillegas.data.repository.auth

import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import com.lksnext.ParkingAVillegas.validation.AuthValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val userRepository: UserRepository
): AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun login(
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

        return try {
            val authResult =
                auth
                    .signInWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()

            val uid =
                authResult.user?.uid
                    ?: throw Exception(
                        "No se pudo obtener el UID"
                    )

            val user =
                firestore
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()
                    .toObject(User::class.java)
                    ?: throw Exception(
                        "Usuario no encontrado"
                    )

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        department: String,
        password: String,
        confirmPassword: String
    ): Result<User> {

        val validation =
            AuthValidator.validateRegister(
                name = name,
                email = email,
                phone = phone,
                department = department,
                password = password,
                confirmPassword = confirmPassword
            )

        if (!validation.isValid) {

            return Result.failure(
                Exception(validation.errorMessage)
            )
        }

        return try {
            val authResult =
                auth
                    .createUserWithEmailAndPassword(
                        email,
                        password
                    )
                    .await()

            val uid =
                authResult.user?.uid
                    ?: throw Exception(
                        "No se pudo obtener el UID"
                    )

            val user =
                User(
                    uid = uid,
                    nombre = name,
                    email = email,
                    telefono = phone,
                    departamento = department
                )

            firestore
                .collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(user)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}
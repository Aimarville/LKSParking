package com.lksnext.ParkingAVillegas.data.repository.user

import com.google.firebase.firestore.FirebaseFirestore
import com.lksnext.ParkingAVillegas.model.User
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    override suspend fun getUserByEmail(
        email: String
    ): User? {

        val snapshot =
            firestore
                .collection("users")
                .whereEqualTo(
                    "email",
                    email
                )
                .get()
                .await()

        return snapshot.documents
            .firstOrNull()
            ?.toObject(User::class.java)
    }

    override suspend fun getUserByUid(
        uid: String
    ): User? {

        return firestore
            .collection("users")
            .document(uid)
            .get()
            .await()
            .toObject(User::class.java)
    }

    override suspend fun updateUser(
        user: User
    ): Boolean {

        return try {

            firestore
                .collection("users")
                .document(user.uid)
                .set(user)
                .await()

            true

        } catch (e: Exception) {

            false
        }
    }

    override suspend fun updateProfile(
        email: String,
        newName: String,
        newPhone: String,
        newDept: String,
        photoUri: String?
    ): Boolean {

        val user =
            getUserByEmail(email)
                ?: return false

        return updateUser(
            user.copy(
                nombre = newName,
                telefono = newPhone,
                departamento = newDept,
                profilePhotoUri = photoUri
            )
        )
    }
}
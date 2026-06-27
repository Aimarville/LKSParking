package com.lksnext.ParkingAVillegas.data.repository.auth

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.lksnext.ParkingAVillegas.data.repository.user.UserRepository
import com.lksnext.ParkingAVillegas.model.User
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    private lateinit var repository: AuthRepositoryImpl
    private val userRepository: UserRepository = mockk()
    private val auth: FirebaseAuth = mockk()
    private val firestore: FirebaseFirestore = mockk()
    private val collectionReference: CollectionReference = mockk()
    private val documentReference: DocumentReference = mockk()
    
    private val taskAuthResult: Task<AuthResult> = mockk()
    private val authResult: AuthResult = mockk()
    private val firebaseUser: FirebaseUser = mockk()
    
    private val taskDocumentSnapshot: Task<DocumentSnapshot> = mockk()
    private val documentSnapshot: DocumentSnapshot = mockk()
    private val taskVoid: Task<Void> = mockk()

    @Before
    fun setup() {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        repository = AuthRepositoryImpl(userRepository, auth, firestore)
        every { firestore.collection("users") } returns collectionReference
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    private fun setupAuthSuccess(uid: String, user: User) {
        every { auth.signInWithEmailAndPassword(any(), any()) } returns taskAuthResult
        coEvery { taskAuthResult.await() } returns authResult
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns uid
        
        every { collectionReference.document(uid) } returns documentReference
        every { documentReference.get() } returns taskDocumentSnapshot
        coEvery { taskDocumentSnapshot.await() } returns documentSnapshot
        every { documentSnapshot.toObject(User::class.java) } returns user
    }

    @Test
    fun `login success returns isSuccess true`() = runTest {
        val user = User(uid = "123", email = "test@lks.com")
        setupAuthSuccess("123", user)
        val result = repository.login("test@lks.com", "password123")
        assertTrue(result.isSuccess)
    }

    @Test
    fun `login success returns correct user object`() = runTest {
        val user = User(uid = "123", email = "test@lks.com")
        setupAuthSuccess("123", user)
        val result = repository.login("test@lks.com", "password123")
        assertEquals(user, result.getOrNull())
    }

    @Test
    fun `login failure with invalid domain returns isFailure true`() = runTest {
        val result = repository.login("test@gmail.com", "password123")
        assertTrue(result.isFailure)
    }

    @Test
    fun `login failure with firebase error returns correct message`() = runTest {
        every { auth.signInWithEmailAndPassword(any(), any()) } returns taskAuthResult
        coEvery { taskAuthResult.await() } throws Exception("Auth error")

        val result = repository.login("test@lks.com", "password123")
        assertEquals("Auth error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `register success returns isSuccess true`() = runTest {
        val email = "test@lks.com"
        val password = "password123"

        every { auth.createUserWithEmailAndPassword(any(), any()) } returns taskAuthResult
        coEvery { taskAuthResult.await() } returns authResult
        every { authResult.user } returns firebaseUser
        every { firebaseUser.uid } returns "123"

        every { collectionReference.document("123") } returns documentReference
        every { documentReference.set(any()) } returns taskVoid
        coEvery { taskVoid.await() } returns mockk()

        val result = repository.register("Test", email, "123", "IT", password, password)
        assertTrue(result.isSuccess)
    }

    @Test
    fun `register failure with firebase error returns isFailure true`() = runTest {
        every { auth.createUserWithEmailAndPassword(any(), any()) } returns taskAuthResult
        coEvery { taskAuthResult.await() } throws Exception("Register error")

        val result = repository.register("Test", "test@lks.com", "123", "IT", "password123", "password123")
        assertTrue(result.isFailure)
    }
}

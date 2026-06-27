package com.lksnext.ParkingAVillegas.data.repository.user

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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

class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl
    private val firestore: FirebaseFirestore = mockk()
    private val collectionReference: CollectionReference = mockk()
    private val documentReference: DocumentReference = mockk()
    private val query: Query = mockk()
    private val taskQuerySnapshot: Task<QuerySnapshot> = mockk()
    private val taskDocumentSnapshot: Task<DocumentSnapshot> = mockk()
    private val taskVoid: Task<Void> = mockk()
    private val querySnapshot: QuerySnapshot = mockk()
    private val documentSnapshot: DocumentSnapshot = mockk()

    @Before
    fun setup() {
        mockkStatic("kotlinx.coroutines.tasks.TasksKt")
        repository = UserRepositoryImpl(firestore)
        every { firestore.collection("users") } returns collectionReference
    }

    @After
    fun tearDown() {
        unmockkStatic("kotlinx.coroutines.tasks.TasksKt")
    }

    private fun setupEmailQuery(email: String, user: User?) {
        every { collectionReference.whereEqualTo("email", email) } returns query
        every { query.get() } returns taskQuerySnapshot
        coEvery { taskQuerySnapshot.await() } returns querySnapshot
        if (user != null) {
            every { querySnapshot.documents } returns listOf(documentSnapshot)
            every { documentSnapshot.toObject(User::class.java) } returns user
        } else {
            every { querySnapshot.documents } returns emptyList()
        }
    }

    @Test
    fun `getUserByEmail returns non-null when found`() = runTest {
        val email = "test@lks.com"
        setupEmailQuery(email, User(uid = "123", email = email))
        assertNotNull(repository.getUserByEmail(email))
    }

    @Test
    fun `getUserByEmail returns user with correct email when found`() = runTest {
        val email = "test@lks.com"
        setupEmailQuery(email, User(uid = "123", email = email))
        assertEquals(email, repository.getUserByEmail(email)?.email)
    }

    @Test
    fun `getUserByEmail returns null when not found`() = runTest {
        val email = "none@lks.com"
        setupEmailQuery(email, null)
        assertNull(repository.getUserByEmail(email))
    }

    @Test
    fun `getUserByUid returns non-null when document exists`() = runTest {
        val uid = "123"
        every { collectionReference.document(uid) } returns documentReference
        every { documentReference.get() } returns taskDocumentSnapshot
        coEvery { taskDocumentSnapshot.await() } returns documentSnapshot
        every { documentSnapshot.toObject(User::class.java) } returns User(uid = uid)

        assertNotNull(repository.getUserByUid(uid))
    }

    @Test
    fun `getUserByUid returns null when document does not exist`() = runTest {
        val uid = "none"
        every { collectionReference.document(uid) } returns documentReference
        every { documentReference.get() } returns taskDocumentSnapshot
        coEvery { taskDocumentSnapshot.await() } returns documentSnapshot
        every { documentSnapshot.toObject(User::class.java) } returns null

        assertNull(repository.getUserByUid(uid))
    }

    @Test
    fun `updateUser returns true on successful write`() = runTest {
        val user = User(uid = "123")
        every { collectionReference.document(user.uid) } returns documentReference
        every { documentReference.set(user) } returns taskVoid
        coEvery { taskVoid.await() } returns mockk<Void>()

        assertTrue(repository.updateUser(user))
    }

    @Test
    fun `updateUser returns false on write failure`() = runTest {
        val user = User(uid = "123")
        every { collectionReference.document(user.uid) } returns documentReference
        every { documentReference.set(user) } returns taskVoid
        coEvery { taskVoid.await() } throws Exception("DB Error")

        assertFalse(repository.updateUser(user))
    }

    @Test
    fun `updateProfile returns true on successful update`() = runTest {
        val email = "test@lks.com"
        setupEmailQuery(email, User(uid = "123", email = email))
        every { collectionReference.document("123") } returns documentReference
        every { documentReference.set(any<User>()) } returns taskVoid
        coEvery { taskVoid.await() } returns mockk<Void>()

        assertTrue(repository.updateProfile(email, "New", "1", "IT", null))
    }

    @Test
    fun `updateProfile returns false when email is not registered`() = runTest {
        val email = "none@lks.com"
        setupEmailQuery(email, null)
        assertFalse(repository.updateProfile(email, "New", "1", "IT", null))
    }
}

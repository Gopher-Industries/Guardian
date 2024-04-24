package deakin.gopher.guardian.services

import android.util.Log

class UserDataService : FireStoreDataService() {
    val usersCollection = fireStore.collection(collectionName)
    override val collectionName: String
        get() = "users"

    override val dataServiceName: String
        get() = "UserDataService"

    fun create(
        userId: String,
        email: String,
    ) {
        usersCollection.document(userId).set(
            hashMapOf(
                "userId" to userId,
                "email" to email,
            ),
        )
            .addOnSuccessListener {
                Log.v(dataServiceName, "Created User $userId")
            }
            .addOnFailureListener { e ->
                Log.e(dataServiceName, "Failed to create user $userId: $e")
            }
    }
}

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
    // Amending UserDataService to include last password change timestamp
    fun updateLastPasswordChangeTimestamp(userId: String, timestamp: Long) {
        usersCollection.document(userId).update("lastPasswordChange", timestamp)
            .addOnSuccessListener {
                Log.v(dataServiceName, "Updated lastPasswordChange for User $userId")
            }
            .addOnFailureListener { e ->
                Log.e(dataServiceName, "Failed to update lastPasswordChange for user $userId: $e")
            }
    }
    fun getLastPasswordChangeTimestamp(userId: String, callback: (Long?) -> Unit) {

        usersCollection.document(userId).get()

            .addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.exists()) {

                    val lastPasswordChange = documentSnapshot.getLong("lastPasswordChange")

                    callback(lastPasswordChange)

                } else {

                    callback(null)

                }

            }

            .addOnFailureListener {

                Log.e(dataServiceName, "Error fetching last password change", it)

                callback(null)

            }

    }
}

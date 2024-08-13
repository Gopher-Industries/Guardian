package deakin.gopher.guardian.services

import android.util.Log
import com.google.firebase.firestore.SetOptions

class UserDataService : FireStoreDataService() {
    val usersCollection = fireStore.collection(collectionName)
    override val collectionName: String
        get() = "users"

    override val dataServiceName: String
        get() = "UserDataService"

    val loginAttemptsCollectionName: String = "loginAttempts"
    val loginAttemptsCollection = fireStore.collection(loginAttemptsCollectionName)

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



    fun getLoginAttemptsByEmail(email: String, callback: (Int, Boolean) -> Unit) {
        loginAttemptsCollection.document(email)
            .get()
            .addOnSuccessListener { document ->
                val attempts = document.getLong("attempts")?.toInt() ?: 0
                val isLocked = document.getBoolean("isLocked") ?: false
                Log.v(dataServiceName, "Retrieved login attempts for Email $email: $attempts")
                callback(attempts, isLocked)
            }
            .addOnFailureListener { e ->
                Log.e(dataServiceName, "Failed to retrieve login attempts for Email $email: ${e.message}")
                callback(0, false)
            }
    }

    // Function to set login attempts by email
    fun setLoginAttemptsByEmail(email: String, attempts: Int, isLocked: Boolean = false) {
        val data = hashMapOf("attempts" to attempts, "isLocked" to isLocked)
        loginAttemptsCollection.document(email)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.v(dataServiceName, "Set login attempts for Email $email to $attempts, isLocked: $isLocked")
            }
            .addOnFailureListener { e ->
                Log.e(dataServiceName, "Failed to set login attempts for Email $email: ${e.message}")
            }
    }

    // Function to reset login attempts for email
    fun resetLoginAttemptsByEmail(email: String) {
        Log.v(dataServiceName, "Resetting login attempts for Email $email")
        setLoginAttemptsByEmail(email, 0, false)
    }
}

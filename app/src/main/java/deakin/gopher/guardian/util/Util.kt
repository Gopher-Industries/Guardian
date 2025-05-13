package deakin.gopher.guardian.util

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import deakin.gopher.guardian.model.FirebaseMessagingService

object Util {
    const val DAILY_REPORT_LODGED = "daily_report_lodged"
    const val SHARED_PREF_DATA = "shared_pref_data"
    const val DAILY_REPORT_DATE = "daily_report_date"
    const val DAILY_REPORT_STATUS_LIST = "daily_report_status"
    const val DAILY_REPORT_STATUS_NOTES = "daily_report_notes"
    const val CHANNEL_ID = "guardians"
    const val CHANNEL_NAME = "guardians_app_notifications"
    const val CHANNEL_DESCRIPTION = "Guardians App Notifications"

    // token nodes
    const val TOKENS = "tokens"
    const val DEVICE_TOKEN = "device_token"

    fun updateDeviceToken(
        context: FirebaseMessagingService,
        token: String,
        username: String?,
    ) {
        val rootRef = FirebaseDatabase.getInstance().reference // get root node of the firebase
        val databaseReference =
            rootRef.child(TOKENS).child(
                username!!,
            ) // get token node associated to the user
        val hashMap: MutableMap<String, String> = HashMap()
        hashMap[DEVICE_TOKEN] = token
        databaseReference
            .setValue(hashMap)
            .addOnCompleteListener { task: Task<Void?> ->
                if (!task.isSuccessful) {
                    createToast(context, "Failed to update token")
                }
            }
    }

    fun createToast(
        context: Context?,
        message: CharSequence?,
    ) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun setToArray(hashSet: Collection<String?>): Array<String?> {
        val arr = arrayOfNulls<String>(hashSet.size)
        var i = 0

        // iterating over the hashset
        for (ele in hashSet) {
            arr[i++] = ele
        }
        return arr
    }
}

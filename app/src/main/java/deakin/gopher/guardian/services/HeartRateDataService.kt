package deakin.gopher.guardian.services

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import deakin.gopher.guardian.model.health.HeartRate
import java.util.Date
import java.util.UUID


class HeartRateDataService  {
     val collectionName: String
        get() = "heartRate"
     val dataServiceName: String
        get() = "HeartRateDataService"

    fun create(heartRate: HeartRate) {
        val fireStore =FirebaseFirestore.getInstance()
        fireStore
            .collection(collectionName)
            .document(heartRate.heartRateId.toString())
            .set(
                mapOf(
                    "patientId" to heartRate.patientId,
                    "heartRateId" to heartRate.heartRateId,
                    "measurement" to heartRate.measurement,
                    "measurementDate" to heartRate.measurementDate,
                ),
            )
            .addOnSuccessListener {
                Log.v(dataServiceName, "Created HeartRate $heartRate")
            }
            .addOnFailureListener { e ->
                Log.e(dataServiceName, "Failed to create heartRate $heartRate: $e")
            }
    }

    fun get(heartRateId: UUID): HeartRate? {
        val fireStore =FirebaseFirestore.getInstance()
              val result=fireStore .collection(collectionName)
                .document(heartRateId.toString())
                .get()

        return result.result?.toObject(HeartRate::class.java)
    }

    companion object {
        fun generateTestData(amount: Int): List<HeartRate> {
            val range = 1..amount
            return range.map {
                HeartRate(
                    heartRateId = UUID.randomUUID(),
                    patientId = UUID.randomUUID(),
                    measurement = (40..200).random(),
                    measurementDate = Date(),
                )
            }
        }
    }
}


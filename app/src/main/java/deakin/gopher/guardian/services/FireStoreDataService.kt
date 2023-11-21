package deakin.gopher.guardian.services

import com.google.firebase.firestore.FirebaseFirestore

abstract class FireStoreDataService {
    protected val fireStore = FirebaseFirestore.getInstance()
    protected abstract val collectionName: String
    protected abstract val dataServiceName: String
}
package deakin.gopher.guardian.view.general

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientListAdapter
import deakin.gopher.guardian.model.Patient
import deakin.gopher.guardian.view.patient.careplan.CarePlanActivity

class PatientListActivity : BaseActivity() {
    private lateinit var navigationView: NavigationView
    private lateinit var patientsOverviewCardView: CardView
    private lateinit var patientListMenuButton: ImageView
    private lateinit var patientListRecyclerView: RecyclerView
    private lateinit var patientSearchView: SearchView
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)
        initialiseViews()
        setupRecyclerView()
        setupSearchView()

        patientListMenuButton.setOnClickListener { _ -> drawerLayout.openDrawer(GravityCompat.START) }

        // TODO Prevents icons in navbar from being tinted grey, should this be here?
        navigationView.itemIconTintList = null

        // This clicker is for test
        patientsOverviewCardView.setOnClickListener { _ ->
            startActivity(Intent(this@PatientListActivity, CarePlanActivity::class.java))
        }
    }

    private fun initialiseViews() {
        navigationView = findViewById(R.id.nav_view)
        patientsOverviewCardView = findViewById(R.id.patient_list_patient_overview)
        patientListMenuButton = findViewById(R.id.patient_list_menu_button)
        patientListRecyclerView = findViewById(R.id.patient_list_recycleView)
        patientSearchView = findViewById(R.id.patient_list_searchView)
        drawerLayout = findViewById(R.id.drawer_layout)
    }

    private fun setupRecyclerView() {
        val allQuery: Query = FirebaseDatabase.getInstance().reference.child("patient_profile")
        val allOptions =
            FirebaseRecyclerOptions.Builder<Patient>()
                .setQuery(allQuery, this::parsePatientSnapshot).build()
        val patientListAdapterDefault = PatientListAdapter(this@PatientListActivity, allOptions)
        patientListRecyclerView.layoutManager = GridLayoutManager(this@PatientListActivity, 1)
        patientListRecyclerView.adapter = patientListAdapterDefault
        patientListAdapterDefault.startListening()
    }

    private fun parsePatientSnapshot(snapshot: DataSnapshot): Patient {
        return Patient(
            snapshot.key,
            snapshot.child("first_name").value?.toString() ?: "",
            snapshot.child("last_name").value?.toString() ?: "",
        ).apply {
            middleName = snapshot.child("middle_name").value?.toString() ?: ""
        }
    }

    private fun updateRecyclerView(
        query: Query,
        options: FirebaseRecyclerOptions<Patient>,
    ) {
        val patientListAdapter = PatientListAdapter(this, options)
        patientListRecyclerView.layoutManager = GridLayoutManager(this, 1)
        patientListRecyclerView.adapter = patientListAdapter

        query.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        patientListAdapter.startListening()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@PatientListActivity,
                        error.message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            },
        )
    }

    private fun setupSearchView() {
        patientSearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String): Boolean {
                    performSearch(s)
                    return true
                }
            },
        )
    }

    private fun performSearch(searchString: String) {
        val query =
            if (searchString.isEmpty()) {
                FirebaseDatabase.getInstance().reference.child("patient_profile")
            } else {
                FirebaseDatabase.getInstance().reference.child("patient_profile")
                    .orderByChild("first_name").startAt(searchString).endAt(searchString + "\uf8ff")
                    .limitToFirst(10)
            }

        val options =
            FirebaseRecyclerOptions.Builder<Patient>().setQuery(query, this::parsePatientSnapshot)
                .build()
        updateRecyclerView(query, options)
    }
}

package deakin.gopher.guardian.view.general

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.os.Bundle
import android.util.Log
import deakin.gopher.guardian.R
import deakin.gopher.guardian.adapter.PatientListAdapter
import deakin.gopher.guardian.adapter.SimpleArchivedPatientAdapter
import deakin.gopher.guardian.model.Patient

@SuppressLint("Registered")
class ArchivedPatientListActivity : BaseActivity() {
    private var recyclerView: RecyclerView? = null
    private val adapter: PatientListAdapter? = null
    private var drawerLayout: DrawerLayout? = null
    private var toggle: ActionBarDrawerToggle? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archived_patient_list)
        recyclerView = findViewById(R.id.archived_patient_recycler_view)
        val archivedPatients: MutableList<Patient> = ArrayList<Patient>()
        archivedPatients.add(Patient("1", "John", "Doe"))
        archivedPatients.add(Patient("2", "Jane", "Doe"))
        val patientRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("patients")
        val archivedQuery: DownloadManager.Query = patientRef.orderByChild("is_Archived").equalTo(true)
        archivedQuery.addListenerForSingleValueEvent(
            object : ValueEventListener() {
                fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(
                            "FirebaseTest",
                            "Archived patients found: " + dataSnapshot.getChildrenCount()
                        )
                    } else {
                        Log.d("FirebaseTest", "No archived patients found.")
                    }
                }

                fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FirebaseTest", "Error fetching data", databaseError.toException())
                }
            })
        val options: FirebaseRecyclerOptions<Patient> = Builder<Patient>()
            .setQuery(archivedQuery, Patient::class.java)
            .build()
        val adapter = SimpleArchivedPatientAdapter(archivedPatients)
        recyclerView.setAdapter(adapter)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true)
        } else {
            Log.e("ActionBar", "The action bar is not available.")
        }
        findViewById(R.id.patient_list_menu_button)
            .setOnClickListener { view -> drawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { item ->
            val id: Int = item.getItemId()
            //
            //      if (id == R.id.nav_home) {
            //      }
            //
            //      if (id == R.id.nav_admin) {
            //      }
            //      if (id == R.id.nav_settings) {
            //      }
            //      if (id == R.id.nav_signout) {
            //      }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    protected fun onStart() {
        super.onStart()
        if (adapter != null) {
            adapter.startListening()
        }
    }

    protected fun onStop() {
        super.onStop()
        if (adapter != null) {
            adapter.stopListening()
        }
    }

    fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
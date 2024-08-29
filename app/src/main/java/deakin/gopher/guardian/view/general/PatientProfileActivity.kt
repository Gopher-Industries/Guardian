package deakin.gopher.guardian.view.general

import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat

class PatientProfileActivity constructor() : BaseActivity() {
    private var customHeader: CustomHeader? = null
    private var drawerLayout: DrawerLayout? = null
    private val mAuth: FirebaseAuth? = null
    var fabDelete: FloatingActionButton? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)
        val intent: Intent = getIntent()
        val tabLayout: TabLayout = findViewById(R.id.dataForViewTabLayout)
        val viewPager2: ViewPager2 = findViewById(R.id.dataForViewViewPager)
        customHeader = findViewById(R.id.customHeader)
        drawerLayout = findViewById(R.id.nav_drawer_layout)
        fabDelete = findViewById(R.id.fab_delete)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val patientId: String? = intent.getStringExtra("patientId")
        Log.d("PatientProfileActivity", "Patient ID: " + patientId)
        assert(patientId != null)
        val viewPagerAdapter: PatientProfileAdapter =
            PatientProfileAdapter(patientId, getSupportFragmentManager(), getLifecycle())
        viewPager2.setAdapter(viewPagerAdapter)
        customHeader!!.setHeaderHeight(450)
        customHeader!!.setHeaderText("Patient Profile")
        customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
        navigationView.setItemIconTintList(null)
        fabDelete.setOnClickListener(
            { view ->
                if (patientId != null) {
                    deletePatient(patientId)
                } else {
                    customHeader!!.setHeaderText("Care Plan Summary")
                    customHeader!!.setHeaderTopImage(R.drawable.profile_avatar_men)
                    customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
                    Toast.makeText(
                        this@PatientProfileActivity, "Error: Patient ID is null", Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        if (null != customHeader) {
            customHeader!!.menuButton!!.setOnClickListener(
                View.OnClickListener({ v: View? ->
                    if (null != drawerLayout) {
                        drawerLayout.openDrawer(GravityCompat.START)
                        navigationView.setNavigationItemSelectedListener(
                            { menuItem ->
                                val id: Int = menuItem.getItemId()
                                if (R.id.nav_home == id) {
                                    startActivity(
                                        Intent(
                                            this@PatientProfileActivity,
                                            Homepage4admin::class.java
                                        )
                                    )
                                } /*else if (R.id.nav_admin == id) {
                          startActivity(new Intent(PatientProfileActivity.this, Homepage4admin.class));
                      } else if (R.id.nav_settings == id) {
                          startActivity(new Intent(PatientProfileActivity.this, Setting.class));
                      }*/ else if (R.id.nav_signout == id) {
                                    mAuth.getInstance().signOut()
                                    startActivity(
                                        Intent(
                                            this@PatientProfileActivity,
                                            LoginActivity::class.java
                                        )
                                    )
                                    finish()
                                }
                                true
                            })
                    }
                })
            )
        }
        TabLayoutMediator(
            tabLayout,
            viewPager2,
            { tab, position ->
                if (0 == position) {
                    tab.setText("Patient")
                } else if (1 == position) {
                    tab.setText("Next of Kin")
                } else if (2 == position) {
                    tab.setText("General Practitioner")
                } else if (3 == position) {
                    tab.setText("Health Details")
                } else if (4 == position) {
                    tab.setText("Health & Welfare Det.")
                } else {
                    tab.setText("Care Plan")
                }
            })
            .attach()
        tabLayout.addOnTabSelectedListener(
            object : OnTabSelectedListener() {
                fun onTabSelected(tab: Tab) {
                    val position: Int = tab.getPosition()
                    if (0 == position) {
                        customHeader!!.setHeaderText("Patient Profile")
                        customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
                        customHeader!!.setHeaderTopImage(R.drawable.avatar_icon)
                    } else if (1 == position) {
                        customHeader!!.setHeaderText("Next Of Kin Contact")
                        customHeader!!.setHeaderTopImage(R.drawable.profile_avatar_men)
                        customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
                    } else if (2 == position) {
                        customHeader!!.setHeaderText("GP Details")
                        customHeader!!.setHeaderTopImage(R.drawable.profile_avatar_men2)
                        customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
                    } else if (3 == position) {
                        customHeader!!.setHeaderText("Health Details")
                        customHeader!!.setHeaderTopImageVisibility(View.GONE)
                    } else if (4 == position) {
                        customHeader!!.setHeaderText("Patient Details")
                        customHeader!!.setHeaderTopImageVisibility(View.GONE)
                    } else {
                        customHeader!!.setHeaderText("Care Plan")
                        customHeader!!.setHeaderTopImage(R.drawable.profile_avatar_men)
                        customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
                    }
                }

                fun onTabUnselected(tab: Tab?) {}
                fun onTabReselected(tab: Tab?) {}
            })
    }

    private fun deletePatient(patientId: String) {
        FirebaseDatabase.getInstance()
            .getReference("patient_profile")
            .child(patientId)
            .removeValue()
            .addOnSuccessListener(
                { aVoid ->
                    Toast.makeText(
                        this@PatientProfileActivity,
                        "Patient deleted successfully",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    finish()
                })
            .addOnFailureListener(
                { e ->
                    Toast.makeText(
                        this@PatientProfileActivity, "Failed to delete patient", Toast.LENGTH_SHORT
                    )
                        .show()
                })
    }
}
package deakin.gopher.guardian.view.general

import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog

class PatientProfileAddActivity constructor() : BaseActivity(), DataListener {
    private var customHeader: CustomHeader? = null
    private var patient: Patient? = null
    private var nextOfKin1: NextOfKin? = null
    private var nextOfKin2: NextOfKin? = null
    private var gp1: GP? = null
    private var gp2: GP? = null
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile_add)
        val viewPager2: ViewPager2 = findViewById(R.id.dataForViewViewPager)
        customHeader = findViewById(R.id.customHeader)
        val drawerLayout: DrawerLayout? = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val viewPagerAdapter: PatientProfileAddAdapter =
            PatientProfileAddAdapter(getSupportFragmentManager(), getLifecycle())
        viewPager2.setAdapter(viewPagerAdapter)
        customHeader!!.setHeaderHeight(450)
        customHeader!!.setHeaderText("Patient Add")
        customHeader!!.setHeaderTopImageVisibility(View.VISIBLE)
        customHeader!!.setHeaderTopImage(R.drawable.add_image_button)
        navigationView.setItemIconTintList(null)
        if (null != customHeader) {
            customHeader!!.menuButton!!.setOnClickListener(
                View.OnClickListener({ v: View? ->
                    if (null != drawerLayout) {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                })
            )
        }
        viewPager2.registerOnPageChangeCallback(
            object : OnPageChangeCallback() {
                fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> customHeader!!.setHeaderText("Add New Patient")
                        1 -> customHeader!!.setHeaderText("Add Next of Kin")
                        2 -> customHeader!!.setHeaderText("Add Next of Kin+")
                        3 -> customHeader!!.setHeaderText("Add General Practitioner")
                        else -> customHeader!!.setHeaderText("Add General Practitioner+")
                    }
                }
            })
    }

    public override fun onDataFilled(
        patient: Patient?,
        nextOfKin1: NextOfKin?,
        nextOfKin2: NextOfKin?,
        gp1: GP?,
        gp2: GP?
    ) {
        if (null != patient) {
            this.patient = patient
        }
        if (null != nextOfKin1) {
            this.nextOfKin1 = nextOfKin1
        }
        if (null != nextOfKin2) {
            this.nextOfKin2 = nextOfKin2
        }
        if (null != gp1) {
            this.gp1 = gp1
        }
        if (null != gp2) {
            this.gp2 = gp2
        }
    }

    public override fun onDataFinished(isFinished: Boolean?) {
        // insert a dialog here
        val builder: AlertDialog.Builder = Builder(this)
        builder.setTitle("Saving Changes?")
        builder.setPositiveButton(
            "YES",
            { dialog, whichButton -> saveInFirebase() })
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.setOnShowListener(
            { arg0 ->
                dialog
                    .getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.colorGreen))
                dialog
                    .getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(getResources().getColor(R.color.colorRed))
            })
        dialog.show()
        // insert
    }

    private fun saveInFirebase() {
        val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val patientRef: DatabaseReference = databaseRef.child("patient_profile")
        val nokRef: DatabaseReference = databaseRef.child("next_of_kins_profile")
        val gpRef: DatabaseReference = databaseRef.child("gp_profile")
        val nof1_id: String = nokRef.push().getKey()
        val nof2_id: String = nokRef.push().getKey()
        val gp1_id: String = gpRef.push().getKey()
        val gp2_id: String = gpRef.push().getKey()
        patient.nokId1 = nof1_id
        patient.nokId2 = nof2_id
        patient.gpId1 = gp1_id
        patient.gpId2 = gp2_id
        nokRef
            .child(nof1_id)
            .setValue(nextOfKin1)
            .addOnSuccessListener({ unused -> Log.v("nok1 success", "") })
            .addOnFailureListener(
                { e ->
                    Toast.makeText(
                        this@PatientProfileAddActivity,
                        "fail to upload first next of kin" + e.getMessage(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })
        nokRef
            .child(nof2_id)
            .setValue(nextOfKin2)
            .addOnSuccessListener({ unused -> Log.v("nok2success", "") })
            .addOnFailureListener(
                { e ->
                    Toast.makeText(
                        this@PatientProfileAddActivity,
                        "fail to upload second next of kin" + e.getMessage(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })
        gpRef
            .child(gp1_id)
            .setValue(gp1)
            .addOnSuccessListener({ unused -> Log.v("gp1success", "") })
            .addOnFailureListener(
                { e ->
                    Toast.makeText(
                        this@PatientProfileAddActivity,
                        "fail to upload first gp" + e.getMessage(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })
        if (null != gp2) {
            gpRef
                .child(gp2_id)
                .setValue(gp2)
                .addOnSuccessListener({ unused -> Log.v("gp2success", "") })
                .addOnFailureListener(
                    { e ->
                        Toast.makeText(
                            this@PatientProfileAddActivity,
                            "fail to upload second gp" + e.getMessage(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    })
        }
        val patient_id: String = patientRef.push().getKey()
        patientRef
            .child(patient_id)
            .setValue(patient)
            .addOnSuccessListener(
                { unused ->
                    Toast.makeText(
                        this@PatientProfileAddActivity,
                        "Success on adding a new patient",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    val intent: Intent =
                        Intent(this@PatientProfileAddActivity, PatientProfileActivity::class.java)
                    intent.putExtra("created_patient_id", patient_id)
                    startActivity(intent)
                })
            .addOnFailureListener(
                { e ->
                    Toast.makeText(
                        this@PatientProfileAddActivity,
                        "fail to upload patient" + e.getMessage(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })
        PatientRelatedCreat(patient_id)
    }

    private fun PatientRelatedCreat(patient_id: String) {
        createMedicalDiagnostic(patient_id)
    }

    private fun createMedicalDiagnostic(patient_id: String) {
        val current_medical_diagnostic: MedicalDiagnostic = MedicalDiagnostic(patient_id, true)
        val reference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("health_details")
        val id: String = reference.push().getKey()
        reference
            .child(id)
            .setValue(current_medical_diagnostic)
            .addOnFailureListener(
                { e ->
                    Toast.makeText(
                        this@PatientProfileAddActivity, (
                                "Fail to create health detail of this patient!Please try it again!Reason:"
                                        + e.getMessage()),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                })
    }
}
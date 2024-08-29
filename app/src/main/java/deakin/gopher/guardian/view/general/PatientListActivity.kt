package deakin.gopher.guardian.view.general

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import java.util.Objects

class PatientListActivity() : BaseActivity() {
    var patient_list_recyclerView: RecyclerView? = null
    var patientListAdapter: PatientListAdapter? = null
    var query: Query? = null
    var patient_searchView: SearchView? = null
    var patientListMenuButton: ImageView? = null
    var currentUserId: String? = null
    private val handler = Handler()
    private val updateRunnable: Runnable = object : Runnable {
        @SuppressLint("NotifyDataSetChanged")
        override fun run() {
            if (patientListAdapter != null) {
                patientListAdapter.notifyDataSetChanged()
            }
            handler.postDelayed(this, 6000)
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateRunnable)
        if (patientListAdapter != null) {
            patientListAdapter.startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_list)
        patient_list_recyclerView = findViewById(R.id.patient_list_recycleView)
        val addPatientIcon: ImageView = findViewById(R.id.imageView62)
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid() // Get current user UID
        val viewArchivedButton: Button = findViewById(R.id.button_view_archived)
        viewArchivedButton.setOnClickListener(
            View.OnClickListener {
                val intent =
                    Intent(this@PatientListActivity, ArchivedPatientListActivity::class.java)
                startActivity(intent)
            })
        addPatientIcon.setOnClickListener(
            { v: View? ->
                val intent: Intent =
                    Intent(this@PatientListActivity, AddNewPatientActivity::class.java)
                startActivity(intent)
            })
        val swipeToDeleteCallback = SwipeToDeleteCallback()
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(patient_list_recyclerView)
        patient_searchView = findViewById(R.id.patient_list_searchView)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val menu: Menu = navigationView.getMenu()
        menu.clear()
        val userType: String = getIntent().getStringExtra("userType")
        navigationView.inflateMenu(R.menu.nav_menu)
        patientListMenuButton = findViewById(R.id.patient_list_menu_button)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        navigationView.setItemIconTintList(null)
        patientListMenuButton!!.setOnClickListener(
            { v: View? ->
                if (null != drawerLayout) {
                    drawerLayout.openDrawer(GravityCompat.START)
                    navigationView.setNavigationItemSelectedListener { menuItem ->
                        val id: Int = menuItem.getItemId()
                        var intent: Intent? = null
                        when (id) {
                            R.id.nav_home -> intent = Intent(
                                this@PatientListActivity,
                                if ((userType == "admin")) Homepage4admin::class.java else Homepage4caretaker::class.java
                            )

                            R.id.nav_signout -> {
                                FirebaseAuth.getInstance().signOut()
                                startActivity(
                                    Intent(
                                        this@PatientListActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            }
                        }
                        if (intent != null) {
                            startActivity(intent)
                            finish()
                        }
                        true
                    }
                }
            })
        val unique_patient_query: Query = FirebaseDatabase.getInstance()
            .getReference()
            .child("patient_profile")
            .orderByChild("userId")
            .equalTo(currentUserId)
        val all_options: FirebaseRecyclerOptions<Patient> = Builder<Patient>()
            .setQuery(
                unique_patient_query
            ) { snapshot ->
                val firstname: String =
                    if (null == snapshot.child("name").getValue()) "" else snapshot.child("name")
                        .getValue().toString()
                Log.d("PatientListActivity", "Data loaded" + firstname)
                val middlename: String = if (null == snapshot.child("middle_name")
                        .getValue()
                ) " " else snapshot.child("middle_name").getValue().toString()
                val lastname: String = if (null == snapshot.child("last_name")
                        .getValue()
                ) " " else snapshot.child("last_name").getValue().toString()
                val patient: Patient =
                    Patient(Objects.requireNonNull(snapshot.getKey()), firstname, lastname)
                Log.d("PatientListActivity", "patient loaded " + patient)
                if ("" !== middlename) patient.middleName = middlename
                patient
            }
            .build()
        val patientListAdapter_default =
            PatientListAdapter(this@PatientListActivity, all_options, false)
        patient_list_recyclerView.setLayoutManager(GridLayoutManager(this@PatientListActivity, 1))
        patient_list_recyclerView.setAdapter(patientListAdapter_default)
        patientListAdapter_default.startListening()
        patient_searchView.setOnQueryTextListener(
            object : OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String): Boolean {
                    if (s.isEmpty()) {
                        query =
                            FirebaseDatabase.getInstance().getReference().child("patient_profile")
                    } else {
                        query = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("patient_profile")
                            .orderByChild("userId")
                            .equalTo(currentUserId)
                    }
                    val options: FirebaseRecyclerOptions<Patient> = Builder<Patient>()
                        .setQuery(
                            query
                        ) { snapshot ->
                            val patient: Patient = Patient(
                                snapshot.getKey(),
                                snapshot.child("name").getValue().toString(),
                                snapshot.child("last_name").getValue().toString()
                            )
                            val middle_name: Any? = snapshot.child("middle_name").getValue()
                            if (null != middle_name) patient.middleName = middle_name.toString()
                            patient
                        }
                        .build()
                    patientListAdapter =
                        PatientListAdapter(this@PatientListActivity, options, false)
                    query.addListenerForSingleValueEvent(
                        object : ValueEventListener() {
                            fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    patientListAdapter =
                                        PatientListAdapter(this@PatientListActivity, options, false)
                                    patient_list_recyclerView.setLayoutManager(
                                        GridLayoutManager(this@PatientListActivity, 1)
                                    )
                                    patient_list_recyclerView.setAdapter(patientListAdapter)
                                    patientListAdapter.startListening()
                                } else {
                                    patient_list_recyclerView.setLayoutManager(
                                        GridLayoutManager(this@PatientListActivity, 1)
                                    )
                                    patient_list_recyclerView.setAdapter(
                                        PatientListAdapter(this@PatientListActivity, options, false)
                                    )
                                }
                            }

                            fun onCancelled(error: DatabaseError) {}
                        })
                    return true
                }
            })
    }

    fun updatePatientStatus(patientId: String?, newStatus: PatientStatus) {
        val patientsRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("patients")
        patientsRef
            .child(patientId)
            .child("status")
            .setValue(newStatus.toString())
            .addOnSuccessListener { aVoid ->
                Log.d(
                    "UpdateStatus",
                    "Patient status updated successfully."
                )
            }
            .addOnFailureListener { e ->
                Log.e(
                    "UpdateStatus",
                    "Failed to update patient status.",
                    e
                )
            }
    }

    inner class SwipeToDeleteCallback() : SimpleCallback(0, ItemTouchHelper.LEFT) {
        fun onMove(
            recyclerView: RecyclerView?,
            viewHolder: RecyclerView.ViewHolder?,
            target: RecyclerView.ViewHolder?
        ): Boolean {
            return false
        }

        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position: Int = viewHolder.getBindingAdapterPosition()
            AlertDialog.Builder(viewHolder.itemView.getContext())
                .setTitle("Delete Patient")
                .setMessage("Are you sure you want to delete this patient?")
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                        (Objects.requireNonNull(patient_list_recyclerView.getAdapter()) as PatientListAdapter)
                            .deleteItem(position)
                    })
                .setNegativeButton(
                    "No",
                    DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                        Objects.requireNonNull(patient_list_recyclerView.getAdapter())
                            .notifyItemChanged(position)
                    })
                .create()
                .show()
        }

        fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val itemView: View = viewHolder.itemView
            // final int backgroundCornerOffset = 20;
            val p = Paint()
            p.color = Color.RED
            val background = RectF(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(background, p)
            val trashIcon: Drawable =
                ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.trash)
            assert(null != trashIcon)
            val margin = (itemView.height - trashIcon.intrinsicHeight) / 2
            val top = itemView.top + (itemView.height - trashIcon.intrinsicHeight) / 2
            val bottom = top + trashIcon.intrinsicHeight
            val iconLeft = itemView.right - margin - trashIcon.intrinsicWidth
            val iconRight = itemView.right - margin
            trashIcon.setBounds(iconLeft, top, iconRight, bottom)
            trashIcon.draw(c)
        }
    }
}
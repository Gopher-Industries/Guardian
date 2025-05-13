package deakin.gopher.guardian.view.general;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientListAdapter;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.model.PatientStatus;
import java.util.Objects;

public class PatientListActivity extends BaseActivity {
    RecyclerView patient_list_recyclerView;
    PatientListAdapter patientListAdapter;
    Query query;
    SearchView patient_searchView;
    ImageView patientListMenuButton;

    private final Handler handler = new Handler();
    private final Runnable updateRunnable =
            new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    if (patientListAdapter != null) {
                        patientListAdapter.notifyDataSetChanged();
                    }
                    handler.postDelayed(this, 6000);
                }
            };

    public static final int ROLE_USER = 0;
    public static final int ROLE_ADMIN = 1;

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateRunnable);
        if (patientListAdapter != null) {
            patientListAdapter.startListening();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_list);
        patient_list_recyclerView = findViewById(R.id.patient_list_recycleView);
        final ImageView addPatientIcon = findViewById(R.id.imageView62);
        final Button viewArchivedButton = findViewById(R.id.button_view_archived);
        viewArchivedButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent intent =
                                new Intent(PatientListActivity.this, ArchivedPatientListActivity.class);
                        startActivity(intent);
                    }
                });

        addPatientIcon.setOnClickListener(
                v -> {
                    final Intent intent = new Intent(PatientListActivity.this, AddNewPatientActivity.class);
                    startActivity(intent);
                });

        final SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(patient_list_recyclerView);

        patient_searchView = findViewById(R.id.patient_list_searchView);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear();
        String userType = getIntent().getStringExtra("userType");

        navigationView.inflateMenu(R.menu.nav_menu);

        patientListMenuButton = findViewById(R.id.patient_list_menu_button);
        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setItemIconTintList(null);

        patientListMenuButton.setOnClickListener(
                v -> {
                    if (null != drawerLayout) {
                        drawerLayout.openDrawer(GravityCompat.START);

                        navigationView.setNavigationItemSelectedListener(
                                menuItem -> {
                                    int id = menuItem.getItemId();
                                    Intent intent = null;

                                    switch (id) {
                                        case R.id.nav_home:
                                            intent =
                                                    new Intent(
                                                            PatientListActivity.this,
                                                            userType.equals("admin")
                                                                    ? Homepage4admin.class
                                                                    : Homepage4caretaker.class);
                                            break;
                                        case R.id.nav_signout:
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(PatientListActivity.this, LoginActivity.class));
                                            finish();
                                    }
                                    if (intent != null) {
                                        startActivity(intent);
                                        finish();
                                    }
                                    return true;
                                });
                    }
                });

        final Query unique_patient_query =
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("patient_profile")
                        .orderByChild("userId");

        final FirebaseRecyclerOptions<Patient> all_options =
                new FirebaseRecyclerOptions.Builder<Patient>()
                        .setQuery(
                                unique_patient_query,
                                snapshot -> {
                                    final String firstname =
                                            null == snapshot.child("name").getValue()
                                                    ? ""
                                                    : snapshot.child("name").getValue().toString();
                                    Log.d("PatientListActivity", "Data loaded" + firstname);
                                    final String middlename =
                                            null == snapshot.child("middle_name").getValue()
                                                    ? " "
                                                    : snapshot.child("middle_name").getValue().toString();
                                    final String lastname =
                                            null == snapshot.child("last_name").getValue()
                                                    ? " "
                                                    : snapshot.child("last_name").getValue().toString();

                                    final Patient patient =
                                            new Patient(Objects.requireNonNull(snapshot.getKey()), firstname, lastname);

                                    Log.d("PatientListActivity", "patient loaded " + patient);

                                    if ("" != middlename) patient.middleName = middlename;

                                    return patient;
                                })
                        .build();
        final PatientListAdapter patientListAdapter_default =
                new PatientListAdapter(PatientListActivity.this, all_options, false);
        patient_list_recyclerView.setLayoutManager(new GridLayoutManager(PatientListActivity.this, 1));
        patient_list_recyclerView.setAdapter(patientListAdapter_default);
        patientListAdapter_default.startListening();
        patient_searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(final String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(final String s) {
                        if (s.isEmpty()) {
                            query = FirebaseDatabase.getInstance().getReference().child("patient_profile");
                        } else {
                            query =
                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("patient_profile")
                                            .orderByChild("userId");
                        }
                        final FirebaseRecyclerOptions<Patient> options =
                                new FirebaseRecyclerOptions.Builder<Patient>()
                                        .setQuery(
                                                query,
                                                snapshot -> {
                                                    final Patient patient =
                                                            new Patient(
                                                                    snapshot.getKey(),
                                                                    snapshot.child("name").getValue().toString(),
                                                                    snapshot.child("last_name").getValue().toString());
                                                    final Object middle_name = snapshot.child("middle_name").getValue();
                                                    if (null != middle_name) patient.middleName = middle_name.toString();
                                                    return patient;
                                                })
                                        .build();
                        patientListAdapter = new PatientListAdapter(PatientListActivity.this, options, false);
                        query.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            patientListAdapter =
                                                    new PatientListAdapter(PatientListActivity.this, options, false);
                                            patient_list_recyclerView.setLayoutManager(
                                                    new GridLayoutManager(PatientListActivity.this, 1));
                                            patient_list_recyclerView.setAdapter(patientListAdapter);
                                            patientListAdapter.startListening();
                                        } else {
                                            patient_list_recyclerView.setLayoutManager(
                                                    new GridLayoutManager(PatientListActivity.this, 1));
                                            patient_list_recyclerView.setAdapter(
                                                    new PatientListAdapter(PatientListActivity.this, options, false));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull final DatabaseError error) {}
                                });

                        return true;
                    }
                });
    }

    public void updatePatientStatus(final String patientId, final PatientStatus newStatus) {
        final DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference("patients");

        patientsRef
                .child(patientId)
                .child("status")
                .setValue(newStatus.toString())
                .addOnSuccessListener(
                        aVoid -> Log.d("UpdateStatus", "Patient status updated successfully."))
                .addOnFailureListener(e -> Log.e("UpdateStatus", "Failed to update patient status.", e));
    }

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        public SwipeToDeleteCallback() {
            super(0, ItemTouchHelper.LEFT);
        }

        @Override
        public boolean onMove(
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getBindingAdapterPosition();
            new AlertDialog.Builder(viewHolder.itemView.getContext())
                    .setTitle("Delete Patient")
                    .setMessage("Are you sure you want to delete this patient?")
                    .setPositiveButton(
                            "Yes",
                            (dialog, which) -> {
                                ((PatientListAdapter)
                                        Objects.requireNonNull(patient_list_recyclerView.getAdapter()))
                                        .deleteItem(position);
                            })
                    .setNegativeButton(
                            "No",
                            (dialog, which) -> {
                                Objects.requireNonNull(patient_list_recyclerView.getAdapter())
                                        .notifyItemChanged(position);
                            })
                    .create()
                    .show();
        }

        @Override
        public void onChildDraw(
                Canvas c,
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                float dX,
                float dY,
                int actionState,
                boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            final View itemView = viewHolder.itemView;
            final Paint p = new Paint();
            p.setColor(Color.RED);

            final RectF background =
                    new RectF(
                            (float) itemView.getRight() + dX,
                            (float) itemView.getTop(),
                            (float) itemView.getRight(),
                            (float) itemView.getBottom());
            c.drawRect(background, p);

            final Drawable trashIcon =
                    ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.trash);
            assert null != trashIcon;
            final int margin = (itemView.getHeight() - trashIcon.getIntrinsicHeight()) / 2;
            final int top =
                    itemView.getTop() + (itemView.getHeight() - trashIcon.getIntrinsicHeight()) / 2;
            final int bottom = top + trashIcon.getIntrinsicHeight();
            final int left = itemView.getRight() - margin - trashIcon.getIntrinsicWidth();
            final int right = itemView.getRight() - margin;
            trashIcon.setBounds(left, top, right, bottom);
            trashIcon.draw(c);
        }
    }
}

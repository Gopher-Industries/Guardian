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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientListAdapter;
import deakin.gopher.guardian.model.Patient;
import deakin.gopher.guardian.services.api.ApiClient;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListActivity extends BaseActivity {
    RecyclerView patient_list_recyclerView;
    PatientListAdapter patientListAdapter;
    SearchView patient_searchView;
    ImageView patientListMenuButton;
    DrawerLayout drawerLayout;
    List<Patient> patientList = new ArrayList<>();

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
    private String userType="admin";

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(updateRunnable);
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
        patient_searchView = findViewById(R.id.patient_list_searchView);
        patientListMenuButton = findViewById(R.id.menu_icon);
        drawerLayout = findViewById(R.id.drawer_layout);

        // Configure RecyclerView
        patientListAdapter = new PatientListAdapter(this, patientList, false);
        findViewById(R.id.button_view_patient_1).setOnClickListener(v -> {


            Intent intent =
                    new Intent(
                            this, PatientProfileActivity.class);




            if (intent != null) {
                startActivity(intent);

            }
        });
        findViewById(R.id.button_view_patient_2).setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            this, PatientProfileActivity.class);

            if (intent != null) {
                startActivity(intent);

            }
        });
        findViewById(R.id.button_view_patient_3).setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            this, PatientProfileActivity.class);




            startActivity(intent);

        });

        patient_list_recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        patient_list_recyclerView.setAdapter(patientListAdapter);

        // Swipe to delete
        final SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback();
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(patient_list_recyclerView);

        // Fetch patient list
        fetchPatients();

        // Set up search functionality
        patient_searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        searchPatients(query);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty()) {
                            fetchPatients();
                        } else {
                            searchPatients(newText);
                        }
                        return true;
                    }
                });

        // Navigation menu setup
        setupNavigationMenu();
    }

    private void fetchPatients() {
        ApiClient.INSTANCE
                .getApiService()
                .getPatients()
                .enqueue(
                        new Callback<List<Patient>>() {
                            @Override
                            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    patientList.clear();
                                    patientList.addAll(response.body());
                                    patientListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e("PatientListActivity", "Failed to fetch patients");
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Patient>> call, Throwable t) {
                                Log.e("PatientListActivity", "Error fetching patients", t);
                            }
                        });
    }

    private void searchPatients(String query) {
        ApiClient.INSTANCE
                .getApiService()
                .searchPatients(query)
                .enqueue(
                        new Callback<List<Patient>>() {
                            @Override
                            public void onResponse(Call<List<Patient>> call, Response<List<Patient>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    patientList.clear();
                                    patientList.addAll(response.body());
                                    patientListAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e("PatientListActivity", "Failed to search patients");
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Patient>> call, Throwable t) {
                                Log.e("PatientListActivity", "Error searching patients", t);
                            }
                        });
    }

    private void setupNavigationMenu() {
        final NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.clear();
        if (!TextUtils.isEmpty(getIntent().getStringExtra("userType"))){

            userType = getIntent().getStringExtra("userType");
        }

        navigationView.inflateMenu(R.menu.nav_menu);

        patientListMenuButton.setOnClickListener(
                v -> {
                    if (drawerLayout != null) {
                        drawerLayout.openDrawer(GravityCompat.START);
                        navigationView.setNavigationItemSelectedListener(
                                menuItem -> {
                                    int id = menuItem.getItemId();
                                    Intent intent = null;

                                    switch (id) {
                                        case R.id.nav_home:
                                            intent =
                                                    new Intent(
                                                            this,
                                                            userType.equals("admin")
                                                                    ? Homepage4admin.class
                                                                    : Homepage4caretaker.class);
                                            break;
                                        case R.id.nav_signout:
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                            break;
                                    }
                                    if (intent != null) {
                                        startActivity(intent);
                                        finish();
                                    }
                                    return true;
                                });
                    }
                });
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
                    .setPositiveButton("Yes", (dialog, which) -> deletePatient(position))
                    .setNegativeButton(
                            "No", (dialog, which) -> patientListAdapter.notifyItemChanged(position))
                    .create()
                    .show();
        }

        private void deletePatient(int position) {
            Patient patient = patientList.get(position);
            ApiClient.INSTANCE
                    .getApiService()
                    .deletePatient(patient.getPatientId())
                    .enqueue(
                            new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        patientList.remove(position);
                                        patientListAdapter.notifyItemRemoved(position);
                                    } else {
                                        Log.e("PatientListActivity", "Failed to delete patient");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Log.e("PatientListActivity", "Error deleting patient", t);
                                }
                            });
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
            assert trashIcon != null;
            final int margin = (itemView.getHeight() - trashIcon.getIntrinsicHeight()) / 2;
            final int top = itemView.getTop() + margin;
            final int bottom = top + trashIcon.getIntrinsicHeight();
            final int iconLeft = itemView.getRight() - margin - trashIcon.getIntrinsicWidth();
            final int iconRight = itemView.getRight() - margin;
            trashIcon.setBounds(iconLeft, top, iconRight, bottom);
            trashIcon.draw(c);
        }
    }
}

package deakin.gopher.guardian.view.general;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.PatientListAdapter;
import deakin.gopher.guardian.model.Patient;
import java.util.Objects;

public class PatientListActivity extends BaseActivity {
  RecyclerView patient_list_recyclerView;
  PatientListAdapter patientListAdapter;
  Query query;
  SearchView patient_searchView;
  ImageView patientListMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_patient_list);
    patient_list_recyclerView = findViewById(R.id.patient_list_recycleView);
    final ImageView addPatientIcon = findViewById(R.id.imageView62);
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
    patientListMenuButton = findViewById(R.id.patient_list_menu_button);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    patientListMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    final Query all_query = FirebaseDatabase.getInstance().getReference().child("patient_profile");
    //      Log.d("PatientListActivity", "Data loaded" + all_query);
    final FirebaseRecyclerOptions<Patient> all_options =
        new FirebaseRecyclerOptions.Builder<Patient>()
            .setQuery(
                all_query,
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

                  final Patient patient = new Patient(snapshot.getKey(), firstname, lastname);

                  if ("" != middlename) patient.setMiddleName(middlename);

                  return patient;
                })
            .build();
    final PatientListAdapter patientListAdapter_default =
        new PatientListAdapter(PatientListActivity.this, all_options);
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
                      .orderByChild("name")
                      .startAt(s)
                      .endAt(s + "\uf8ff")
                      .limitToFirst(10);
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
                          if (null != middle_name) patient.setMiddleName(middle_name.toString());
                          return patient;
                        })
                    .build();
            patientListAdapter = new PatientListAdapter(PatientListActivity.this, options);
            query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull final DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                      patientListAdapter =
                          new PatientListAdapter(PatientListActivity.this, options);
                      patient_list_recyclerView.setLayoutManager(
                          new GridLayoutManager(PatientListActivity.this, 1));
                      patient_list_recyclerView.setAdapter(patientListAdapter);
                      patientListAdapter.startListening();
                    } else {
                      patient_list_recyclerView.setLayoutManager(
                          new GridLayoutManager(PatientListActivity.this, 1));
                      patient_list_recyclerView.setAdapter(
                          new PatientListAdapter(PatientListActivity.this, options));
                    }
                  }

                  @Override
                  public void onCancelled(@NonNull final DatabaseError error) {}
                });

            return true;
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
      ((PatientListAdapter) Objects.requireNonNull(patient_list_recyclerView.getAdapter()))
          .deleteItem(position);
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
      // final int backgroundCornerOffset = 20;

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

      final int iconLeft = itemView.getRight() - margin - trashIcon.getIntrinsicWidth();
      final int iconRight = itemView.getRight() - margin;
      trashIcon.setBounds(iconLeft, top, iconRight, bottom);

      trashIcon.draw(c);
    }
  }
}

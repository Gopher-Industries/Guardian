package deakin.gopher.guardian.view.caretaker.notifications.confirmincident;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.view.caretaker.CaretakerDashboardActivity;
import deakin.gopher.guardian.view.general.BaseActivity;

public class ConfirmIncidentActivity extends BaseActivity {

  Spinner hospitalSpinner;
  String hospitalSelection;
  ImageView confirmIncidentMenuButton;

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_confirm_incident);

    final NavigationView navigationView = findViewById(R.id.nav_view);
    confirmIncidentMenuButton = findViewById(R.id.menuButton);
    final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
    navigationView.setItemIconTintList(null);

    confirmIncidentMenuButton.setOnClickListener(
        v -> {
          drawerLayout.openDrawer(GravityCompat.START);
        });

    hospitalSpinner = findViewById(R.id.hospitalSpinner);

    // Create an ArrayAdapter using the string array and a default Spinner layout
    final ArrayAdapter<CharSequence> hospitalAdapter =
        ArrayAdapter.createFromResource(
            getApplicationContext(), R.array.hospital_list_array, R.layout.spinner_layout);

    // Apply the adapter to the Spinner
    hospitalSpinner.setAdapter(hospitalAdapter);

    // Set the Spinner to the list of choices
    hospitalSpinner.setOnItemSelectedListener(new SpinnerActivity());

    hospitalSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(
              final AdapterView<?> parentView,
              final View selectedItemView,
              final int position,
              final long id) {
            hospitalSelection = parentView.getItemAtPosition(position).toString();
          }

          @Override
          public void onNothingSelected(final AdapterView<?> adapterView) {}
        });
  }

  public void onConfirmIncidentCancelClick(final View view) {
    final Intent medicalDiagnosticsActivityIntent =
        new Intent(ConfirmIncidentActivity.this, CaretakerDashboardActivity.class);
    startActivity(medicalDiagnosticsActivityIntent);
  }

  public static class SpinnerActivity extends Activity
      implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(
        final AdapterView<?> parent, final View view, final int pos, final long id) {}

    public void onNothingSelected(final AdapterView<?> parent) {}
  }
}

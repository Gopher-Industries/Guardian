package deakin.gopher.guardian.view.patient.careplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.model.CarePlan;

public class CarePlanSummaryActivityFragment extends Fragment {
  Button prevButton;
  Button editButton;
  ImageView carePlanSummaryMenuButton;

  // Firebase
  DatabaseReference carePlanRef;

  TextView carePlanSummaryTextView;
  TextView carePlanNutHyd;
  TextView suppReq;
  TextView dietTime;
  TextView drinkLike;
  TextView sleepPat;
  TextView pain;
  TextView behaveMng;
  RatingBar ratingBar;
  String patientId;

  View view;

  public CarePlanSummaryActivityFragment(final String patientId) {
    this.patientId = patientId;
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    view = inflater.inflate(R.layout.fragment_careplan_summary, container, false);

    editButton = view.findViewById(R.id.save);
    carePlanSummaryTextView = view.findViewById(R.id.carePlanSummary);
    carePlanNutHyd = view.findViewById(R.id.nutritionHydrationSummary);
    suppReq = view.findViewById(R.id.supportReqSummary1);
    dietTime = view.findViewById(R.id.dietTimingSummary);
    drinkLike = view.findViewById(R.id.drinkLikesSummary);
    sleepPat = view.findViewById(R.id.sleepPatternSummary);
    pain = view.findViewById(R.id.painSummary);
    ratingBar = view.findViewById(R.id.painRatingBarSummary);
    behaveMng = view.findViewById(R.id.behaviouralManagement);

    // Set onClickListener for editButton
    editButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            // Open CarePlanActivity
            final CarePlanActivity carePlanActivity = new CarePlanActivity();
            final Intent intent = new Intent(getActivity(), carePlanActivity.getClass());
            intent.putExtra("patientId", patientId);
            startActivity(intent);
          }
        });

    // Firebase initialization
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    carePlanRef = database.getReference("careplan");
    // Load care plan summary data from Firebase
    loadCarePlanDataForPatient(patientId);
    return view;
  }

  private void loadCarePlanDataForPatient(final String patientId) {
    DatabaseReference patientRef = carePlanRef.child(patientId);
    patientRef.addListenerForSingleValueEvent(
        new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
              CarePlan carePlan = dataSnapshot.getValue(CarePlan.class);
              if (carePlan != null) {
                carePlanSummaryTextView.setText(carePlan.getCarePlanType());
                carePlanNutHyd.setText(carePlan.getNutritionHydration());
                suppReq.setText(carePlan.getSupportRequirement());
                dietTime.setText(carePlan.getDietTimings());
                drinkLike.setText(carePlan.getDrinkLikings());
                sleepPat.setText(carePlan.getSleepPattern());
                ratingBar.setRating((float) carePlan.getPainScore() / 2);
                behaveMng.setText(carePlan.getBehavioralManagement());
              }
            } else {
              carePlanSummaryTextView.setText("Add new one");
              carePlanNutHyd.setVisibility(View.GONE);
              suppReq.setVisibility(View.GONE);
              dietTime.setVisibility(View.GONE);
              drinkLike.setVisibility(View.GONE);
              sleepPat.setVisibility(View.GONE);
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
            // Handle error
          }
        });
  }

  // Interface to notify data load status
  private interface OnDataLoadListener {
    void onDataLoaded(boolean dataFound);
  }
}

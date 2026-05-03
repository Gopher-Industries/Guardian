package deakin.gopher.guardian.view.patient.careplan;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // ADDED
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast; // ADDED
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

        // ADDED: Validate patientId before trying to load data
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(getActivity(), "Invalid patient", Toast.LENGTH_SHORT).show();
            return view;
        }

        // IMPROVED: Simplified edit navigation
        editButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(getActivity(), CarePlanActivity.class);
                    intent.putExtra("patientId", patientId);
                    startActivity(intent);
                });

        // Firebase initialization
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        carePlanRef = database.getReference("careplan");

        // ADDED: Basic loading state
        carePlanSummaryTextView.setText("Loading care plan...");

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
                                // IMPROVED: Use fallback text for null or empty values
                                carePlanSummaryTextView.setText(getSafeText(carePlan.carePlanType));
                                carePlanNutHyd.setText(getSafeText(carePlan.nutritionHydration));
                                suppReq.setText(getSafeText(carePlan.supportRequirement));
                                dietTime.setText(getSafeText(carePlan.dietTimings));
                                drinkLike.setText(getSafeText(carePlan.drinkLikings));
                                sleepPat.setText(getSafeText(carePlan.sleepPattern));
                                pain.setText(getSafeText(carePlan.painCategories));
                                behaveMng.setText(getSafeText(carePlan.behavioralManagement));

                                // IMPROVED: Ensure rating is set safely
                                ratingBar.setRating((float) carePlan.painScore / 2);
                            } else {
                                showEmptyState();
                            }
                        } else {
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // ADDED: Show user-friendly error feedback
                        Toast.makeText(getActivity(), "Failed to load care plan", Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                });
    }

    // ADDED: Reusable fallback for null/empty text values
    private String getSafeText(String value) {
        return TextUtils.isEmpty(value) ? "Not available" : value;
    }

    // ADDED: Cleaner empty state handling
    private void showEmptyState() {
        carePlanSummaryTextView.setText("No care plan available");
        carePlanNutHyd.setText("Not available");
        suppReq.setText("Not available");
        dietTime.setText("Not available");
        drinkLike.setText("Not available");
        sleepPat.setText("Not available");
        pain.setText("Not available");
        behaveMng.setText("Not available");
        ratingBar.setRating(0);
    }

    // Interface to notify data load status
    private interface OnDataLoadListener {
        void onDataLoaded(boolean dataFound);
    }
}
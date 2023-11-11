package com.gopher.guardian.view.nextofkin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.gopher.guardian.R;

public class NextOfKinFragment extends Fragment {

  public NextOfKinFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_next_of_kin, container, false);
  }
}

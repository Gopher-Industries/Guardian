
package deakin.gopher.guardian.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;
import deakin.gopher.guardian.R;
import deakin.gopher.guardian.adapter.NotificationAdapter;

public class AdminNotificationsFragment extends Fragment {

    private RecyclerView notificationsRecyclerView;

    public AdminNotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample notifications data
        List<String> notifications = Arrays.asList(
                "Wi-Fi CSI anomaly detected",
                "Failed login attempt from IP 192.168.1.10",
                "System reboot detected",
                "User account locked due to multiple failed logins"
        );

        NotificationAdapter adapter = new NotificationAdapter(notifications);
        notificationsRecyclerView.setAdapter(adapter);
    }
}

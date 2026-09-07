package com.example.hopstack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class admin extends Fragment {

    private ImageView attendanceBtn, qrBtn, profileBtn, grievanceBtn, hopbtn,reportbtn,memorybtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize buttons
        hopbtn = rootView.findViewById(R.id.adhoplogo);
        attendanceBtn = rootView.findViewById(R.id.adattedanceimg);
        qrBtn = rootView.findViewById(R.id.adQRimg);
        profileBtn = rootView.findViewById(R.id.adprofileimg);
        grievanceBtn = rootView.findViewById(R.id.adgrievanceimg);
        reportbtn = rootView.findViewById(R.id.adreportimg);
        memorybtn = rootView.findViewById(R.id.adhostelvibeimg);

        // Title Change

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {  // Check if ActionBar exists before setting title
            actionBar.setTitle("Admin");
}


        // **Fix: Ensure getActivity() is not null before accessing DrawerLayout**
        if (getActivity() != null) {
            DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
            if (drawerLayout != null) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        }

        // Set click listeners
        attendanceBtn.setOnClickListener(view -> openActivity(View_Attendance.class));
        qrBtn.setOnClickListener(view -> openActivity(Generate_QR.class));
        profileBtn.setOnClickListener(view -> openActivity(View_Profile.class));
        grievanceBtn.setOnClickListener(view -> openActivity(View_Grevience.class));
        hopbtn.setOnClickListener(view -> openActivity(hopinfo.class));
        reportbtn.setOnClickListener(view -> openActivity(View_reports.class));
        memorybtn.setOnClickListener(view -> openActivity(View_memories.class));

        return rootView;
    }

    private void openActivity(Class<?> activityClass) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), activityClass);
            startActivity(intent);
        }
    }
}

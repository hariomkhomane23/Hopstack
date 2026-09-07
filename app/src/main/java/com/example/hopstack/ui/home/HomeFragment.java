package com.example.hopstack.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hopstack.Attendance;
import com.example.hopstack.DisplayProfile;
import com.example.hopstack.Grievance;
import com.example.hopstack.Hopstack_info;
import com.example.hopstack.HostelVibes;
import com.example.hopstack.LegacyHub;
import com.example.hopstack.MindSpeak;
import com.example.hopstack.Payment;
import com.example.hopstack.Profile;
import com.example.hopstack.Rules;
import com.example.hopstack.Scanner;
import com.example.hopstack.StandStrong;
import com.example.hopstack.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.hoplogo.setOnClickListener(v -> startActivity(new Intent(getActivity(), Hopstack_info.class)));
        binding.attedanceimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), Attendance.class)));
        binding.paymentimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), Payment.class)));
        binding.rulesimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), Rules.class)));
        binding.grievanceimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), Grievance.class)));
        binding.scannerimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), Scanner.class)));

        binding.myprofile.setOnClickListener(v -> checkProfile());

        binding.mindspeakimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), MindSpeak.class)));
        binding.standstrongimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), StandStrong.class)));
        binding.hostelvibeimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), HostelVibes.class)));
        binding.legacyhubimg.setOnClickListener(v -> startActivity(new Intent(getActivity(), LegacyHub.class)));
        return root;
    }

    private void checkProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userEmailKey = user.getEmail().replace(".", "_");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Profiles").child(userEmailKey);

            // Show loading dialog
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading profile...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    progressDialog.dismiss(); // Hide loading dialog when data is fetched

                    if (snapshot.exists()) {
                        startActivity(new Intent(getActivity(), DisplayProfile.class));
                    } else {
                        startActivity(new Intent(getActivity(), Profile.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressDialog.dismiss(); // Hide loading dialog
                    Toast.makeText(getActivity(), "Error checking profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void openScanner() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(requireActivity());
        intentIntegrator.setPrompt("Scan a QR code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

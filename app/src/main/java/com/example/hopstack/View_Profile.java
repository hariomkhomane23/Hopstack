package com.example.hopstack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class View_Profile extends AppCompatActivity {

    private ListView profileListView;
    private ArrayList<String> profileList;
    private ArrayList<String> emailList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private static final String TAG = "View_Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        profileListView = findViewById(R.id.profileListView);
        profileList = new ArrayList<>();
        emailList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profileList);
        profileListView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading profiles...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fetchProfiles();

        profileListView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d(TAG, "Clicked Position: " + position);
            if (position >= 0 && position < emailList.size()) {
                String selectedEmail = emailList.get(position);
                Log.d(TAG, "Selected Profile: " + profileList.get(position));
                Log.d(TAG, "Corresponding Email: " + selectedEmail);

                Intent intent = new Intent(View_Profile.this, DetailedProfileActivity.class);
                intent.putExtra("email", selectedEmail);
                startActivity(intent);
            } else {
                Toast.makeText(View_Profile.this, "Error: Invalid selection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchProfiles() {
        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("Profiles");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileList.clear();
                emailList.clear();

                Log.d(TAG, "DataSnapshot Received: " + dataSnapshot.toString());

                if (!dataSnapshot.exists()) {
                    Toast.makeText(View_Profile.this, "No profiles found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                List<ProfileData> profiles = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String encodedEmail = snapshot.getKey(); // Firebase stores email with ','
                    Log.d(TAG, "Processing Entry Key: " + encodedEmail);

                    if (encodedEmail != null) {
                        String decodedEmail = encodedEmail.replace(",", "."); // Convert back to normal email
                        String name = snapshot.child("name").getValue(String.class);
                        String branch = snapshot.child("branch").getValue(String.class);

                        if (name != null && branch != null) {
                            profiles.add(new ProfileData(name + " - " + branch, decodedEmail));
                            Log.d(TAG, "Profile Added: " + name + " - " + branch + " (" + decodedEmail + ")");
                        } else {
                            Log.e(TAG, "Profile Data Missing for Email: " + decodedEmail);
                        }
                    }
                }

                // Sort profiles list by name and branch
                Collections.sort(profiles, (p1, p2) -> p1.getNameBranch().compareToIgnoreCase(p2.getNameBranch()));

                // Populate lists with sorted data
                for (ProfileData profile : profiles) {
                    profileList.add(profile.getNameBranch());
                    emailList.add(profile.getEmail());
                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

                Log.d(TAG, "Final Profile List Size: " + profileList.size());

                if (profileList.isEmpty()) {
                    Toast.makeText(View_Profile.this, "No profiles found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.e(TAG, "Firebase Error: " + databaseError.getMessage());
                Toast.makeText(View_Profile.this, "Failed to load profiles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle Back Button Click in Action Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openAdminFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle Hardware Back Button Press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openAdminFragment();
    }

    // Open Admin Fragment inside Drawer Activity
    private void openAdminFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openAdmin", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // Helper class to maintain profile data consistency
    static class ProfileData {
        private final String nameBranch;
        private final String email;

        public ProfileData(String nameBranch, String email) {
            this.nameBranch = nameBranch;
            this.email = email;
        }

        public String getNameBranch() { return nameBranch; }
        public String getEmail() { return email; }
    }
}

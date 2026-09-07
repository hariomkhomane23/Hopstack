package com.example.hopstack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.Map;

public class View_Grevience extends AppCompatActivity {

    private ListView grievanceListView;
    private ArrayList<String> grievanceList;
    private ArrayList<Map<String, String>> grievanceDataList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grevience);

        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        grievanceListView = findViewById(R.id.grevienceListView);
        grievanceList = new ArrayList<>();
        grievanceDataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, grievanceList);
        grievanceListView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("grievances");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading grievances...");
        progressDialog.setCancelable(false);

        loadGrievances();

        grievanceListView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, String> selectedGrievance = grievanceDataList.get(position);

            Intent intent = new Intent(View_Grevience.this, DetailedGrevienceActivity.class);
            intent.putExtra("grievanceId", selectedGrievance.get("grievanceId"));
            startActivity(intent);
        });
    }

    private void loadGrievances() {
        progressDialog.show();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                grievanceList.clear();
                grievanceDataList.clear();

                if (!dataSnapshot.exists()) {
                    Toast.makeText(View_Grevience.this, "No grievances found!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String grievanceId = snapshot.getKey();
                    String username = snapshot.child("username").getValue(String.class);
                    String enrollment = snapshot.child("enrollment").getValue(String.class);
                    String comment = snapshot.child("comment").getValue(String.class);
                    String file = snapshot.child("file").getValue(String.class);

                    if (grievanceId != null && username != null && enrollment != null) {
                        String displayText = username + " - " + enrollment + " (ID: " + grievanceId + ")";
                        grievanceList.add(displayText);

                        Map<String, String> grievanceData = new HashMap<>();
                        grievanceData.put("grievanceId", grievanceId);
                        grievanceData.put("username", username);
                        grievanceData.put("enrollment", enrollment);
                        grievanceData.put("comment", comment != null ? comment : "No comment provided");
                        grievanceData.put("file", file != null ? file : "No file uploaded");

                        grievanceDataList.add(grievanceData);
                    }
                }

                if (grievanceList.isEmpty()) {
                    Toast.makeText(View_Grevience.this, "No grievances found!", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.notifyDataSetChanged();
                    Toast.makeText(View_Grevience.this, "Grievances loaded successfully!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(View_Grevience.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    // Handle Back Button Click in Action Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openAdminFragment(); // Navigate to Admin Fragment
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle Hardware Back Button Press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openAdminFragment(); // Navigate to Admin Fragment
    }

    // Open Admin Fragment inside Drawer Activity
    private void openAdminFragment() {
        Intent intent = new Intent(this, MainActivity.class); // Replace with your Drawer Activity
        intent.putExtra("openAdmin", true); // Flag to indicate Admin Fragment should open
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Close current activity
    }
}

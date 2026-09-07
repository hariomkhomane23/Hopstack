package com.example.hopstack;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

public class View_Attendance extends AppCompatActivity {

    private ListView studentListView;
    private ArrayList<String> studentList;
    private ArrayList<String> emailList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference attendanceRef, registrationRef;
    private ProgressDialog progressDialog;
    private static final String TAG = "View_Attendance";
    private Map<String, String> studentDetails; // Stores email -> "Name - Branch"

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        studentListView = findViewById(R.id.studentListView);

        studentList = new ArrayList<>();
        emailList = new ArrayList<>();
        studentDetails = new HashMap<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentList);
        studentListView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading students...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fetchStudentDetails();

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0 && position < emailList.size()) {
                String selectedEmail = emailList.get(position);
                Log.d(TAG, "Opening attendance details for: " + selectedEmail);

                Intent intent = new Intent(View_Attendance.this, View_Attendance_Details.class);
                intent.putExtra("email", selectedEmail);
                startActivity(intent);
            } else {
                Toast.makeText(View_Attendance.this, "Error: Invalid selection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudentDetails() {
        registrationRef = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("registration_users");

        registrationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(View_Attendance.this, "No registered students found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String emailKey = userSnapshot.getKey();
                    if (emailKey != null) {
                        String email = emailKey.replace("_", ".");
                        String name = userSnapshot.child("name").getValue(String.class);
                        String branch = userSnapshot.child("branch").getValue(String.class);

                        if (name != null && branch != null) {
                            studentDetails.put(email, name + " - " + branch);
                        }
                    }
                }
                fetchAttendanceRecords();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching registered users: " + error.getMessage());
            }
        });
    }

    private void fetchAttendanceRecords() {
        attendanceRef = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("Attendance");

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                emailList.clear();

                if (!snapshot.exists()) {
                    Toast.makeText(View_Attendance.this, "No attendance records found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                for (DataSnapshot snapshotChild : snapshot.getChildren()) {
                    String emailKey = snapshotChild.getKey();
                    if (emailKey != null) {
                        String email = emailKey.replace(",", ".");
                        String studentInfo = studentDetails.get(email);

                        if (studentInfo != null) {
                            studentList.add(studentInfo);
                        } else {
                            studentList.add(email + " (Details Missing)");
                        }
                        emailList.add(email);
                    }
                }

                Collections.sort(studentList, String::compareToIgnoreCase);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

                if (studentList.isEmpty()) {
                    Toast.makeText(View_Attendance.this, "No students found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Log.e(TAG, "Firebase Error: " + error.getMessage());
                Toast.makeText(View_Attendance.this, "Failed to load students", Toast.LENGTH_SHORT).show();
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


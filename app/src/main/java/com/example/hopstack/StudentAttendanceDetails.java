package com.example.hopstack;

import android.os.Bundle;
import android.util.Log;
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

public class StudentAttendanceDetails extends AppCompatActivity {

    private ListView attendanceListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> attendanceList = new ArrayList<>();
    private DatabaseReference attendanceRef;
    private static final String TAG = "StudentAttendanceDetails";
    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_details);

        attendanceListView = findViewById(R.id.attendanceListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendanceList);
        attendanceListView.setAdapter(adapter);

        // Get student email from intent
        studentEmail = getIntent().getStringExtra("studentEmailKey");

        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            Toast.makeText(this, "Error: No student selected!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Received null or empty student email!");
            finish();
            return;
        }

        Log.d(TAG, "Fetching attendance for student: " + studentEmail);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/");
        attendanceRef = database.getReference("Attendance").child(studentEmail);

        fetchAttendanceDetails();
    }

    private void fetchAttendanceDetails() {
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendanceList.clear();

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    Toast.makeText(StudentAttendanceDetails.this, "No attendance records found!", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "No attendance data found for: " + studentEmail);
                    return;
                }

                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey(); // Example: "2025-03-06"
                    String status = dateSnapshot.getValue(String.class); // Fetch the attendance status directly

                    if (status == null) {
                        status = "Unknown";
                    }

                    String attendanceEntry = "Date: " + date + " - Status: " + status;
                    attendanceList.add(attendanceEntry);
                }

                adapter.notifyDataSetChanged();
                Log.d(TAG, "Attendance records loaded successfully.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentAttendanceDetails.this, "Failed to load attendance!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Firebase error: " + error.getMessage());
            }
        });
    }
}

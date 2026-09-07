package com.example.hopstack;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class Attendance extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView attendanceStatus;
    private HashMap<String, String> attendanceMap = new HashMap<>(); // Store attendance records
    private DatabaseReference attendanceRef;
    private FirebaseUser currentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        calendarView = findViewById(R.id.calenderview);
        attendanceStatus = findViewById(R.id.attendance_status);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String emailKey = currentUser.getEmail().replace(".", "_").replace("@", "_");
        attendanceRef = FirebaseDatabase.getInstance()
                .getReference("Attendance")
                .child(emailKey);

        fetchAttendanceData();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String clickedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            if (attendanceMap.containsKey(clickedDate)) {
                String status = attendanceMap.get(clickedDate);
                attendanceStatus.setText(clickedDate + ": " + (status.equals("Present") ? "✅ Present" : "❌ Absent"));
            } else {
                attendanceStatus.setText(clickedDate + ": No attendance recorded");
            }
        });
    }

    private void fetchAttendanceData() {
        attendanceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                attendanceMap.clear();
                for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    String status = dateSnapshot.getValue(String.class);

                    if (status != null) {
                        attendanceMap.put(date, status);
                    }
                }
                updateCalendarView();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(Attendance.this, "Failed to fetch attendance!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to update calendar UI (colors)
    private void updateCalendarView() {
        long currentDateMillis = calendarView.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date(currentDateMillis));

        if (attendanceMap.containsKey(todayDate)) {
            String status = attendanceMap.get(todayDate);
            if (status.equals("Present")) {
                calendarView.setBackgroundColor(Color.parseColor("#90EE90")); // Light green for present
            } else {
                calendarView.setBackgroundColor(Color.parseColor("#FFCCCB")); // Light red for absent
            }
        } else {
            calendarView.setBackgroundColor(Color.WHITE); // Default background
        }
    }
}

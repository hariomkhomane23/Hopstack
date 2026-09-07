package com.example.hopstack;

import android.app.ProgressDialog;
import android.os.Bundle;
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

public class StudentAttendanceActivity extends AppCompatActivity {

    private ListView attendanceListView;
    private ArrayList<String> attendanceList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

        attendanceListView = findViewById(R.id.attendanceListView);
        attendanceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, attendanceList);
        attendanceListView.setAdapter(adapter);

        studentEmail = getIntent().getStringExtra("email");
        if (studentEmail == null) {
            Toast.makeText(this, "Error: No student selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading attendance...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        fetchAttendance();
    }

    private void fetchAttendance() {
        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("Attendance").child(studentEmail.replace(".", ",")); // Convert email to Firebase format

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attendanceList.clear();

                if (!dataSnapshot.exists()) {
                    Toast.makeText(StudentAttendanceActivity.this, "No attendance records found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.getKey();
                    String status = snapshot.getValue(String.class);

                    if (date != null && status != null) {
                        attendanceList.add(date + " - " + status);
                    }
                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

                if (attendanceList.isEmpty()) {
                    Toast.makeText(StudentAttendanceActivity.this, "No attendance records found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(StudentAttendanceActivity.this, "Failed to load attendance", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

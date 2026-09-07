package com.example.hopstack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailedAttendanceActivity extends AppCompatActivity {

    private TextView dateTextView, statusTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_attendance);

        dateTextView = findViewById(R.id.dateTextView);
        statusTextView = findViewById(R.id.statusTextView);

        // Receive data from intent
        Intent intent = getIntent();
        if (intent != null) {
            String date = intent.getStringExtra("date");
            String status = intent.getStringExtra("status");

            dateTextView.setText("Date: " + date);
            statusTextView.setText("Status: " + status);
        }
    }
}

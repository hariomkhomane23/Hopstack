package com.example.hopstack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Scanner extends AppCompatActivity {

    private Button scanButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private static final String TAG = "ScannerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        scanButton = findViewById(R.id.scanButton);
        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/").getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        scanButton.setOnClickListener(view -> startScanner());
    }

    private void startScanner() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan the QR Code");
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setCameraId(0);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result == null || result.getContents() == null) {
            Toast.makeText(this, "Scan cancelled! Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String scannedData = result.getContents();
        Log.d(TAG, "Scanned Data: " + scannedData);
        Toast.makeText(this, "Scanned Data:\n" + scannedData, Toast.LENGTH_LONG).show();

        // Extract data from scanned text
        String scannedDate = extractValue(scannedData, "Date");
        String scannedTime = extractValue(scannedData, "Time");
        String scannedLocation = extractValue(scannedData, "Location");

        if (scannedDate == null || scannedTime == null || scannedLocation == null) {
            Toast.makeText(this, "Invalid QR Code format!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get today's date
        String todayDate = getCurrentDate();
        if (!scannedDate.equals(todayDate)) {
            Toast.makeText(this, "Invalid QR Code! Scan today's QR.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store "Present" in attendance
        markAttendancePresent(scannedDate);
    }

    private void markAttendancePresent(String date) {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailKey = formatEmailKey(userEmail);
        DatabaseReference attendanceRef = databaseReference.child("Attendance").child(emailKey);

        // Mark attendance as "Present"
        attendanceRef.child(date).setValue("Present")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Scanner.this, "Attendance Marked as Present!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Attendance successfully recorded.");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Scanner.this, "Failed to save attendance!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error saving attendance", e);
                });
    }

    private String extractValue(String scannedData, String key) {
        try {
            String[] lines = scannedData.split("\n");
            for (String line : lines) {
                String[] keyValue = line.split(":", 2);
                if (keyValue.length == 2 && keyValue[0].trim().equalsIgnoreCase(key)) {
                    return keyValue[1].trim();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting value for key: " + key, e);
        }
        return null;
    }

    private String formatEmailKey(String email) {
        return email.replace(".", "_").replace("@", "_");
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}

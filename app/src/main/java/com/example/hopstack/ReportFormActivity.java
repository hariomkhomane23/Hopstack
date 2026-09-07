package com.example.hopstack;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ReportFormActivity extends AppCompatActivity {

    private EditText reportInput;
    private Button submitReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);

        reportInput = findViewById(R.id.reportInput);
        submitReport = findViewById(R.id.submitReportButton);

        submitReport.setOnClickListener(v -> submitRaggingReport());
    }

    private void submitRaggingReport() {
        String reportText = reportInput.getText().toString().trim();
        if (reportText.isEmpty()) {
            Toast.makeText(this, "Please enter details!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RaggingReports");
        String reportId = databaseReference.push().getKey();

        // Get current date and time
        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date());

        // Store report with date
        HashMap<String, String> reportData = new HashMap<>();
        reportData.put("id", reportId);
        reportData.put("report", reportText);
        reportData.put("date", currentDate);

        databaseReference.child(reportId).setValue(reportData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Report submitted anonymously!", Toast.LENGTH_SHORT).show();
                    reportInput.setText("");
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to submit report!", Toast.LENGTH_SHORT).show());
    }
}

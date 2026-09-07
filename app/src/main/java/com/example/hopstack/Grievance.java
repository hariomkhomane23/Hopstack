package com.example.hopstack;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Grievance extends AppCompatActivity {

    private static final String TAG = "GrievanceActivity";

    private EditText editUsername, editEnrollment, editComment;
    private Button uploadFileBtn, submitBtn;

    private Uri selectedFileUri = null;
    private DatabaseReference grievanceRef;
    private ProgressDialog progressDialog;  // Progress Dialog for loading

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grievance);

        editUsername = findViewById(R.id.username);
        editEnrollment = findViewById(R.id.enno);
        editComment = findViewById(R.id.comment);
        uploadFileBtn = findViewById(R.id.button2);
        submitBtn = findViewById(R.id.submit);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/");
        grievanceRef = database.getReference("grievances");

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading grievance...");
        progressDialog.setCancelable(false);

        ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedFileUri = uri;
                        Log.d(TAG, "File selected URI: " + uri);
                        Toast.makeText(this, "File selected: " + uri.getPath(), Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "No file selected");
                        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        uploadFileBtn.setOnClickListener(v -> {
            Log.d(TAG, "Opening file picker...");
            filePickerLauncher.launch("*/*");
        });

        submitBtn.setOnClickListener(v -> submitGrievance());
    }

    private void submitGrievance() {
        String username = editUsername.getText().toString().trim();
        String enrollment = editEnrollment.getText().toString().trim();
        String comment = editComment.getText().toString().trim();

        if (username.isEmpty() || enrollment.isEmpty() || comment.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress Dialog
        progressDialog.show();

        Map<String, Object> grievanceData = new HashMap<>();
        grievanceData.put("username", username);
        grievanceData.put("enrollment", enrollment);
        grievanceData.put("comment", comment);

        if (selectedFileUri != null) {
            String fileBase64 = convertFileToBase64(selectedFileUri);
            if (fileBase64 != null) {
                grievanceData.put("file", fileBase64);
            }
        }

        grievanceRef.push().setValue(grievanceData)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss(); // Hide Progress Dialog

                    if (task.isSuccessful()) {
                        Toast.makeText(Grievance.this, "Grievance uploaded successfully!", Toast.LENGTH_SHORT).show();
                        Intent homeIntent = new Intent(Grievance.this, MainActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Toast.makeText(Grievance.this, "Submission Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String convertFileToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] bytes = getBytes(inputStream);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}

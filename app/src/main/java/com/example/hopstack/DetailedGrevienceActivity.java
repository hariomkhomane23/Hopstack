package com.example.hopstack;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayInputStream;

public class DetailedGrevienceActivity extends AppCompatActivity {

    private TextView txtUsername, txtEnno, txtComment;
    private ImageView imgFile;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_grevience);

        txtUsername = findViewById(R.id.txtUsername);
        txtEnno = findViewById(R.id.txtEnno);
        txtComment = findViewById(R.id.txtComment);
        imgFile = findViewById(R.id.imgFile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading grievance details...");
        progressDialog.setCancelable(false);

        String grievanceId = getIntent().getStringExtra("grievanceId");

        if (grievanceId != null) {
            progressDialog.show();
            fetchGrievanceDetails(grievanceId);
        } else {
            txtUsername.setText("Error: No grievance ID found.");
            txtEnno.setText("");
            txtComment.setText("");
            imgFile.setImageResource(R.drawable.baseline_error_outline_24);
        }
    }

    private void fetchGrievanceDetails(String grievanceId) {
        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("grievances").child(grievanceId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String enrollment = dataSnapshot.child("enrollment").getValue(String.class);
                    String comment = dataSnapshot.child("comment").getValue(String.class);
                    String fileBase64 = dataSnapshot.child("file").getValue(String.class);

                    txtUsername.setText("Username: " + (username != null ? username : "N/A"));
                    txtEnno.setText("Enrollment: " + (enrollment != null ? enrollment : "N/A"));
                    txtComment.setText("Comment: " + (comment != null ? comment : "No comment provided"));

                    if (fileBase64 != null && !fileBase64.equals("No file uploaded")) {
                        byte[] decodedBytes = Base64.decode(fileBase64, Base64.DEFAULT);
                        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imgFile.setImageBitmap(bitmap);
                    } else {
                        imgFile.setImageResource(R.drawable.baseline_error_outline_24);
                    }
                } else {
                    txtUsername.setText("Error: Grievance not found.");
                    txtEnno.setText("");
                    txtComment.setText("");
                    imgFile.setImageResource(R.drawable.baseline_error_outline_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                txtUsername.setText("Error: " + databaseError.getMessage());
                txtEnno.setText("");
                txtComment.setText("");
                imgFile.setImageResource(R.drawable.baseline_error_outline_24);
            }
        });
    }
}
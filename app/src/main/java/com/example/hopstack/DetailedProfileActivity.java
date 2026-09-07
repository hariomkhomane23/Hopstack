package com.example.hopstack;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailedProfileActivity extends AppCompatActivity {
    private static final String TAG = "DetailedProfileActivity";

    private ImageView profileImage;
    private TextView nameText, branchText, emailText, phoneText, roomNoText, dobText, bloodGroupText;
    private DatabaseReference databaseReference;
    private String emailKey;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_profile);

        // Initialize UI elements
        profileImage = findViewById(R.id.profileImage);
        nameText = findViewById(R.id.nameText);
        branchText = findViewById(R.id.branchText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        roomNoText = findViewById(R.id.roomNoText);
        dobText = findViewById(R.id.dobText);
        bloodGroupText = findViewById(R.id.bloodGroupText);

        // Initialize and show loading dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Get email from intent
        emailKey = getIntent().getStringExtra("email");

        if (emailKey == null || emailKey.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Error: No email received!", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Intent emailKey is null or empty.");
            finish();
            return;
        }

        Log.d(TAG, "Received email: " + emailKey);
        fetchProfileData(emailKey);
    }

    private void fetchProfileData(String email) {
        String encodedEmail = email.replace(".", "_"); // Encode email for Firebase
        Log.d(TAG, "Fetching profile for: " + encodedEmail);

        databaseReference = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/")
                .getReference("Profiles")
                .child(encodedEmail);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressDialog.dismiss(); // Dismiss loading dialog when data is fetched

                Log.d(TAG, "Fetched Data Snapshot: " + dataSnapshot.toString());

                if (!dataSnapshot.exists()) {
                    Toast.makeText(DetailedProfileActivity.this, "Error: Profile not found!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Profile data not found for email: " + email);
                    finish();
                    return;
                }

                String name = dataSnapshot.child("name").getValue(String.class);
                String branch = dataSnapshot.child("branch").getValue(String.class);
                String phone = dataSnapshot.child("phone").getValue(String.class);
                String roomNo = dataSnapshot.child("roomNo").getValue(String.class);
                String dob = dataSnapshot.child("dob").getValue(String.class);
                String bloodGroup = dataSnapshot.child("bloodGroup").getValue(String.class);
                String imageUrl = dataSnapshot.child("image").getValue(String.class);

                // Update UI with profile details
                nameText.setText(name != null ? name : "Not Available");
                branchText.setText(branch != null ? branch : "Not Available");
                phoneText.setText(phone != null ? phone : "Not Available");
                roomNoText.setText(roomNo != null ? roomNo : "Not Available");
                dobText.setText(dob != null ? dob : "Not Available");
                bloodGroupText.setText(bloodGroup != null ? bloodGroup : "Not Available");
                emailText.setText(email);

                // Handle missing profileImage field
                if (!dataSnapshot.hasChild("image")) {
                    Log.e(TAG, "Profile Image field is missing in Firebase");
                    profileImage.setImageResource(R.drawable.default_profile);
                    return;
                }

                // Handle Base64 Image Decoding
                if (imageUrl == null || imageUrl.isEmpty()) {
                    Log.e(TAG, "Image URL is null or empty");
                    profileImage.setImageResource(R.drawable.default_profile);
                    return;
                }

                Log.d(TAG, "Base64 Image String (first 50 chars): " + imageUrl.substring(0, Math.min(imageUrl.length(), 50)));

                try {
                    // Remove "data:image/png;base64," if present
                    if (imageUrl.startsWith("data:image")) {
                        imageUrl = imageUrl.substring(imageUrl.indexOf(",") + 1);
                    }

                    byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    if (decodedBitmap != null) {
                        profileImage.setImageBitmap(decodedBitmap);
                    } else {
                        Log.e(TAG, "Decoded Bitmap is null");
                        profileImage.setImageResource(R.drawable.default_profile);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Image decoding failed: " + e.getMessage());
                    profileImage.setImageResource(R.drawable.default_profile);
                }

                Log.d(TAG, "Profile loaded successfully!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss(); // Dismiss loading dialog on error
                Toast.makeText(DetailedProfileActivity.this, "Error: Failed to load profile", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Firebase Database Error: " + databaseError.getMessage());
            }
        });
    }
}

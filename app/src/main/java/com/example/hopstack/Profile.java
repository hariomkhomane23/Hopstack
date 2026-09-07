package com.example.hopstack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;

public class Profile extends AppCompatActivity {

    private EditText editName, editEmail, editPhone, editRoomNo, editBranch, editDob, editBloodGroup;
    private ImageView imageProfile;
    private Uri imageUri;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        String userEmailKey = user.getEmail().replace(".", "_");
        databaseReference = FirebaseDatabase.getInstance().getReference("Profiles").child(userEmailKey);

        imageProfile = findViewById(R.id.imageProfile);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editRoomNo = findViewById(R.id.editRoomNo);
        editBranch = findViewById(R.id.editBranch);
        editDob = findViewById(R.id.editDob);
        editBloodGroup = findViewById(R.id.editBloodGroup);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnAddPhoto = findViewById(R.id.btnAddPhoto);

        btnAddPhoto.setOnClickListener(v -> selectImage());
        btnSave.setOnClickListener(v -> saveProfileData());

        // Show loading bar while fetching data
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Fetch data from Firebase
        fetchProfileData();
    }

    private void fetchProfileData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss(); // Hide loading bar

                if (snapshot.exists()) {
                    UserProfile userProfile = snapshot.getValue(UserProfile.class);
                    if (userProfile != null) {
                        editName.setText(userProfile.getName());
                        editEmail.setText(userProfile.getEmail());
                        editPhone.setText(userProfile.getPhone());
                        editRoomNo.setText(userProfile.getRoomNo());
                        editBranch.setText(userProfile.getBranch());
                        editDob.setText(userProfile.getDob());
                        editBloodGroup.setText(userProfile.getBloodGroup());

                        // Decode and set the profile image if available
                        if (!userProfile.getImage().isEmpty()) {
                            byte[] decodedString = Base64.decode(userProfile.getImage(), Base64.DEFAULT);
                            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageProfile.setImageBitmap(bitmap);
                        }
                    }
                } else {
                    Toast.makeText(Profile.this, "No profile data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss(); // Hide loading bar
                Toast.makeText(Profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageProfile.setImageURI(imageUri);
        }
    }

    private void saveProfileData() {
        progressDialog = ProgressDialog.show(this, "Saving Profile", "Please wait...", true);

        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String roomNo = editRoomNo.getText().toString().trim();
        String branch = editBranch.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        String bloodGroup = editBloodGroup.getText().toString().trim();

        String imageString = encodeImage();

        UserProfile userProfile = new UserProfile(name, email, phone, roomNo, branch, dob, bloodGroup, imageString);

        databaseReference.setValue(userProfile).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            Toast.makeText(Profile.this, "Profile Saved", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Profile.this, DisplayProfile.class));
            finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(Profile.this, "Failed to save profile", Toast.LENGTH_SHORT).show();
        });
    }

    private String encodeImage() {
        if (imageProfile.getDrawable() == null) {
            return "";
        }
        Bitmap bitmap = ((BitmapDrawable) imageProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}

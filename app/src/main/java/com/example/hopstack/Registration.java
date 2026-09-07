package com.example.hopstack;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {
    private EditText Regname, Regemail, Regphone, Regpass;
    private Button regButton;
    private FirebaseAuth auth;
    private DatabaseReference usersRef; // ✅ Global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://hopstack-59120-default-rtdb.firebaseio.com/");
        usersRef = database.getReference("registration_users"); // ✅ Now correctly initialized

        // Initialize UI Elements
        Regname = findViewById(R.id.regname);
        Regemail = findViewById(R.id.regemail);
        Regphone = findViewById(R.id.regphone);
        Regpass = findViewById(R.id.regpass);
        regButton = findViewById(R.id.regbut);

        // Register Button Click333
        regButton.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String username = Regname.getText().toString().trim();
        String email = Regemail.getText().toString().trim();
        String phone = Regphone.getText().toString().trim();
        String password = Regpass.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Regname.setError("name Required");
            Regname.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Regemail.setError("Email Required");
            Regemail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Regphone.setError("Phone Required");
            Regphone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Regpass.setError("Password Required");
            Regpass.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 digits ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserData(user.getUid(), username, email, phone);
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e("FirebaseAuth", "Registration Failed: " + errorMessage);
                        Toast.makeText(Registration.this, "Registration Failed! " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserData(String userId, String username, String email, String phone) {
        // Store user data in Firebase Database
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("role", "user"); // Default role

        usersRef.child(userId).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FirebaseDatabase", "User registered successfully: " + userId);
                        Toast.makeText(Registration.this, "Registration Successful !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registration.this, Login.class));
                        finish();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e("FirebaseDatabase", "User data save failed: " + errorMessage);
                        Toast.makeText(Registration.this, "Database Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

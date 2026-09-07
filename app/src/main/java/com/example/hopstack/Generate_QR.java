package com.example.hopstack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;

public class Generate_QR extends AppCompatActivity {

    private ImageView qrCodeImage;
    private Button generateQRButton;
    private DatabaseReference qrRef;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentLocation = "Fetching...";
    private String todayDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        qrCodeImage = findViewById(R.id.qrCodeImage);
        generateQRButton = findViewById(R.id.generateQRButton);
        // Enable Back Button in Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 🔹 Initialize Firebase Reference
        qrRef = FirebaseDatabase.getInstance().getReference("QR_Codes");

        // 🔹 Initialize Location Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        todayDate = getCurrentDate();

        checkExistingQRCode();

        generateQRButton.setOnClickListener(view -> fetchLocationAndGenerateQR());
    }

    // ✅ *Check if QR Code already exists for today*
    private void checkExistingQRCode() {
        qrRef.child(todayDate).child("qrImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loadQRCodeFromFirebase(snapshot.getValue(String.class));
                } else {
                    Toast.makeText(Generate_QR.this, "No QR Code found for today. Click to generate.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Generate_QR.this, "Error fetching QR Code", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ *Load QR Code from Firebase*
    private void loadQRCodeFromFirebase(String encodedImage) {
        if (encodedImage == null || encodedImage.isEmpty()) {
            Toast.makeText(this, "No QR Code found for today!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            qrCodeImage.setImageBitmap(bitmap);
            Toast.makeText(this, "QR Code Loaded Successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ *Fetch location before generating QR*
    private void fetchLocationAndGenerateQR() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                checkAndGenerateQRCode();
            } else {
                requestNewLocation();
            }
        });
    }

    // ✅ *Request real-time location updates if last location is null*
    @SuppressLint("MissingPermission")
    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                fusedLocationClient.removeLocationUpdates(this);
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    currentLocation = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                    checkAndGenerateQRCode();
                }
            }
        }, null);
    }

    // ✅ *Ensure only one QR per day*
    private void checkAndGenerateQRCode() {
        qrRef.child(todayDate).child("qrImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Generate_QR.this, "QR Code already exists for today!", Toast.LENGTH_SHORT).show();
                } else {
                    generateQRCode();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Generate_QR.this, "Error checking QR existence", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ *Generate QR Code*
    private void generateQRCode() {
        try {
            String currentTime = getCurrentTime();
            String qrData = "Date: " + todayDate + "\nTime: " + currentTime + "\nLocation: " + currentLocation;

            MultiFormatWriter writer = new MultiFormatWriter();
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512, hints);
            Bitmap bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565);

            for (int x = 0; x < 512; x++) {
                for (int y = 0; y < 512; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            qrCodeImage.setImageBitmap(bitmap);
            saveQRCodeToFirebase(bitmap, currentTime);
            Toast.makeText(this, "QR Code Generated Successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ *Store QR Code & Location in Firebase*
    private void saveQRCodeToFirebase(Bitmap bitmap, String generatedTime) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        String encodedImage = Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT);

        DatabaseReference qrEntryRef = qrRef.child(todayDate);
        qrEntryRef.child("qrImage").setValue(encodedImage);
        qrEntryRef.child("generatedTime").setValue(generatedTime);
        qrEntryRef.child("location").setValue(currentLocation);
        qrEntryRef.child("generatedDate").setValue(todayDate) // ✅ Store the generated date

                .addOnSuccessListener(aVoid -> Toast.makeText(this, "QR Code and date saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save QR Code", Toast.LENGTH_SHORT).show());
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // Handle Back Button Click in Action Bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openAdminFragment(); // Navigate to Admin Fragment
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle Hardware Back Button Press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        openAdminFragment(); // Navigate to Admin Fragment
    }

    // Open Admin Fragment inside Drawer Activity
    private void openAdminFragment() {
        Intent intent = new Intent(this, MainActivity.class); // Replace with your Drawer Activity
        intent.putExtra("openAdmin", true); // Flag to indicate Admin Fragment should open
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Close current activity
    }
}
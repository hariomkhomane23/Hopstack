package com.example.hopstack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class StandStrong extends AppCompatActivity {

    private ImageView antiRaggingImage;
    private Button reportRaggingButton, hotlineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stand_strong);

        antiRaggingImage = findViewById(R.id.antiRaggingImage);
        reportRaggingButton = findViewById(R.id.reportRaggingButton);
        hotlineButton = findViewById(R.id.hotlineButton);

        // Handle Report Button Click
        reportRaggingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportFormActivity.class);
            startActivity(intent);
        });

        // Handle Hotline Button Click
        hotlineButton.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:1800-180-5522")); // Anti-Ragging Helpline
            startActivity(callIntent);
        });
    }
}
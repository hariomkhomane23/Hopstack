package com.example.hopstack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;

public class hopinfo extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler();
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hopinfo);

        viewPager = findViewById(R.id.viewPager);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Image List
        List<Integer> imageList = Arrays.asList(
                R.drawable.info,
                R.drawable.image11,
                R.drawable.image22,
                R.drawable.image33,
                R.drawable.image44
        );

        // Set Adapter
        SliderAdapter adapter = new SliderAdapter(this, imageList);
        viewPager.setAdapter(adapter);

        // Start Auto Slide
        startAutoSlide(imageList.size());
    }

    // 🔹 Back Button Click -> Open Admin Fragment
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            openAdminFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 🔹 Hardware Back Button Click -> Open Admin Fragment
    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Ensure normal back functionality
        openAdminFragment();
    }

    // 🔹 Start Auto Slide
    private void startAutoSlide(int size) {
        sliderHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPage >= size) {
                    currentPage = 0; // Restart slider from first image
                }
                viewPager.setCurrentItem(currentPage++, true);
                sliderHandler.postDelayed(this, 3000); // Change every 3 seconds
            }
        }, 3000);
    }

    // 🔹 Open Admin Fragment Inside DrawerLayout
    private void openAdminFragment() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openAdmin", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacksAndMessages(null); // Stop auto-slide
    }
}

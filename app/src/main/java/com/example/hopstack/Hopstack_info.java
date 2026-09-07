package com.example.hopstack;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.Arrays;
import java.util.List;

public class Hopstack_info extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler();
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hopstack_info);  // Set your activity layout

        viewPager = findViewById(R.id.viewPager);

        // Add images to the list (Replace with your actual drawable resources)
        List<Integer> imageList = Arrays.asList(
                R.drawable.info,
                R.drawable.image11,
                R.drawable.image22,
                R.drawable.image33,
                R.drawable.image44
        );

        // Set the adapter
        SliderAdapter adapter = new SliderAdapter(this, imageList);
        viewPager.setAdapter(adapter);

        // Start auto-slide
        startAutoSlide(imageList.size());
    }

    private void startAutoSlide(int size) {
        sliderHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentPage == size) {
                    currentPage = 0; // Restart slider from first image
                }
                viewPager.setCurrentItem(currentPage++, true);
                sliderHandler.postDelayed(this, 3000); // Change every 3 seconds
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacksAndMessages(null); // Stop auto-slide when activity is destroyed
    }
}

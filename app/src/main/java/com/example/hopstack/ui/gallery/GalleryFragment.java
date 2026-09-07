package com.example.hopstack.ui.gallery;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hopstack.R;
import com.example.hopstack.SliderAdapter;
import com.example.hopstack.databinding.FragmentGalleryBinding;

import java.util.Arrays;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler();
    private int currentPage = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        viewPager = binding.viewPagerGallery;

        // Add images for the gallery slider
        List<Integer> imageList = Arrays.asList(
                R.drawable.mam,
                R.drawable.hariom,
                R.drawable.tanishka,
                R.drawable.krishna,
                R.drawable.pranav
        );

        // Set up the adapter for ViewPager2
        SliderAdapter adapter = new SliderAdapter(requireContext(), imageList);
        viewPager.setAdapter(adapter);

        // Start auto-slide
        startAutoSlide(imageList.size());

        return root;
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
    public void onDestroyView() {
        super.onDestroyView();
        sliderHandler.removeCallbacksAndMessages(null); // Stop auto-slide when fragment is destroyed
        binding = null;
    }
}

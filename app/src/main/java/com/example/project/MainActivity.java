package com.example.project;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.project.adapter.BannerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPagerBanner;
    private LinearLayout layoutIndicators;
    private BannerAdapter bannerAdapter;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    // Banner images array
    private int[] bannerImages = {
            R.drawable.tranh1,
            R.drawable.tranh2,
            R.drawable.tranh3
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupBanner();
        setupAutoSlide();
    }

    private void initViews() {
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        layoutIndicators = findViewById(R.id.layoutIndicators);
    }

    private void setupBanner() {
        bannerAdapter = new BannerAdapter(bannerImages);
        viewPagerBanner.setAdapter(bannerAdapter);

        setupIndicators();
        setCurrentIndicator(0);

        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });
    }

    private void setupIndicators() {
        ImageView[] indicators = new ImageView[bannerImages.length];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_inactive
                ));
            }
        }
    }

    private void setupAutoSlide() {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerBanner.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerImages.length;
                viewPagerBanner.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // 3 seconds delay
            }
        };

        // Start auto-slide after 3 seconds
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

}
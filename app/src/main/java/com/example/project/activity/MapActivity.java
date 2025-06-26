package com.example.project.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.google.android.material.button.MaterialButton;

public class MapActivity extends AppCompatActivity {

    // FPT University Ho Chi Minh City information
    private static final String FPT_PHONE = "02873003300";
    private static final double FPT_LATITUDE = 10.8411276;
    private static final double FPT_LONGITUDE = 106.8093721;

    // UI Components
    private ImageView btnBack;
    private WebView webViewMap;
    private MaterialButton btnDirections, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViews();
        setupClickListeners();
        setupWebView();
        loadMap();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        webViewMap = findViewById(R.id.webViewMap);
        btnDirections = findViewById(R.id.btnDirections);
        btnCall = findViewById(R.id.btnCall);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnDirections.setOnClickListener(v -> openDirections());

        btnCall.setOnClickListener(v -> makePhoneCall());
    }

    private void setupWebView() {
        WebSettings webSettings = webViewMap.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        // Handle page loading
        webViewMap.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false; // Let WebView handle the URL
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Page loaded successfully
            }
        });
    }

    private void loadMap() {
        // Create Google Maps embed URL
        String mapUrl = String.format(
                "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3918.4544374621143!2d%f!3d%f!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x317527587e9ad5bf:0xafa66f9c8be3c91!2sFPT%%20University%%20HCMC!5e0!3m2!1sen!2s!4v1234567890123",
                FPT_LONGITUDE, FPT_LATITUDE
        );

        // Alternative: Load using Google Maps direct link (simpler)
        String simpleMapUrl = String.format(
                "https://maps.google.com/maps?q=%f,%f&z=15&output=embed",
                FPT_LATITUDE, FPT_LONGITUDE
        );

        // Create HTML content with embedded map
        String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { margin: 0; padding: 0; }" +
                "iframe { width: 100%; height: 100vh; border: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<iframe src='" + simpleMapUrl + "' frameborder='0'></iframe>" +
                "</body>" +
                "</html>";

        // Load the HTML content
        webViewMap.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    private void openDirections() {
        try {
            // Try to open Google Maps app first
            String uri = String.format("google.navigation:q=%f,%f&mode=d", FPT_LATITUDE, FPT_LONGITUDE);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback to web browser if Google Maps app is not installed
                String webUri = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f",
                        FPT_LATITUDE, FPT_LONGITUDE);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                startActivity(webIntent);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở chỉ đường", Toast.LENGTH_SHORT).show();
        }
    }

    private void makePhoneCall() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + FPT_PHONE));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể thực hiện cuộc gọi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (webViewMap.canGoBack()) {
            webViewMap.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webViewMap != null) {
            webViewMap.destroy();
        }
        super.onDestroy();
    }
}

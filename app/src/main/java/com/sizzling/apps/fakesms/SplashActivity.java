package com.sizzling.apps.fakesms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import static com.sizzling.apps.fakesms.FakesmsActivity.testingMode;

public class SplashActivity extends AppCompatActivity {

    InterstitialAd mInterstitialAd;
    boolean adShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mInterstitialAd = new InterstitialAd(this);
        if (testingMode) {
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Test Ad Id
        } else {
            mInterstitialAd.setAdUnitId("ca-app-pub-6557142062270167/9949615949");
        }
        requestNewInterstitial();
        AdView adView = findViewById(R.id.bannerAdMain);
        AdRequest adRequest;
        if (testingMode) {
            adRequest = new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
        } else {
            adRequest = new AdRequest.Builder().build();
        }

        adView.loadAd(adRequest);
        NativeExpressAdView nativeAdView = findViewById(R.id.nativeAdViewMain);
        AdRequest request;
        if (testingMode) {
            request = new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
        } else {
            request = new AdRequest.Builder().build();
        }
        nativeAdView.loadAd(request);
    }

    public void requestNewInterstitial() {
        AdRequest adRequest = null;
        if (testingMode) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice("55757F6B6D6116FAC42122EC92E5A58C")
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .build();
        }
        mInterstitialAd.loadAd(adRequest);
    }

    public void enterApp(View view) {
        if (!adShown) {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return;
            } else
                requestNewInterstitial();
        }
        adShown = false;
        Intent i = new Intent(SplashActivity.this, FakesmsActivity.class);
        startActivity(i);

    }
}

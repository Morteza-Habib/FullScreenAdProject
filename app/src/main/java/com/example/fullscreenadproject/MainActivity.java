package com.example.fullscreenadproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    private int counter;

    private final String TAG = "myApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate called");

        Button mButton = findViewById(R.id.id_button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Counter = " + counter);

                if (mInterstitialAd != null && counter % 5 == 0) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d(TAG, "The interstitial ad wasn't ready yet.");
                    openSecondActivity();
                }

                counter++;
            }
        });

        if (savedInstanceState != null) {
            counter = savedInstanceState.getInt("mCounter", 0);
        }
    }

    private void loadFullScreenAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.interstitial_ad_unit), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");


                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                Log.d(TAG, "The ad was dismissed.");

                                openSecondActivity();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                Log.d(TAG, "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                                Log.d(TAG, "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;

                        new CountDownTimer(10000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.d(TAG, "Sec:" + millisUntilFinished / 1000);
                            }

                            @Override
                            public void onFinish() {
                                Log.d(TAG,"Re-Loading ad");
                                loadFullScreenAd();
                            }
                        }.start();

                    }
                });
    }


    private void openSecondActivity() {
        startActivity(new Intent(MainActivity.this, SecondActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResumed Called");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {


                loadFullScreenAd();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mCounter", counter);
    }
}
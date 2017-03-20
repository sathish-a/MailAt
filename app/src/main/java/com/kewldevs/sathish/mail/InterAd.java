package com.kewldevs.sathish.mail;

import android.app.Activity;
import android.content.Context;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by sathish on 5/19/16.
 */
public class InterAd {
    InterstitialAd mInterstitialAd;
    Context mContext;
    Activity mActivity;
    String TAG = "ADs";
    public InterAd(Context context,InterstitialAd interstitialAd) {
        mContext = context;
        mInterstitialAd = interstitialAd;
        mActivity = (Activity) mContext;
    }

    public void initAd() {
        MobileAds.initialize(mContext, "ca-app-pub-3940256099942544~3347511713");

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(mContext);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(mContext.getString(R.string.ad_unit_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mActivity.finish();
            }
        });

                if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
                    final AdRequest adRequest = new AdRequest.Builder().build();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mInterstitialAd.loadAd(adRequest);
                        }
                    });
                }



    }

    public void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            mActivity.finish();
        }
    }

}

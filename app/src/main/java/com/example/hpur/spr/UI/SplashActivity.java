package com.example.hpur.spr.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.hpur.spr.R;
import com.example.hpur.spr.Storage.SharedPreferencesStorage;

public class SplashActivity extends AppCompatActivity {
    private static final String KEY = "first_installation", IS_FIRST_INSTALLATION = "false";

    public static int SPLASH_OUT=2000;
    private ImageView loading;
    public RotateAnimation rotate;
    private SharedPreferencesStorage mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mSharedPreferences = new SharedPreferencesStorage(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loading = (ImageView)findViewById(R.id.imageView);
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(SPLASH_OUT/2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSharedPreferences.readData(KEY, "true").equals("true")) {
                    mSharedPreferences.saveData(IS_FIRST_INSTALLATION, KEY);
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }, SPLASH_OUT);
        loading.startAnimation(rotate);
    }
}

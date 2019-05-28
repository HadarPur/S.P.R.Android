package com.example.hpur.spr.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.R;

public class SplashActivity extends AppCompatActivity {

    private static final String KEY = "connect", IS_FIRST_INSTALLATION = "false";
    public static int SPLASH_OUT=2000;
    private ImageView mLoading;
    private UserModel mUserModel;

    private RotateAnimation mRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        this.mUserModel = new UserModel();
        this.mRotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
                0.5f,  Animation.RELATIVE_TO_SELF, 0.5f);
        this.mRotate.setDuration(SPLASH_OUT/2);

        findViews();
    }

    // find all views from xml by id
    private void findViews() {
        this.mLoading = findViewById(R.id.imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //If the user is new to the application or did not agree to kept signed in,
                // then the user will transfer to the log in screen.
                if (mUserModel.getUserLocalDataByKey(getApplicationContext(), "SignedIn").equals("") || mUserModel.getUserLocalDataByKey(getApplicationContext(), "SignedIn").equals("false")) {
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

        this.mLoading.startAnimation(mRotate);
    }

}

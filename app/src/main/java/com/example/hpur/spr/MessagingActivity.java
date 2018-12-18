package com.example.hpur.spr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MessagingActivity extends AppCompatActivity {

    private ImageButton mPhone;
    private ImageButton mVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        findViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void findViews() {
        this.mPhone = findViewById(R.id.phone);
        this.mVideo = findViewById(R.id.video);

        this.mPhone.setVisibility(View.VISIBLE);
        this.mVideo.setVisibility(View.VISIBLE);

        this.mPhone.setClickable(true);
        this.mVideo.setClickable(true);
    }
}

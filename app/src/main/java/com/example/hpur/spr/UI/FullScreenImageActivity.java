package com.example.hpur.spr.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.example.hpur.spr.R;
import com.example.hpur.spr.UI.Views.TouchImageView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullScreenImageActivity extends AppCompatActivity {

    private TouchImageView mImageView;
    private SpinKitView mProgressBar = null;
    private ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        findViews();
        setValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void findViews(){
        this.mProgressBar = findViewById(R.id.spin_kit);
        this.mImageView = findViewById(R.id.imageView);
        this.mBack = findViewById(R.id.backbtn);
        this.mBack.setVisibility(View.VISIBLE);

        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setValues(){
        String urlPhotoClick;

        urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
        Log.i("TAG","image "+urlPhotoClick);

        mProgressBar.setVisibility(View.VISIBLE);
        Picasso.get().load(urlPhotoClick).into(mImageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("bindChatBubble", "onSuccess");
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("bindChatBubble", "onError: "+ e);
                e.printStackTrace();
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}

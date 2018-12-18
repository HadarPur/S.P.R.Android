package com.example.hpur.spr;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AboutUsActivity extends AppCompatActivity {
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    protected void onStart() {
        super.onStart();
        findViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //find view by id and set text
    public void findViews() {
        tv=(TextView) findViewById(R.id.aboutus);
        tv.setText("S.P.R: Saving population at risk\n");
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setTextColor(Color.BLACK);
    }
}

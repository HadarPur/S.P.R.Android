package com.example.hpur.spr.UI;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.hpur.spr.R;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private Spinner mSpinner;
    private SupportMapFragment mapFragment;
    private int position;
    private Button mSearchBtn;
    private Bundle ex;
    private int count_wait=0;
    private String streetName;
    private boolean isFirst=true, isLoading;
    private ArrayList<String> names;
    private RelativeLayout mLoadingBack;
    private ArrayAdapter adapter;
    private boolean firstAsk=false;
    private double latitude, longitude;
    private ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        findViews ();
        setupOnClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //find views from xml with listeners
    public void findViews () {
        this.mLoadingBack = findViewById(R.id.load);
        this.mBack = findViewById(R.id.backbtn);
        this.mSpinner = findViewById(R.id.spinner1);
        this.mSearchBtn = findViewById(R.id.search);

        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }
}

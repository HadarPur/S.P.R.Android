package com.example.hpur.spr.UI;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.example.hpur.spr.R;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private Spinner spinner;
    private SupportMapFragment mapFragment;
    private int position;
    private Button bSearch;
    private Bundle ex;
    private int count_wait=0;
    private String streetName;
    private boolean isFirst=true, isLoading;
    private ArrayList<String> names;
    private RelativeLayout loadingBack;
    private ArrayAdapter adapter;
    private boolean firstAsk=false;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        findViews ();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //find views from xml with listeners
    public void findViews () {
        loadingBack = findViewById(R.id.load);
        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        loadingBack.setBackgroundColor(Color.argb(200, 206,117,126));
        spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        bSearch = (Button) findViewById(R.id.search);

    }

}

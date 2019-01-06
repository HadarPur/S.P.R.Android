package com.example.hpur.spr.UI;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import com.example.hpur.spr.Logic.GPSTracker;
import com.example.hpur.spr.Logic.Map;
import com.example.hpur.spr.Logic.Shelter;
import com.example.hpur.spr.Logic.ShelterInstance;
import com.example.hpur.spr.R;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity {
    private static final String TAG = NavigationActivity.class.getSimpleName();
    private boolean mFirstAsk =false, mIsLoading;
    private double mLatitude, mLongitude;
    private ArrayList<Shelter>[] mShelterData;

    private ShelterInstance mShelterInfo;

    private Button mSearchBtn;
    private ImageButton mBack;
    private FloatingActionButton mFab;

    private Map mMap;
    private SupportMapFragment mMapFragment;

    private RelativeLayout mLoadingBack;
    private GPSTracker mGpsTracker;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        this.mGpsTracker = new GPSTracker(this, mFirstAsk);
        if(this.mGpsTracker.getGPSEnable()&& mGpsTracker.getPosition()!=null){
            this.mLatitude = this.mGpsTracker.getPosition().getLatitude();
            this.mLongitude = this.mGpsTracker.getPosition().getLongitude();
            //if location wasn't enable and now it's enable
            this.mGpsTracker.initLocation();
        }
        else if (!this.mGpsTracker.getGPSEnable()){
            this.mLatitude = 0;
            this.mLongitude = 0;
        }

        Log.d(TAG, "mLatitude: " + this.mLatitude + " mLongitude: " + this.mLongitude);

        findViews ();
        setupOnClick();

        this.mShelterInfo = ShelterInstance.getInstance();
        this.mShelterData = this.mShelterInfo.getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showCurrentPosOnMap();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    //find views from xml with listeners
    private void findViews () {
        this.mBack = findViewById(R.id.backbtn);
        this.mSpinner = findViewById(R.id.spinner1);
        this.mSearchBtn = findViewById(R.id.search);
        this.mFab = findViewById(R.id.fab);
        this.mLoadingBack = findViewById(R.id.load);

        this.mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        this.mSpinner.setAdapter(adapter);
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

        this.mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoading == false) {
                    String spinnerPosText = mSpinner.getSelectedItem().toString();
                    int spinnerPos = mSpinner.getSelectedItemPosition();

                    Log.d(TAG, "spinnerPosText: " + spinnerPosText + " position : " + spinnerPos);

                    showSheltersOnMap(spinnerPos);
                }
            }
        });

        this.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsLoading == false) {
                    loadingPage();
                    showClosestSheltersOnMap();
                }
            }
        });
    }

    //set shelter location markers on map
    private void showSheltersOnMap(int position) {
        this.mMap.showShelters(mShelterData[position], this);
    }

    //set the closest shelters on the map
    private void showClosestSheltersOnMap() {
        this.mMap.showClosestShelters(mShelterData, this);
    }

    //set user current location marker on the map
    private void showCurrentPosOnMap() {
        this.mMap = new Map(mMapFragment, mLatitude, mLongitude, this.getApplicationContext());
    }

    //start loading view for the callback
    private void loadingPage() {
        this.mLoadingBack.setVisibility(View.VISIBLE);
        this.mIsLoading =true;
    }

    //finish loading view for the callback
    public void doneLoadingPage() {
        this.mLoadingBack.setVisibility(View.GONE);
        this.mIsLoading =false;
    }

}

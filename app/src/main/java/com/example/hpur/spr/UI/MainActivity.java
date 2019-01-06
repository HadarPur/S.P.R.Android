package com.example.hpur.spr.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.hpur.spr.Logic.GPSTracker;
import com.example.hpur.spr.Logic.Queries.DateCallback;
import com.example.hpur.spr.Logic.Shelter;
import com.example.hpur.spr.Logic.ShelterInstance;
import com.example.hpur.spr.R;
import com.example.hpur.spr.Storage.FireBaseModifiedDate;
import com.example.hpur.spr.Storage.SharedPreferencesStorage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DateCallback{
    private static final String TAG = MainActivity.class.getSimpleName();
    private boolean mFirstAsk = true, mIsLoading, mIsShow;
    private String mDate;

    private ShelterInstance mShelterInfo;

    private Button mStartChat;
    private Button mFindShelter;
    private Button mAboutUs;
    private Button mSignOut;
    private Button mAlertOkBtn;

    private LinearLayout mAlertView;
    private RelativeLayout mLoadingBack;

    private TextView mAlertTittle;
    private TextView mAlertText;

    private GPSTracker mGpsTracker;
    private SharedPreferencesStorage mSharedPreferences;
    private FireBaseModifiedDate mModifiedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mSharedPreferences = new SharedPreferencesStorage(getApplicationContext());
        this.mModifiedDate = new FireBaseModifiedDate();

        this.mDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Log.d(TAG, "Date: "+ mDate);

        findViews();
        setupOnClick();

        mAlertTittle.setText("Pay attention");
        mAlertText.setText("You must permit location and network connection for this app");
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        mAlertView.startAnimation(aniFade);
        mAlertView.setVisibility(View.VISIBLE);
        this.mIsShow = true;
        disableButtons();

        this.mGpsTracker = new GPSTracker(this, mFirstAsk);
        if(!this.mGpsTracker.getGPSEnable()){
            showSettingsAlert();
        }
        if(!isNetworkAvailable(this)) {
            showConnectionInternetFailed();
        }
        else {
            singletonShelters();

//            mModifiedDate.ReadData(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // find all views from xml by id
    private void findViews() {
        this.mStartChat = findViewById(R.id.chat);
        this.mFindShelter = findViewById(R.id.shelter);
        this.mAboutUs = findViewById(R.id.about_us);
        this.mSignOut = findViewById(R.id.signout);

        this.mAlertView = findViewById(R.id.alertview);
        this.mAlertTittle = findViewById(R.id.alerttittle);
        this.mAlertText = findViewById(R.id.msg);
        this.mAlertOkBtn = findViewById(R.id.alert_def_btn);

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));
    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoading == false) {
                    Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        this.mFindShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoading == false) {
                    Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        this.mAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoading == false) {
                    Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });

        this.mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoading == false) {
                    mSharedPreferences.saveData("false", "SignedIn");

                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            }
        });

        this.mAlertOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mAlertView.startAnimation(aniFade);
                mAlertView.setVisibility(View.INVISIBLE);
                mIsShow = false;
                enableButtons();
            }
        });
    }

    // called just when the shelters list were updated
    private void singletonShelters() {
        this.mShelterInfo = ShelterInstance.getInstance();
        String[] cities = getResources().getStringArray(R.array.cities_array);
        Log.d(TAG, "cities length: " + cities.length);
        loadingPage();
        this.mShelterInfo.readData(new ShelterInstance.Callback() {
            @Override
            public void onCallback(ArrayList<Shelter>[] cloudData) {
                mShelterInfo.setData(cloudData);
                doneLoadingPage();
            }
        }, cities);
    }

    //alert network not available
    private void showConnectionInternetFailed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Network Connection Failed");
        alertDialog.setMessage("Network is not enabled." +
                "\n"+
                "If you want to use this app you need a connection to the network");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.show();
    }

    //alert for GPS connection
    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                finish();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.show();
    }

    //check network connection
    private static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        }
        else {
            return false;
        }
    }

    private void disableButtons(){
        this.mSignOut.setClickable(false);
        this.mStartChat.setClickable(false);
        this.mFindShelter.setClickable(false);
        this.mAboutUs.setClickable(false);
    }

    private void enableButtons(){
        this.mSignOut.setClickable(true);
        this.mStartChat.setClickable(true);
        this.mFindShelter.setClickable(true);
        this.mAboutUs.setClickable(true);
    }

    //start loading view for the callback
    private void loadingPage() {
        disableButtons();
        this.mLoadingBack.setVisibility(View.VISIBLE);
        this.mIsLoading =true;
    }

    //finish loading view for the callback
    private void doneLoadingPage() {
        if (mIsShow == false)
            enableButtons();
        this.mLoadingBack.setVisibility(View.GONE);
        this.mIsLoading =false;
    }

    @Override
    public void getModifiedDate(String date) {

//        try {
//            String lastDate = mSharedPreferences.readData("IsModified");
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//            if (lastDate.equals("") || sdf.parse(lastDate).getTime() < sdf.parse(date).getTime()) {
//                mSharedPreferences.saveData(date, "IsModified");
//                singletonShelters();
//            }
//            else {
//                Toast.makeText(MainActivity.this, "calling from sp ", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//        catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
}

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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.hpur.spr.Logic.GPSTracker;
import com.example.hpur.spr.Logic.Models.AgentModel;
import com.example.hpur.spr.Logic.Models.ShelterModel;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.AvailableAgentsCallback;
import com.example.hpur.spr.Logic.Queries.KnnCallback;
import com.example.hpur.spr.Logic.ShelterInstance;
import com.example.hpur.spr.Logic.Types.ActivityType;
import com.example.hpur.spr.R;
import com.example.hpur.spr.UI.Utils.KnnServiceUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, KnnCallback, AvailableAgentsCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean mFirstAsk = true, mIsLoading, mIsShow;

    private ShelterInstance mShelterInfo;

    private Button mStartChat;
    private Button mFindShelter;
    private Button mAboutUs;
    private Button mSignOut;
    private Button mAlertOkBtn;

    private LinearLayout mAlertView;
    private RelativeLayout mLoadingBack;
    private FirebaseFirestore mFirebaseFirestore;

    private TextView mAlertTittle;
    private TextView mAlertText;

    private GPSTracker mGpsTracker;

    private ActionBarDrawerToggle mToggle;
    private DrawerLayout mDrawerLayout;

    private UserModel mUserModel;
    private KnnServiceUtil mKnnServiceUtil = null;

    private AgentModel availableAgents;
    private FirebaseAuth mAuth;
    private String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mKnnServiceUtil = new KnnServiceUtil(this);
        this.mUserModel = new UserModel();
        this.availableAgents = new AgentModel();
        this.mFirebaseFirestore = FirebaseFirestore.getInstance();

        this.mAuth = FirebaseAuth.getInstance();
        this.mUID = this.mAuth.getCurrentUser().getUid();
        
        findViews();
        initNavigationDrawer();
        setupOnClick();

        this.mGpsTracker = new GPSTracker(this, mFirstAsk);

        if(!this.mGpsTracker.getGPSEnable()){
            showSettingsAlert();
        }

        if(!isNetworkAvailable(this)) {
            showConnectionInternetFailed();
        }
        else {
            singletonShelters();
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

        this.mAlertTittle.setText("Pay attention");
        this.mAlertText.setText("You must permit location and network connection for this app");
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        this.mAlertView.startAnimation(aniFade);
        this.mAlertView.setVisibility(View.VISIBLE);
        this.mIsShow = true;
        disableButtons();

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));

        this.mDrawerLayout = findViewById(R.id.activity_main);
    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsLoading == false) {
                    loadingPage();
                    new AgentModel().getAvailableAgentsUID(MainActivity.this, MainActivity.this);
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

                    mUserModel.setUserLocalDataByKeyAndValue(getApplicationContext(),"false", "SignedIn");
                    mUserModel.setUserLocalDataByKeyAndValue(getApplicationContext(),"false", "Available");

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
                mAlertView.setVisibility(View.GONE);
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
            public void onCallback(ArrayList<ShelterModel>[] cloudData) {
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

    private void initNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        this.mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);

        this.mDrawerLayout.addDrawerListener(mToggle);
        this.mToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        NavigationView nv = findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(this);

        nv.getMenu().getItem(0).setChecked(true);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.profile_item:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            default:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return false;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void  startChatHelper() throws JSONException {
        UserModel user = new UserModel().readLocalObj(MainActivity.this);
        mKnnServiceUtil.knnServiceJsonRequest(MainActivity.this, user, this.availableAgents.getAvailableAgentsArray(), MainActivity.this);
    }

    private void noAgentAlert() {
        doneLoadingPage();
        mAlertTittle.setText("No Agent found");
        mAlertText.setText("There is no agent available for now, please try later");
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        mAlertView.startAnimation(aniFade);
        mAlertView.setVisibility(View.VISIBLE);

        mAlertOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertView.setVisibility(View.GONE);
            }
        });
    }

    private void startChat(String agentUid) {
        doneLoadingPage();

        // send push to the agent
        String name = new UserModel().readLocalObj(this).getNickname();
        String message = "You have a new match with "+name+", Please make yourself available to talk";
        mKnnServiceUtil.sendCallNotification(mFirebaseFirestore, this, name, message, mUID, agentUid, ActivityType.MAIN.toString(),"android.intent.action.MainActivity");

        Intent intent = new Intent(MainActivity.this, MessagingActivity.class);
        intent.putExtra("AGENT_UID",agentUid);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Callback Func //////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    @Override
    public void onKnnServiceRequestOnSuccess(String agentUid) {
        Log.e(TAG, "onKnnServiceRequestOnSuccess, agentUID = " + agentUid);
        startChat(agentUid);
    }

    @Override
    public void onKnnServiceRequestFailed() {
        Log.e(TAG, "onKnnServiceRequestFailed");
        noAgentAlert();
    }

    @Override
    public void availableAgents(ArrayList<AgentModel> availableAgentsArray) {
        try {
            this.availableAgents.setAvailableAgentsArray(availableAgentsArray);
            startChatHelper();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void availableAgentsUID(ArrayList<String> availableAgentsUIDArray) {
        this.availableAgents.setAvailableAgentsArrayUID(availableAgentsUIDArray);
        if (availableAgentsUIDArray.size() > 1)
            new AgentModel().getAvailableAgents(this, availableAgentsUIDArray, this);
        else
            startChat(availableAgentsUIDArray.get(0));
    }

    @Override
    public void noAvailableAgentsUID() {
        noAgentAlert();
    }
}

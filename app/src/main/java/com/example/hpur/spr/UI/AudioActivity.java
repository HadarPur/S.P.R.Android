package com.example.hpur.spr.UI;

import android.Manifest;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.hpur.spr.Logic.Models.UserModel;
import com.example.hpur.spr.Logic.Queries.TokBoxServerSDKCallback;
import com.example.hpur.spr.Logic.Types.ActivityType;
import com.example.hpur.spr.R;
import com.example.hpur.spr.UI.Utils.OpenTokConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AudioActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener, TokBoxServerSDKCallback {

    private static final String TAG = VideoActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;

    private ImageButton mBack;
    private ImageButton mEndCall;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private Button mAlertOkBtn;
    private LinearLayout mAlertView;
    private TextView mAlertTittle;
    private TextView mAlertText;

    private OpenTokConfig mOpenTok = null;

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseAuth mAuth;
    private String mUID;
    private String mAgentUID;
    private RelativeLayout mLoadingBack;
    private TextView mLoadingtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        this.mOpenTok = new OpenTokConfig(this);
        this.mFirebaseFirestore = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.mUID = this.mAuth.getCurrentUser().getUid();
        this.mAgentUID = getIntent().getStringExtra("AgentUID");

        findViews();
        setOnClick();
        requestPermissions();
    }

    public void findViews() {
        this.mBack = findViewById(R.id.backbtn);
        this.mEndCall = findViewById(R.id.endcallvideo);
        this.mBack.setVisibility(View.VISIBLE);

        this.mAlertView = findViewById(R.id.alertview);
        this.mAlertTittle = findViewById(R.id.alerttittle);
        this.mAlertText = findViewById(R.id.msg);
        this.mAlertOkBtn = findViewById(R.id.alert_def_btn);

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));
        this.mLoadingtv = mLoadingBack.findViewById(R.id.loadingtv);
        this.mLoadingtv.setText("Connecting...");
    }

    public void setOnClick() {
        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.mEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mSession != null)
            mSession.disconnect();
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize and connect to the session
            loadingPage();
            mOpenTok.tokboxHttpJsonRequest(this);

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    // ***************************************************************** //
    // **************** SessionListener methods tokbox ***************** //
    // ***************************************************************** //

    @Override
    public void onConnected(Session session) {
        Log.i(TAG, "Session Connected");

        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(TAG, "Session Disconnected");
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        Log.i(TAG, "Stream Received");
        if (mSubscriber == null) {
            doneLoadingPage();
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            onBackPressed();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(TAG, "Session error: " + opentokError.getMessage());
    }

    // ***************************************************************** //
    // *************** PublisherListener methods tokbox **************** //
    // ***************************************************************** //

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(TAG, "Publisher error: " + opentokError.getMessage());
    }

    // ***************************************************************** //
    // *************** Tokbox server request callback ****************** //
    // ***************************************************************** //

    @Override
    public void onTokboxRequestSucceed(String apiKey, String sessionId, String tokenPublisher, String tokenSubscriber, String tokenModerator) {
        Log.e(TAG, "onTokboxRequestSucceed");
        Log.e(TAG, "apiKey = " +apiKey);
        Log.e(TAG, "sessionId = " +sessionId);
        Log.e(TAG, "tokenPublisher = " +tokenPublisher);
        Log.e(TAG, "tokenSubscriber = "+tokenSubscriber);
        Log.e(TAG, "tokenModerator = "+ tokenModerator);

        // initialize and connect to the session
        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(tokenPublisher);

        // send push to the agent
        String name = new UserModel().readLocalObj(this).getNickname();
        String message = "New incoming audio call from "+name;
        mOpenTok.sendCallNotification(mFirebaseFirestore, this, name, message, mUID, mAgentUID, apiKey, sessionId, tokenPublisher, tokenSubscriber, tokenModerator, ActivityType.AUDIO.toString(),"android.intent.action.AudioActivity");

    }

    @Override
    public void onTokboxRequestFailed() {
        Log.e(TAG, "onTokboxRequestFailed");

        this.mAlertTittle.setText("Connection failed");
        this.mAlertText.setText("Video connection failed, please try again later");
        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        this.mAlertView.startAnimation(aniFade);
        this.mAlertView.setVisibility(View.VISIBLE);

        this.mAlertOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    //start loading view for the callback
    private void loadingPage() {
        this.mLoadingBack.setVisibility(View.VISIBLE);
    }

    //finish loading view for the callback
    private void doneLoadingPage() {
        this.mLoadingBack.setVisibility(View.GONE);
    }
}

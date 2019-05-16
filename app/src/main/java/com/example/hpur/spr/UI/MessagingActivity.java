package com.example.hpur.spr.UI;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.hpur.spr.BuildConfig;
import com.example.hpur.spr.Logic.ChatBubble;
import com.example.hpur.spr.Logic.ChatBubbleAdapter;
import com.example.hpur.spr.Logic.GPSTracker;
import com.example.hpur.spr.Logic.ImageModel;
import com.example.hpur.spr.Logic.MapModel;
import com.example.hpur.spr.Logic.MessageType;
import com.example.hpur.spr.Logic.Queries.OnMapClickedCallback;
import com.example.hpur.spr.Logic.Queries.PermissionsCallback;
import com.example.hpur.spr.R;
import com.example.hpur.spr.UI.Utils.UtilitiesFunc;
import com.example.hpur.spr.UI.Utils.UtilitiesPermissions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.widget.ImageButton;

public class MessagingActivity extends AppCompatActivity implements OnMapClickedCallback, PermissionsCallback {

    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private final String TAG = "MessagingActivity:";

    private List<ChatBubble> mChatBubbles;
    private String mUserName = "AnonymousTeenager";

    private RelativeLayout mLoadingBack;

    private RecyclerView mRecycleView;
    private View mSendBtn;

    private EditText mEditText;
    private ChatBubbleAdapter mChatAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ImageButton mPhone;
    private ImageButton mVideo;
    private ImageButton mBack;

    private FloatingActionButton mSendLocation;
    private FloatingActionButton mSendImage;
    private FloatingActionButton mSendImageFromCam;

    private FloatingActionMenu mMenu;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    //File
    private File filePathImageCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mMessagesDatabaseReference = mFirebaseDatabase.getReference("SPRApp/Messages/some_agent_uid").child("Maor");

        this.mChatBubbles = new ArrayList<>();

        findViews();
        setupOnClick();

        this.mChatAdapter = new ChatBubbleAdapter(getApplicationContext(), R.layout.right_chat_bubble, mChatBubbles, this);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //Bind the RecycleView with the current application layout
        this.mRecycleView.setLayoutManager(layoutManager);
        this.mRecycleView.setAdapter(mChatAdapter);

        if (Build.VERSION.SDK_INT >= 11) {
            mRecycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (bottom < oldBottom) {
                        mRecycleView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecycleView.smoothScrollToPosition(
                                        mRecycleView.getAdapter().getItemCount());
                                mRecycleView.smoothScrollToPosition(mRecycleView.getAdapter().getItemCount());
                            }
                        }, 1);
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        attachDatabaseReadListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatBubbles.clear();
        mChatAdapter.notifyItemInserted(mChatBubbles.size());
        detachDatabaseReadListener();
    }

    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void findViews(){
        this.mRecycleView = findViewById(R.id.recycleView_msg);
        this. mSendBtn = findViewById(R.id.btn_chat_send);
        this.mEditText = findViewById(R.id.msg_type);

        this.mPhone = findViewById(R.id.phone);
        this.mVideo = findViewById(R.id.video);
        this.mBack = findViewById(R.id.backbtn);

        this.mPhone.setVisibility(View.VISIBLE);
        this.mVideo.setVisibility(View.VISIBLE);

        this.mPhone.setClickable(true);
        this.mVideo.setClickable(true);

        this.mMenu = findViewById(R.id.menu_msg);
        this.mMenu.setClosedOnTouchOutside(true);

        this.mSendLocation = findViewById(R.id.location_fab);
        this.mSendImage = findViewById(R.id.gallery_fab);
        this.mSendImageFromCam = findViewById(R.id.cam_fab);

        this.mBack.setVisibility(View.VISIBLE);

        this.mLoadingBack = findViewById(R.id.load);
        this.mLoadingBack.setBackgroundColor(Color.argb(200, 206,117,126));

    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    UtilitiesFunc.hideKeyboard(MessagingActivity.this);
                }
            }
        });

        this.mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mPhone clicked");
                startActivity(new Intent(MessagingActivity.this, AudioActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        this.mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mVideo clicked");
                startActivity(new Intent(MessagingActivity.this, VideoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //event for button SEND
        this.mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEditText.getText().toString().trim())) {
                    Toast.makeText(MessagingActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    ChatBubble chatBubble = new ChatBubble(mEditText.getText().toString(), mUserName, MessageType.USER_CHAT_MESSAGE);
                    mMessagesDatabaseReference.push().setValue(chatBubble);
                    mEditText.setText("");
                }
            }
        });

        this.mSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenu.close(true);
                locationPlacesIntent();
            }
        });

        this.mSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.close(true);
                galleryIntent();
            }
        });

        this.mSendImageFromCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenu.close(true);
                UtilitiesPermissions.verifyStoragePermissions(MessagingActivity.this,MessagingActivity.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    photoCameraIntent();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        StorageReference storageRef = storage.getReferenceFromUrl("gs://sprfinalproject.appspot.com").child("SPRApp/PhotoMessages");

        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                if (imageUri != null) {
                    Bitmap bmp = UtilitiesFunc.decodeUri(this, imageUri, 300);
                    sendFileFirebase(storageRef, UtilitiesFunc.getImageUri(this, bmp));
                }
                else
                    Log.e(TAG, "imageUri == null");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == IMAGE_CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (filePathImageCamera != null && filePathImageCamera.exists()) {
                try {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, UtilitiesFunc.getCompressed(this, filePathImageCamera.getPath()));
                } catch (Throwable t) {
                    Log.e(TAG, "Error compressing file." + t.toString());
                    t.printStackTrace();
                }

            } else {
                Log.e(TAG, "filePathImageCamera == null");
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Intent Func ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    private void galleryIntent() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), IMAGE_GALLERY_REQUEST);
    }

    private void photoCameraIntent() {
        String nomeFoto = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), nomeFoto+"camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(MessagingActivity.this, BuildConfig.APPLICATION_ID + ".provider", filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    private void locationPlacesIntent() {
        GPSTracker gpsTracker = new GPSTracker(this, false);
        if(gpsTracker.getGPSEnable()&& gpsTracker.getPosition()!=null){
            double latitude = gpsTracker.getPosition().getLatitude();
            double longitude = gpsTracker.getPosition().getLongitude();
            //if location wasn't enable and now it's enable
            gpsTracker.initLocation();

            MapModel mapModel = new MapModel(latitude+"",longitude+"");
            ChatBubble chatBubble = new ChatBubble(mapModel, mUserName, MessageType.USER_MAP_MESSAGE);
            mMessagesDatabaseReference.push().setValue(chatBubble);
            mEditText.setText("");
        }
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Database Func //////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    private void attachDatabaseReadListener() {
        if(mChildEventListener == null)
        {
            //Notify each time there is a change in messaging node in the DB
            this.mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    //Called in the start for each child in the node + every time a child inserted to the DB
                    ChatBubble curBubbleMessage = dataSnapshot.getValue(ChatBubble.class);
                    //chat bubble layout decision depends on the source of the message.
                    if (!(curBubbleMessage.getmUserName().equals(mUserName))) {
                        Log.d(TAG, "another user message");

                        if (curBubbleMessage.getmMapModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.OTHER_MAP_MESSAGE);
                        else if (curBubbleMessage.getmImageModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.OTHER_IMAGE_MESSAGE);
                        else
                            curBubbleMessage.setmMessageType(MessageType.OTHER_CHAT_MESSAGE);

                    } else {

                        Log.d(TAG, "own user message");
                        if (curBubbleMessage.getmMapModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.USER_MAP_MESSAGE);
                        else if (curBubbleMessage.getmImageModel() != null)
                            curBubbleMessage.setmMessageType(MessageType.USER_IMAGE_MESSAGE);
                        else
                            curBubbleMessage.setmMessageType(MessageType.USER_CHAT_MESSAGE);

                    }
                    //Add a new chatBubble(Message) into the list.
                    mChatBubbles.add(curBubbleMessage);
                    //Notify the Adapter to create a new view to the current received message
                    mChatAdapter.notifyItemInserted(mChatBubbles.size());
                    //Scroll the layout to the current received message
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecycleView.smoothScrollToPosition(mChatBubbles.size() - 1);
                        }
                    }, 1);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //When some sort of error occurred when there is try to make changes/read data
                }
            };

            //sign listener to the db reference
            this.mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener != null){
            this.mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Database file Func /////////////////////////
    ////////////////////////////////////////////////////////////////////////

    private void sendFileFirebase(StorageReference storageReference, final Uri uri){
        if (storageReference != null){
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            final StorageReference imageGalleryRef = storageReference.child(name+"_gallery");

            UploadTask uploadTask = imageGalleryRef.putFile(uri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL

                    return imageGalleryRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        ImageModel imageModel = new ImageModel("img", downloadUrl.toString(), name, "");
                        ChatBubble chatBubble = new ChatBubble(imageModel, mUserName, MessageType.USER_IMAGE_MESSAGE);
                        mMessagesDatabaseReference.push().setValue(chatBubble);
                    } else {
                        Log.d(TAG,"Handle failures");
                    }
                }
            });
        }
        else{
            Log.d(TAG,"storageReference is null");
        }
    }
    private void sendFileFirebase(final StorageReference storageReference, final File file){
        if (storageReference != null){
            Uri photoURI = FileProvider.getUriForFile(MessagingActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);

            UploadTask uploadTask = storageReference.putFile(photoURI);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        ImageModel imageModel = new ImageModel("img", downloadUrl.toString(), file.getName(),file.length()+"");
                        ChatBubble chatBubble = new ChatBubble(imageModel, mUserName, MessageType.USER_IMAGE_MESSAGE);
                        mMessagesDatabaseReference.push().setValue(chatBubble);
                    } else {
                        Log.e(TAG,"Handle failures");
                    }
                }
            });
        }
        else{
            Log.e(TAG,"storageReference is null");
        }
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////////////// Callback Func //////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapBubbleClicked(String lat, String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onImageBubbleClicked(String urlPhotoClick) {
        Intent intent = new Intent(this,FullScreenImageActivity.class);
        intent.putExtra("urlPhotoClick",urlPhotoClick);
        startActivity(intent);
    }

    @Override
    public void onStoragePermissionGuarantee() {
        photoCameraIntent();
    }
}



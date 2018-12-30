package com.example.hpur.spr.UI;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hpur.spr.Logic.ChatBubble;
import com.example.hpur.spr.Logic.ChatBubbleAdapter;
import com.example.hpur.spr.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;


public class MessagingActivity extends AppCompatActivity {

    private final String TAG = "MessagingActivity:";
    private RecyclerView mRecycleView;
    private View mSendBtn;
    private EditText mEditText;
    boolean mMyMessage = true;
    private String mUserName = "AnonymousTeenager";
    private List<ChatBubble> mChatBubbles;
    private ChatBubbleAdapter mChatAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ImageButton mPhone;
    private ImageButton mVideo;
    private ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        this.mFirebaseDatabase = FirebaseDatabase.getInstance();
        this.mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Messages");

        this.mChatBubbles = new ArrayList<>();

        findViews();
        setupOnClick();

        this.mChatAdapter = new ChatBubbleAdapter(getApplicationContext(), R.layout.right_chat_bubble, mChatBubbles);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        //Bind the RecycleView with the current application layout
        this.mRecycleView.setLayoutManager(layoutManager);
        this.mRecycleView.setAdapter(mChatAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mChatBubbles.clear();
        detachDatabaseReadListener();
    }

    public void onBackPressed(){
        alertGotOut();
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
    }

    // setup all button events when they clicked
    private void setupOnClick() {
        this.mPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessagingActivity.this, "mPhone clicked", Toast.LENGTH_SHORT).show();

            }
        });

        this.mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MessagingActivity.this, "mVideo clicked", Toast.LENGTH_SHORT).show();

            }
        });

        this.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        //event for button SEND
        this.mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(MessagingActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    ChatBubble chatBubble = new ChatBubble(mEditText.getText().toString(), mUserName, mMyMessage);
                    mMessagesDatabaseReference.push().setValue(chatBubble);
                    mEditText.setText("");
                }
            }
        });
    }

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
                        curBubbleMessage.setmMyMessage(false);
                    } else {
                        Log.d(TAG, "own user message");
                        curBubbleMessage.setmMyMessage(true);
                    }
                    //Add a new chatBubble(Message) into the list.
                    mChatBubbles.add(curBubbleMessage);
                    //Notify the Adapter to create a new view to the current received message
                    mChatAdapter.notifyItemInserted(mChatBubbles.size());
                    //Scroll the layout to the current received message
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecycleView.smoothScrollToPosition(mChatBubbles.size());
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

    private void alertGotOut() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to leave the chat?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MessagingActivity.super.onBackPressed();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }).create().show();
    }
}



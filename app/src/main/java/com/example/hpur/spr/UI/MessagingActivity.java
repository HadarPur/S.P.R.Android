package com.example.hpur.spr.UI;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hpur.spr.Logic.ChatBubble;
import com.example.hpur.spr.Logic.MessageAdapter;
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
    private ListView mListView;
    private View mSendBtn;
    private EditText mEditText;
    boolean mMyMessage = true;
    private String mUserName = "Anonymouse";
    private List<ChatBubble> mChatBubbles;
    private ArrayAdapter<ChatBubble> mChatAdapter;

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
        this.mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        this.mChatBubbles = new ArrayList<>();

        findViews();
        setupOnClick();
        setListener();

        //set ListView adapter first
        this.mChatAdapter = new MessageAdapter(MessagingActivity.this, R.layout.left_chat_bubble, mChatBubbles);
        this.mListView.setAdapter(mChatAdapter);

        this.mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
    }

    public void onBackPressed(){
        alertGotOut();
    }

    private void findViews(){
        this.mListView = findViewById(R.id.list_msg);
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


    private void setListener() {
        //Notify each time there is a change in messaging node in the DB
        this.mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Called in the start for each child in the node + every time a child inserted to the DB
                ChatBubble curBubbleMessage = dataSnapshot.getValue(ChatBubble.class);
                //chat bubble layout decision depends on the source of the message.
                if(!(curBubbleMessage.getmUserName().equals(mUserName))) {
                    Log.d(TAG, "line 108");
                    curBubbleMessage.setmMyMessage(false);
                }
                else{
                    Log.d(TAG,"line 110");
                    curBubbleMessage.setmMyMessage(true);
                }


                mChatAdapter.add(curBubbleMessage);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //When some sort of error occurred when there is try to make changes/read data

            }
        };
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



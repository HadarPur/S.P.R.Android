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


public class MessagingActivity extends AppCompatActivity {

    private final String TAG = "MessagingActivity:";
    private ListView listView;
    private View btnSend;
    private EditText editText;
    boolean mMyMessage = true;
    private String mUserName = "Anonymouse";
    private List<ChatBubble> mChatBubbles;
    private ArrayAdapter<ChatBubble> mChatAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");

        mChatBubbles = new ArrayList<>();
        findViews();

        //set ListView adapter first
        mChatAdapter = new MessageAdapter(MessagingActivity.this, R.layout.left_chat_bubble, mChatBubbles);
        listView.setAdapter(mChatAdapter);

        initListeners();
        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
    }

    public void onBackPressed(){
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

    private void findViews(){
        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        editText = (EditText) findViewById(R.id.msg_type);
    }

    private void initListeners(){

        //event for button SEND
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(MessagingActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    ChatBubble chatBubble = new ChatBubble(editText.getText().toString(), mUserName, mMyMessage);
                    mMessagesDatabaseReference.push().setValue(chatBubble);
                    editText.setText("");
                }
            }
        });

        //Notify each time there is a change in messaging node in the DB
        mChildEventListener = new ChildEventListener() {
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
}

    /*

    import android.os.Bundle;
    import android.view.View;
    import android.widget.ImageButton;
    import android.widget.Toast;
    import com.example.hpur.spr.R;


    private ImageButton mPhone;
    private ImageButton mVideo;
    private ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        findViews();
        setupOnClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    //find views from xml with listeners
    private void findViews() {
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
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

*/
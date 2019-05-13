package com.example.hpur.spr.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hpur.spr.R;

//In charge of handling item's content/user interaction (row)
public class ChatBubbleHolder extends RecyclerView.ViewHolder {

    private TextView mTxtMessage;
    private ImageView mMapMessage;
    private static final int USER_MESSAGE = 0;
    private static final int OTHER_MESSAGE = 1;
    private static final int USER_MAP = 2;
    private static final int OTHER_MAP = 3;

    private Context mContext;
    private ChatBubble mChatBubble;

    public ChatBubbleHolder(Context context,@NonNull View itemView, int type) {
        super(itemView);
        this.mContext = context;

        switch (type) {
            case USER_MESSAGE: case OTHER_MESSAGE:
                this.mTxtMessage = itemView.findViewById(R.id.txt_msg);
                break;
            case USER_MAP: case OTHER_MAP:
                this.mMapMessage = itemView.findViewById(R.id.img_chat);
                break;
        }
    }

    public void bindChatBubble(ChatBubble chatBubble){
        this.mChatBubble = chatBubble;
        if (mChatBubble.getmMapModel() != null)
            //.mChatBubbleMapMessage.image(mChatBubble.getmMapModel());
             Toast.makeText(mContext, "location", Toast.LENGTH_SHORT).show();
        else
            this.mTxtMessage.setText(mChatBubble.getmTextMessage());
    }
}

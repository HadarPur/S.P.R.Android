package com.example.hpur.spr.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.hpur.spr.Logic.Queries.OnMapClickedCallback;
import com.example.hpur.spr.R;
import com.github.ybq.android.spinkit.SpinKitView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

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
    private FragmentManager mFragment;
    private SpinKitView mProgressBar = null;


    public ChatBubbleHolder(FragmentManager fragment, Context context, @NonNull View itemView, int type) {
        super(itemView);
        this.mContext = context;
        this.mFragment = fragment;

        switch (type) {
            case USER_MESSAGE: case OTHER_MESSAGE:
                this.mTxtMessage = itemView.findViewById(R.id.txt_msg);
                break;
            case USER_MAP: case OTHER_MAP:
                this.mMapMessage = itemView.findViewById(R.id.map_bubble);
                this.mProgressBar = itemView.findViewById(R.id.spin_kit);

                break;
        }
    }

    public void bindChatBubble(ChatBubble chatBubble, final OnMapClickedCallback callback){
        this.mChatBubble = chatBubble;
        if (mChatBubble.getmMapModel() != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            final String latLong = mChatBubble.getmMapModel().getLongitude() +","+mChatBubble.getmMapModel().getLatitude();
            final String url = "https://static-maps.yandex.ru/1.x/?lang=en_US&ll="+latLong+"&size=400,200&z=7&l=map&pt="+latLong+",pm2rdl";
            Picasso.get().load(url).fit().into(mMapMessage, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("bindChatBubble", "onSuccess");
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Exception e) {
                    Log.d("bindChatBubble", "onError: "+ e);
                    e.printStackTrace();
                    if (mProgressBar != null) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
            this.mMapMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMapBubbleClicked(mChatBubble.getmMapModel().getLatitude(), mChatBubble.getmMapModel().getLongitude());
                }
            });
        }
        else
            this.mTxtMessage.setText(mChatBubble.getmTextMessage());
    }
}

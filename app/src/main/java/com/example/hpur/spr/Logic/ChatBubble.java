package com.example.hpur.spr.Logic;

public class ChatBubble  {

    private String mTextMessage;
    private String mUserName;
    private boolean mMyMessage;
    private MapModel mMap;

    //
    public ChatBubble(){ }

    public ChatBubble(String mTextMessage, String userName, boolean myMessage) {
        this.mTextMessage = mTextMessage;
        this.mMyMessage = myMessage;
        this.mUserName = userName;
    }

    public ChatBubble(MapModel map, String userName, boolean myMessage) {
        this.mTextMessage = "";
        this.mMap = map;
        this.mMyMessage = myMessage;
        this.mUserName = userName;
    }


    public String getmTextMessage() {
        return mTextMessage;
    }

    public void setmTextMessage(String mTextMessage) {
        this.mTextMessage = mTextMessage;
    }

    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public boolean ismMyMessage() {
        return mMyMessage;
    }

    public void setmMyMessage(boolean mMyMessage) {
        this.mMyMessage = mMyMessage;
    }

    public MapModel getmMap() {
        return mMap;
    }

    public void setmMap(MapModel mMap) {
        this.mMap = mMap;
    }
}

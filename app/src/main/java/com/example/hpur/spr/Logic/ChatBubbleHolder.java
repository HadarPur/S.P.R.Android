package com.example.hpur.spr.Logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.hpur.spr.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

//In charge of handling item's content/user interaction (row)
public class ChatBubbleHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    private TextView mTxtMessage;
    private SupportMapFragment mMapMessage;
    private static final int USER_MESSAGE = 0;
    private static final int OTHER_MESSAGE = 1;
    private static final int USER_MAP = 2;
    private static final int OTHER_MAP = 3;
    private GoogleMap mMap;
    private Context mContext;
    private ChatBubble mChatBubble;
    private MarkerOptions mMarkerOptionsMyLocation;

    public ChatBubbleHolder(FragmentManager fragment, Context context, @NonNull View itemView, int type) {
        super(itemView);
        this.mContext = context;

        switch (type) {
            case USER_MESSAGE: case OTHER_MESSAGE:
                this.mTxtMessage = itemView.findViewById(R.id.txt_msg);
                break;
            case USER_MAP: case OTHER_MAP:
                this.mMapMessage = (SupportMapFragment) fragment.findFragmentById((R.id.map_bubble));
                break;
        }
    }

    public void bindChatBubble(ChatBubble chatBubble){
        this.mChatBubble = chatBubble;
        if (mChatBubble.getmMapModel() != null)
            this.mMapMessage.getMapAsync(this);
        else
            this.mTxtMessage.setText(mChatBubble.getmTextMessage());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            this.mMap = googleMap;
            setMyLocationOnTheMap();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //set user location on the map
    public void setMyLocationOnTheMap() throws IOException{
        //place users markers
        this.mMap.clear();

        //Place current location marker
        LatLng latLng = new LatLng(Double.parseDouble(mChatBubble.getmMapModel().getLatitude()), Double.parseDouble(mChatBubble.getmMapModel().getLongitude()));
        this.mMarkerOptionsMyLocation = new MarkerOptions();

        //Place current location marker
        this.mMarkerOptionsMyLocation.position(latLng);

        this.mMap.addMarker(mMarkerOptionsMyLocation);

        //move map camera
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

    }

}

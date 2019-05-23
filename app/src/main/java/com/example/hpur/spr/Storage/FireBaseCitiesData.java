package com.example.hpur.spr.Storage;

import android.util.Log;
import com.example.hpur.spr.Logic.Models.ShelterModel;
import com.example.hpur.spr.Logic.ShelterInstance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class FireBaseCitiesData implements Serializable {
        private static final String TAG = "DATA";
    ;
        private ShelterModel mShelter;
        private ArrayList<ShelterModel> mCloudData[];
        private DatabaseReference mRef;
        private DatabaseReference[] mRefChildren;
        private String[] mCities;

        //c'tor
        public FireBaseCitiesData(String[] cities){
            FirebaseDatabase data = FirebaseDatabase.getInstance();
            this.mRefChildren = new DatabaseReference[cities.length];

            this.mCities = cities;
            // gets the shelters node reference
            this.mRef = data.getReference("SPRApp").child("Shelters");

            for(int i = 0; i < this.mRefChildren.length; i++ ){
                Log.d(TAG, "City: "+this.mCities[i]);
                this.mRefChildren[i] = this.mRef.child(mCities[i]);
            }
        }

        //init array
        public void initData(){
            mCloudData = new ArrayList[this.mCities.length];
            for(int i = 0 ; i < mCloudData.length ; i++){
                mCloudData[i] = new ArrayList<>();
            }
        }

        //read data from firebase
        public void ReadData(ShelterInstance.Callback calllback){
            initData();
            for(int i = 0 ; i < this.mRefChildren.length ; i++){
                callingEvent(this.mRefChildren[i],i,calllback);
            }
        }

        //callback event
        private void callingEvent(DatabaseReference ref,final int index,final ShelterInstance.Callback calllback){
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                    while(itr.hasNext()) {
                       mShelter = itr.next().getValue(ShelterModel.class);
                       mShelter.setCity(dataSnapshot.getKey());
                       mCloudData[index].add(mShelter);
                    }

                    if (calllback != null) {
                        calllback.onCallback(mCloudData);
                    }

                    Log.d(TAG, "City: "+ mCities[index].trim() + " mCloudData[index].size() : " + mCloudData[index].size());

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
}

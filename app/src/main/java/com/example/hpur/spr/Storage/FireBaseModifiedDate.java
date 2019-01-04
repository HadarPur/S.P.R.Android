package com.example.hpur.spr.Storage;

import com.example.hpur.spr.Logic.Queries.DateCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Iterator;

public class FireBaseModifiedDate {
    private DatabaseReference mRef;

    //c'tor
    public FireBaseModifiedDate(){
        FirebaseDatabase data = FirebaseDatabase.getInstance();

        // gets the modified date reference
        this.mRef = data.getReference("SPRApp").child("Modified Shelters");
    }


    //read data from firebase
    public void ReadData(DateCallback calllback){
        callingEvent(calllback);
    }

    //callback event
    private void callingEvent(final DateCallback calllback){
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                String date = itr.next().getValue().toString();

                if (calllback != null) {
                    calllback.getModifiedDate(date);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}

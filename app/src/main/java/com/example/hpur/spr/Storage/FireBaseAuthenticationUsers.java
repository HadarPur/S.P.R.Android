package com.example.hpur.spr.Storage;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Iterator;

public class FireBaseAuthenticationUsers implements Serializable {
    private DatabaseReference mRef;
    private boolean mUserExist;

    public void FireBaseAuthenticationUsers() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("Users");
    }

    //write into fire base
    public void writeUserToDataBase(String uid, String email) {
        this.mRef.child(uid).setValue(email);
    }

    //read from fire base
    public void readResults(final String uid, final String email) {
//        this.mUserExist = false;
//        this.mRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
//                while(itr.hasNext()) {
//                    if (itr.next().child(uid).getValue() == email) {
//                        this.mUserExist = true;
//                        break;
//                    }
//
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return answer;
    }
}

package com.example.hpur.spr.Storage;

import com.example.hpur.spr.Queries.CheckUserCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Iterator;

public class FireBaseAuthenticationUsers implements Serializable {
    private DatabaseReference mRef;

    public FireBaseAuthenticationUsers() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("Users");
    }

    //write into fire base
    public void writeUserToDataBase(String uid, String email) {
        this.mRef.child(uid).setValue(email);
    }

    public void checkUser(final String uid, final String email, final CheckUserCallback queryCallback) {
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExist = false;
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                while(itr.hasNext()) {
                    if (itr.next().getValue().equals(email)) {
                        userExist = true;
                        break;
                    }
                }
                queryCallback.performQuery(userExist);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

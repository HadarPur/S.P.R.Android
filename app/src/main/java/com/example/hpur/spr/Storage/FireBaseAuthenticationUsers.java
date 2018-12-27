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
    private final int RESET=0, SIGN=1;
    private DatabaseReference mRef;


    // c'tor
    public FireBaseAuthenticationUsers() {
        FirebaseDatabase data = FirebaseDatabase.getInstance();
        this.mRef = data.getReference("Users");
    }

    //write user into fire base
    public void writeUserToDataBase(String uid, String email) {
        this.mRef.child(uid).setValue(email);
    }

    // check if user exist
    public void checkUser(final int type,final String email, final CheckUserCallback queryCallback) {
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Checks if user already exists in the system
                boolean userExist = false;
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                while(itr.hasNext()) {
                    if (itr.next().getValue().equals(email)) {
                        userExist = true;
                        break;
                    }
                }

                switch (type) {
                    case SIGN:
                        queryCallback.CheckUserCallback(userExist);
                        break;
                    case RESET:
                        queryCallback.CheckUserExistResetCallBack(userExist);
                        break;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}

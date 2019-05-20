package com.example.hpur.spr.Storage;

import android.content.Context;

import com.example.hpur.spr.Logic.Queries.CheckUserCallbacks;
import com.example.hpur.spr.Logic.Types.GenderType;
import com.example.hpur.spr.Logic.Types.SectorType;
import com.example.hpur.spr.Logic.Types.SexType;
import com.example.hpur.spr.Logic.UserModel;
import com.example.hpur.spr.UI.SignInActivity;
import com.example.hpur.spr.UI.SignUpActivity;
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
        this.mRef = data.getReference("SPRApp").child("Users");
    }

    //write user into fire base
    public void writeUserToDataBase(String uid, UserModel user) {
        this.mRef.child(uid).setValue(user);
    }

    // check if user exist
    public void checkUser(final Context ctx, final int type, final String email, final CheckUserCallbacks queryCallback) {
        this.mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Checks if user already exists in the system
                boolean userExist = false;
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                while(itr.hasNext()) {
                    UserModel user = itr.next().getValue(UserModel.class);

                    if (user.getEmail().equals(email)) {
                        user.saveLocalObj(ctx);
                        userExist = true;
                        break;
                    }
                }

                switch (type) {
                    case SIGN:
                        queryCallback.checkUserFirebaseCallback(userExist);
                        break;
                    case RESET:
                        queryCallback.checkUserExistResetCallBack(userExist);
                        break;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

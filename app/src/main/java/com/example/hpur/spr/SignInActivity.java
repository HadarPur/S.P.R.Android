package com.example.hpur.spr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {

    private Button mSignIn;
    private Button mSignUp;
    private EditText mEmail;
    private EditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViews();
        setupOnClick();
    }

    private void findViews() {
        this.mSignIn = findViewById(R.id.sign_in);
        this.mSignUp = findViewById(R.id.signup);
        this.mEmail = findViewById(R.id.email);
        this.mPassword = findViewById(R.id.password);
    }

    private void setupOnClick() {
        this.mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}

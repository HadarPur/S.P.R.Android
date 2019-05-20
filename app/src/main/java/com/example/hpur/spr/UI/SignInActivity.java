package com.example.hpur.spr.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hpur.spr.Logic.Queries.CheckUserCallbacks;
import com.example.hpur.spr.Logic.UserModel;
import com.example.hpur.spr.R;
import com.example.hpur.spr.Storage.FireBaseAuthenticationUsers;
import com.example.hpur.spr.Storage.SharedPreferencesStorage;
import com.example.hpur.spr.UI.Utils.UtilitiesFunc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements CheckUserCallbacks {
    private static final String KEY = "connect", IS_FIRST_INSTALLATION = "false";
    private static final String TAG = SignInActivity.class.getSimpleName();
    
    private final int MIN_PASS_LEN = 6;
    private final int RESET=0, SIGN=1;

    private String mEmail;
    private String mPass;
    private boolean mForgetPassword;

    private Button mSignInBtn;
    private Button mSignUpBtn;
    private Button mPasswordResetBtn;
    private Button mGoBackBtn;
    private Button mResetBtn;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mEmailReset;

    private TextView mLoadingViewText;

    private LinearLayout mResetView;
    private LinearLayout mLoadingView;

    private FirebaseAuth mFirebaseAuth;
    private FireBaseAuthenticationUsers mUsers;
    private FirebaseUser mCurrentUser;
    private UtilitiesFunc mUtils;
    private SharedPreferencesStorage mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.mFirebaseAuth = FirebaseAuth.getInstance();
        this.mCurrentUser = mFirebaseAuth.getCurrentUser();

        this.mUsers = new FireBaseAuthenticationUsers();

        this.mSharedPreferences = new SharedPreferencesStorage(getApplicationContext());
        this.mForgetPassword = false;

        this.mUtils = new UtilitiesFunc();

        findViews();
        setupOnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // find all views from xml by id
    private void findViews() {
        this.mSignInBtn = findViewById(R.id.sign_in);
        this.mSignUpBtn = findViewById(R.id.signup);
        this.mEmailEditText = findViewById(R.id.email);
        this.mPasswordEditText = findViewById(R.id.password);
        this.mPasswordResetBtn = findViewById(R.id.passwordreset);

        this.mEmailReset = findViewById(R.id.emailreset);
        this.mGoBackBtn = findViewById(R.id.close);
        this.mResetBtn = findViewById(R.id.resetbtn);
        this.mResetView = findViewById(R.id.resetview);
        this.mLoadingView = findViewById(R.id.loadingview);
        this.mLoadingViewText = findViewById(R.id.progress_dialog_text);

        this.mEmailEditText.setText(mSharedPreferences.readData("Email"), TextView.BufferType.EDITABLE);
        this.mEmailReset.setText(mSharedPreferences.readData("Email"), TextView.BufferType.EDITABLE);
    }

    // setup all button events when they clicked
    private void setupOnClick() {

        this.mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        this.mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userSignUp();
            }
        });

        this.mPasswordResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForgetPassword = true;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                mResetView.startAnimation(aniFade);
                mResetView.setVisibility(View.VISIBLE);
                disableMainButtons();
            }
        });

        this.mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mForgetPassword = false;
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mResetView.startAnimation(aniFade);
                mResetView.setVisibility(View.INVISIBLE);
                enableMainButtons();
            }
        });

        this.mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (this.mForgetPassword) {
            this.mGoBackBtn.callOnClick();
            hideProgressDialog();
        }
        else {
            super.onBackPressed();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    // reset password for email function
    private void resetPassword() {
        this.mEmail = this.mEmailReset.getText().toString().trim();

        if (TextUtils.isEmpty(this.mEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        showProgressDialog("Please wait...");
        this.mUsers.checkUser(this,RESET, this.mEmail, SignInActivity.this);
    }

    // log in a user to app
    private void userLogin() {
        this.mEmail = this.mEmailEditText.getText().toString().trim();
        this.mPass = this.mPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(this.mEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(this.mPass)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        if (this.mPass.length() <= MIN_PASS_LEN) {
            Toast.makeText(getApplicationContext(), "Password need to be at least " + MIN_PASS_LEN + " characters", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Registering, please wait...");
        this.mFirebaseAuth.signInWithEmailAndPassword(this.mEmail, this.mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            mUsers.checkUser(SignInActivity.this,SIGN, user.getEmail(), SignInActivity.this);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();

                    }
                });
    }

    // sign up a user to the app
    private void userSignUp() {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // checked if email was verified
    private void checkEmailVerification() {
        Task usertask = this.mCurrentUser.reload();
        usertask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (mCurrentUser.isEmailVerified()) {
                    mUsers.writeUserToDataBase(mCurrentUser.getUid(), new UserModel().readLocalObj(SignInActivity.this));
                }
                else {
                    Toast.makeText(SignInActivity.this, "Email verification failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void disableMainButtons(){
        this.mSignInBtn.setClickable(false);
        this.mSignUpBtn.setClickable(false);
        this.mPasswordResetBtn.setClickable(false);
    }

    private void enableMainButtons(){
        this.mSignInBtn.setClickable(true);
        this.mSignUpBtn.setClickable(true);
        this.mPasswordResetBtn.setClickable(true);
    }

    private void showProgressDialog(final String msg) {
        this.mUtils.hideKeyboard(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingViewText.setText(msg);

                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                mLoadingView.startAnimation(aniFade);
                mLoadingView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideProgressDialog() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                mLoadingView.startAnimation(aniFade);
                mLoadingView.setVisibility(View.INVISIBLE);

                mLoadingViewText.setText("");
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// *************** firebase callbacks *************** ///////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void checkUserFirebaseCallback(boolean result) {
        if (result) {
            Log.d(TAG, "Intent intent = new Intent(SignInActivity.this, MainActivity.class)");

            this.mSharedPreferences.saveData(IS_FIRST_INSTALLATION, KEY);
            this.mSharedPreferences.saveData(this.mEmail, "Email");
            this.mSharedPreferences.saveData("true", "SignedIn");
            this.mSharedPreferences.saveData(mFirebaseAuth.getCurrentUser().getUid(), "UID");

            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
        else {
            checkEmailVerification();
        }
    }

    @Override
    public void checkUserExistResetCallBack(boolean result) {
        hideProgressDialog();
        if (result) {
            this.mFirebaseAuth.sendPasswordResetEmail(this.mEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Email sent.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else {
            Toast.makeText(SignInActivity.this, "The user does not exist", Toast.LENGTH_SHORT).show();
        }
    }
}

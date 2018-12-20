package com.example.hpur.spr.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hpur.spr.Queries.CheckUserCallback;
import com.example.hpur.spr.R;
import com.example.hpur.spr.Storage.FireBaseAuthenticationUsers;
import com.example.hpur.spr.Storage.SharedPreferencesStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements CheckUserCallback {
    private static final String KEY = "connect", IS_FIRST_INSTALLATION = "false";

    private final int RESET=0, SIGN=1;

    private static final String TAG = SignInActivity.class.getSimpleName();
    private Button mSignInBtn;
    private Button mSignUpBtn;
    private Button mPasswordResetBtn;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private FirebaseAuth mFirebaseAuth;
    private String mEmail;
    private String mPass;
    private ProgressDialog mProgressDialog;
    private FireBaseAuthenticationUsers mUsers;
    private FirebaseUser mCurrentUser;

    private EditText mEmailReset;
    private Button mGoBackBtn;
    private Button mResetBtn;
    private LinearLayout mResetView;

    private SharedPreferencesStorage mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.mFirebaseAuth = FirebaseAuth.getInstance();

        this.mProgressDialog = new ProgressDialog(this);
        this.mProgressDialog.setCancelable(false);

        this.mUsers = new FireBaseAuthenticationUsers();

        mSharedPreferences = new SharedPreferencesStorage(getApplicationContext());

        findViews();
        setupOnClick();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

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
    }

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
                mResetView.setVisibility(View.VISIBLE);
            }
        });

        this.mGoBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetView.setVisibility(View.INVISIBLE);
            }
        });

        this.mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        this.mEmail = this.mEmailReset.getText().toString().trim();

        if (TextUtils.isEmpty(this.mEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        showProgressDialog("Please wait...");
        mUsers.checkUser(RESET, this.mEmail, SignInActivity.this);
    }

    // log in a user to brand's conversation
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

        if (this.mPass.length() <= 6) {
            Toast.makeText(getApplicationContext(), "Password need to be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Registering, please wait...");

        mFirebaseAuth.signInWithEmailAndPassword(this.mEmail, this.mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            mUsers.checkUser(SIGN, user.getEmail(), SignInActivity.this);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    // sign up a user that does not exist on firebase's storage
    private void userSignUp() {
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

        if (this.mPass.length() <= 6) {
            Toast.makeText(this, "Password need to be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog("Signing up, please wait...");

        mFirebaseAuth.createUserWithEmailAndPassword(this.mEmail, this.mPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            sendEmailVerification();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void sendEmailVerification () {
        this.mCurrentUser = mFirebaseAuth.getCurrentUser();
        this.mCurrentUser.sendEmailVerification();
        Toast.makeText(SignInActivity.this, "Email verification sent successfully.", Toast.LENGTH_SHORT).show();
    }


    private void checkEmailVerification() {
        Task usertask = this.mCurrentUser.reload();
        usertask.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (mCurrentUser.isEmailVerified()) {
                    mUsers.writeUserToDataBase(mCurrentUser.getUid(), mCurrentUser.getEmail());
                }
                else {
                    Toast.makeText(SignInActivity.this, "Email verification failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // show progress dialog
    private void showProgressDialog(String message) {
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    // hide progress dialog
    private void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void CheckUserCallback(boolean result) {
        if (result) {
            mSharedPreferences.saveData(IS_FIRST_INSTALLATION, KEY);
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        else {
            checkEmailVerification();
        }
    }

    @Override
    public void CheckUserExistResetCallBack(boolean result) {
        hideProgressDialog();
        if (result) {
            mFirebaseAuth.sendPasswordResetEmail(mEmail)
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

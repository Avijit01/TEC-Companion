package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity
{
    private Button createAccountButton;
    private EditText userFullName, userId, userBatch, userSession, userPhone, userGuardianPhone, userCurrentAddress, userEmail, userPassword;
    private TextView alreadyHaveAnAccount;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                CreateNewAccount();
            }
        });
    }

    private void CreateNewAccount()
    {
        String fullName = userFullName.getText().toString();
        String id = userId.getText().toString();
        String batch = userBatch.getText().toString();
        String session = userSession.getText().toString();
        String phone = userPhone.getText().toString();
        String guardianPhone = userGuardianPhone.getText().toString();
        String currentAddress = userCurrentAddress.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();
        String userType = "Student";

        if(TextUtils.isEmpty(fullName))
        {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(id))
        {
            Toast.makeText(this, "Please enter ID", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(batch))
        {
            Toast.makeText(this, "Please enter batch", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(session))
        {
            Toast.makeText(this, "Please enter session", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter phone No.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(guardianPhone))
        {
            Toast.makeText(this, "Please enter guardian phone No.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(currentAddress))
        {
            Toast.makeText(this, "Please enter current address", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String currentUserID = mAuth.getCurrentUser().getUid();
                                rootRef.child("Users").child(currentUserID).setValue("");

                                SendUserToMainActivity();
                                Toast.makeText(Signup.this, "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(Signup.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields()
    {
        createAccountButton = (Button) findViewById(R.id.createAccountButtonId);
        userFullName = (EditText) findViewById(R.id.signupFullNameId);
        userId = (EditText) findViewById(R.id.signupIdId);
        userBatch = (EditText) findViewById(R.id.signupBatchId);
        userSession = (EditText) findViewById(R.id.signupSessionId);
        userPhone = (EditText) findViewById(R.id.signupPhoneId);
        userGuardianPhone = (EditText) findViewById(R.id.signupGuardianPhoneId);
        userCurrentAddress = (EditText) findViewById(R.id.signupCurrentAddressId);
        userEmail = (EditText) findViewById(R.id.signupEmailId);
        userPassword = (EditText) findViewById(R.id.signupPasswordId);
        alreadyHaveAnAccount = (TextView) findViewById(R.id.alreadyHaveAnAccountId);

        loadingBar = new ProgressDialog(this);
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(Signup.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(Signup.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    private Button loginButton, adminButton;
    private EditText userEmail, userPassword;
    private TextView needNewAccount, forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        InitializeFields();

        needNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToSignupOptions();
                //SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowLogin();
            }
        });

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AdminLoginActivity();
            }
        });
    }


    private void AllowLogin()
    {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password: ", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Signing In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                //Toast.makeText(Login.this, "Login Successful...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                finish();
                            }

                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(Login.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        loginButton = (Button) findViewById(R.id.loginButtonId);
        userEmail = (EditText) findViewById(R.id.loginEmailId);
        userPassword = (EditText) findViewById(R.id.loginPasswordId);
        needNewAccount = (TextView) findViewById(R.id.newAccountId);
        loadingBar = new ProgressDialog(this);
        adminButton = (Button) findViewById(R.id.adminButtonId);
    }



    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser != null)
        {
            SendUserToMainActivity();

        }
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(Login.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity()
    {
        Intent signupIntent = new Intent(Login.this, Signup.class);
        startActivity(signupIntent);
    }

    private void AdminLoginActivity()
    {
        Intent adminLoginIntent = new Intent(Login.this, AdminLogin.class);
        startActivity(adminLoginIntent);
    }

    private void SendToSignupOptions()
    {
        Intent signupOptionsIntent = new Intent(Login.this, SignupOptions.class);
        startActivity(signupOptionsIntent);
    }
}
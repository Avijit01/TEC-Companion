package com.example.teccompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignupOptions extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button signupStudentBtn, signupTeacherBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_options);

        mToolbar = (Toolbar) findViewById(R.id.SignupOptions_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Registration Options");

        signupStudentBtn = (Button) findViewById(R.id.SignupOptions_StudentBtnId);
        signupTeacherBtn = (Button) findViewById(R.id.SignupOptions_TeacherBtnId);

        signupStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToSignupStudent();
            }
        });

        signupTeacherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToSignupTeacher();
            }
        });
    }



    private void SendToSignupStudent()
    {
        Intent signupStudentIntent = new Intent(SignupOptions.this, Signup.class);
        startActivity(signupStudentIntent);
    }

    private void SendToSignupTeacher()
    {
        Intent signupTeacherIntent = new Intent(SignupOptions.this, SignupTeacher.class);
        startActivity(signupTeacherIntent);
    }
}
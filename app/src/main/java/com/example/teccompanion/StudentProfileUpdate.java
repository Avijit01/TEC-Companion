package com.example.teccompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class StudentProfileUpdate extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_update);

        mToolbar = (Toolbar) findViewById(R.id.studentProfileUpdate_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile Update");
    }
}
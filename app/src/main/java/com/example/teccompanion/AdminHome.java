package com.example.teccompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminHome extends AppCompatActivity {

    private Toolbar mToolbar;
    private CardView studentVerifyCardView, teacherVerifyCardView, adminChatCardView, adminDocumentCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        mToolbar = (Toolbar) findViewById(R.id.AdminHome_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Admin Home");

        studentVerifyCardView = (CardView) findViewById(R.id.AdminHome_StudentVerifyId);
        teacherVerifyCardView = (CardView) findViewById(R.id.AdminHome_TeacherVerifyId);
        //adminChatCardView = (CardView) findViewById(R.id.AdminHome_ChatId);
        //adminDocumentCardView = (CardView) findViewById(R.id.AdminHome_DocumentId);


        studentVerifyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToStudentVerify();
            }
        });

        teacherVerifyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendToTeacherVerify();
            }
        });


    }

    private void SendToStudentVerify()
    {
        Intent studentVerifyIntent = new Intent(this, StudentVerify.class);
        startActivity(studentVerifyIntent);
    }

    private void SendToTeacherVerify()
    {
        Intent teacherVerifyIntent = new Intent(this, TeacherVerify.class);
        startActivity(teacherVerifyIntent);
    }


}
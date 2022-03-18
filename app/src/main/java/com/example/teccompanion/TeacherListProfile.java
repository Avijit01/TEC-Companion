package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherListProfile extends AppCompatActivity
{
    private String receivedUserID;

    private Toolbar mToolbar;
    private TextView txtTeacherFullName, txtTeacherPhone, txtTeacherAddress, txtTeacherEmail, txtTeacherDept;
    private CircleImageView profileImageView;
    private String imageSend;

    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list_profile);

        mToolbar = (Toolbar) findViewById(R.id.teacherListProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Teacher Profile");

        RootRef = FirebaseDatabase.getInstance().getReference();
        receivedUserID = getIntent().getExtras().get("visit_teacher_id").toString();

        InitializeFields();
        RetrieveStudentInfo();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToImageFullScreen();
            }
        });
    }

    private void SendToImageFullScreen()
    {
        String visit_image = imageSend;

        Intent imageFullScreen = new Intent(this, ImageFullScreen.class);
        imageFullScreen.putExtra("visit_image",visit_image);
        startActivity(imageFullScreen);
    }

    private void InitializeFields()
    {
        txtTeacherFullName = (TextView)  findViewById(R.id.teacherListProfile_FullNameId);
        txtTeacherDept = (TextView)  findViewById(R.id.teacherListProfile_DeptId);;
        txtTeacherPhone = (TextView)  findViewById(R.id.teacherListProfile_PhoneNoId);
        txtTeacherAddress = (TextView)  findViewById(R.id.teacherListProfile_AddressId);
        txtTeacherEmail = (TextView)  findViewById(R.id.teacherListProfile_EmailId);
        profileImageView = (CircleImageView) findViewById(R.id.teacherListProfile_ImageTeacherId);
    }

    private void RetrieveStudentInfo()
    {
        RootRef.child("UsersVerified").child(receivedUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if((snapshot.exists()) && (snapshot.hasChild("FullName")))
                        {
                            String retrieveFullName = snapshot.child("FullName").getValue().toString();
                            String retrievePhone = snapshot.child("Phone").getValue().toString();
                            String retrieveAddress = snapshot.child("Address").getValue().toString();
                            String retrieveEmail = snapshot.child("Email").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();
                            String retrieveDept = snapshot.child("Dept").getValue().toString();

                            imageSend = retrieveProfileImage;

                            txtTeacherFullName.setText(retrieveFullName);
                            txtTeacherPhone.setText(retrievePhone);
                            txtTeacherAddress.setText(retrieveAddress);
                            txtTeacherEmail.setText(retrieveEmail);
                            txtTeacherDept.setText(retrieveDept);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(TeacherListProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfile extends AppCompatActivity
{
    private Toolbar mToolbar;
    private TextView txtTeacherFullName, txtTeacherDept, txtTeacherPhone, txtTeacherAddress, txtTeacherEmail;
    private Button editButton;
    private CircleImageView profileImageView;
    private String imageFullScreen;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        mToolbar = (Toolbar) findViewById(R.id.teacherProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();
        RetrieveUserInfo();

        editButton = (Button) findViewById(R.id.teacherProfile_ButtonId);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherProfileUpdateActivity();
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToImageFullScreen();
            }
        });

    }

    private void InitializeFields()
    {
        txtTeacherFullName = (TextView)  findViewById(R.id.teacherProfile_FullNameId);
        txtTeacherPhone = (TextView)  findViewById(R.id.teacherProfile_PhoneNoId);
        txtTeacherAddress = (TextView)  findViewById(R.id.teacherProfile_AddressId);
        txtTeacherEmail = (TextView)  findViewById(R.id.teacherProfile_EmailId);
        txtTeacherDept = (TextView) findViewById(R.id.teacherProfile_DeptId);
        profileImageView = (CircleImageView) findViewById(R.id.teacherProfile_ImageStudentId);
    }

    private void TeacherProfileUpdateActivity()
    {
        Intent teacherProfileUpdateIntent = new Intent(this, TeacherProfileUpdate.class);
        startActivity(teacherProfileUpdateIntent);
    }

    private void RetrieveUserInfo()
    {
        RootRef.child("UsersVerified").child(currentUserID)
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
                            String retrieveDept = snapshot.child("Dept").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();

                            imageFullScreen = retrieveProfileImage;

                            txtTeacherFullName.setText(retrieveFullName);
                            txtTeacherDept.setText(retrieveDept);
                            txtTeacherPhone.setText(retrievePhone);
                            txtTeacherAddress.setText(retrieveAddress);
                            txtTeacherEmail.setText(retrieveEmail);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(TeacherProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void SendToImageFullScreen()
    {
        String visit_image = imageFullScreen;

        Intent imageFullScreen = new Intent(this, ImageFullScreen.class);
        imageFullScreen.putExtra("visit_image",visit_image);
        startActivity(imageFullScreen);
    }

}
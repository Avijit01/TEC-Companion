package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BatchListProfile extends AppCompatActivity
{
    private String receivedUserID;

    private Toolbar mToolbar;
    private TextView txtStudentRegular;
    private TextView txtStudentFullName, txtStudentId, txtStudentBatch, txtStudentSession, txtStudentPhone, txtStudentGuardianPhone, txtStudentAddress, txtStudentEmail, txtStudentDept;
    private CircleImageView profileImageView;
    private String imageSend;

    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_list_profile);

        mToolbar = (Toolbar) findViewById(R.id.batchListProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Student Profile");

        RootRef = FirebaseDatabase.getInstance().getReference();
        receivedUserID = getIntent().getExtras().get("visit_student_id").toString();

        InitializeFields();
        RetrieveStudentInfo();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToImageFullScreen();
            }
        });

    }

    private void InitializeFields()
    {
        txtStudentFullName = (TextView)  findViewById(R.id.batchListProfile_FullNameId);
        txtStudentId = (TextView)  findViewById(R.id.batchListProfile_IDId);
        txtStudentDept = (TextView)  findViewById(R.id.batchListProfile_DeptId);
        txtStudentBatch = (TextView)  findViewById(R.id.batchListProfile_BatchId);
        txtStudentSession = (TextView)  findViewById(R.id.batchListProfile_SessionId);
        txtStudentPhone = (TextView)  findViewById(R.id.batchListProfile_PhoneNoId);
        txtStudentGuardianPhone = (TextView)  findViewById(R.id.batchListProfile_GuardPhoneNoId);
        txtStudentAddress = (TextView)  findViewById(R.id.batchListProfile_AddressId);
        txtStudentEmail = (TextView)  findViewById(R.id.batchListProfile_EmailId);
        profileImageView = (CircleImageView) findViewById(R.id.batchListProfile_ImageStudentId);

        txtStudentRegular = (TextView)  findViewById(R.id.batchListProfile_RegularId);
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
                            String retrieveID = snapshot.child("ID").getValue().toString();
                            String retrieveBatch = snapshot.child("Batch").getValue().toString();
                            String retrieveSession = snapshot.child("Session").getValue().toString();
                            String retrievePhone = snapshot.child("Phone").getValue().toString();
                            String retrieveGuardianPhone = snapshot.child("Guardian Phone").getValue().toString();
                            String retrieveAddress = snapshot.child("Address").getValue().toString();
                            String retrieveEmail = snapshot.child("Email").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();
                            String retrieveDept = snapshot.child("Dept").getValue().toString();

                            String retrieveRegular = snapshot.child("Regularity").getValue().toString();

                            imageSend = retrieveProfileImage;

                            txtStudentFullName.setText(retrieveFullName);
                            txtStudentId.setText(retrieveID);
                            txtStudentBatch.setText(retrieveBatch);
                            txtStudentSession.setText(retrieveSession);
                            txtStudentPhone.setText(retrievePhone);
                            txtStudentGuardianPhone.setText(retrieveGuardianPhone);
                            txtStudentAddress.setText(retrieveAddress);
                            txtStudentEmail.setText(retrieveEmail);
                            txtStudentDept.setText(retrieveDept);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);

                            txtStudentRegular.setText(retrieveRegular);
                        }
                        else
                        {
                            Toast.makeText(BatchListProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
}
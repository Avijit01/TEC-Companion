package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentProfile extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView txtStudentRegular;
    private TextView txtStudentFullName, txtStudentId, txtStudentDept, txtStudentBatch, txtStudentSession, txtStudentPhone, txtStudentGuardianPhone, txtStudentAddress, txtStudentEmail, txtStudentPassword;
    private Button editButton, deleteButton;
    private CircleImageView profileImageView;
    private String imageSend;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;

    private String fullNameDelete, typeDelete, UID_Delete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        mToolbar = (Toolbar) findViewById(R.id.studentProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        editButton = (Button) findViewById(R.id.studentProfile_ButtonId);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentProfileUpdateActivity();
            }
        });


        RetrieveUserInfo();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToImageFullScreen();
            }
        });


        deleteButton = (Button) findViewById(R.id.studentProfile_DeleteButtonId);

        /*
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDialogDelete();
            }
        });

         */
    }



    private void CreateDialogDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want to Delete the Account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                ProfileUpdate();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        builder.create();
        builder.show();
    }


    private void ProfileUpdate()
    {
        String setValue = "";
        typeDelete = "User Deleted";

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("ID", setValue);
        profileMap.put("Batch", setValue);
        profileMap.put("Session", setValue);
        profileMap.put("Phone", setValue);
        profileMap.put("Guardian Phone", setValue);
        profileMap.put("Address", setValue);
        profileMap.put("Email", setValue);
        profileMap.put("Password", setValue);
        profileMap.put("Level", setValue);
        profileMap.put("Dept", setValue);
        profileMap.put("Regularity",setValue);

        profileMap.put("Type", typeDelete);
        profileMap.put("UID", currentUserID);
        profileMap.put("FullName", fullNameDelete);
        profileMap.put("ImageUrl", String.valueOf(imageSend));
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
        txtStudentFullName = (TextView)  findViewById(R.id.studentProfile_FullNameId);
        txtStudentId = (TextView)  findViewById(R.id.studentProfile_IDId);
        txtStudentDept = (TextView) findViewById(R.id.studentProfile_DeptId);
        txtStudentBatch = (TextView)  findViewById(R.id.studentProfile_BatchId);
        txtStudentSession = (TextView)  findViewById(R.id.studentProfile_SessionId);
        txtStudentPhone = (TextView)  findViewById(R.id.studentProfile_PhoneNoId);
        txtStudentGuardianPhone = (TextView)  findViewById(R.id.studentProfile_GuardPhoneNoId);
        txtStudentAddress = (TextView)  findViewById(R.id.studentProfile_AddressId);
        txtStudentEmail = (TextView)  findViewById(R.id.studentProfile_EmailId);
        profileImageView = (CircleImageView) findViewById(R.id.studentProfile_ImageStudentId);

        txtStudentRegular = (TextView)  findViewById(R.id.studentProfile_RegularId);
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
                            String retrieveID = snapshot.child("ID").getValue().toString();
                            String retrieveDept = snapshot.child("Dept").getValue().toString();
                            String retrieveBatch = snapshot.child("Batch").getValue().toString();
                            String retrieveSession = snapshot.child("Session").getValue().toString();
                            String retrievePhone = snapshot.child("Phone").getValue().toString();
                            String retrieveGuardianPhone = snapshot.child("Guardian Phone").getValue().toString();
                            String retrieveAddress = snapshot.child("Address").getValue().toString();
                            String retrieveEmail = snapshot.child("Email").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();

                            String retriveRegular = snapshot.child("Regularity").getValue().toString();

                            imageSend = retrieveProfileImage;

                            fullNameDelete = retrieveFullName;

                            txtStudentFullName.setText(retrieveFullName);
                            txtStudentId.setText(retrieveID);
                            txtStudentDept.setText(retrieveDept);
                            txtStudentBatch.setText(retrieveBatch);
                            txtStudentSession.setText(retrieveSession);
                            txtStudentPhone.setText(retrievePhone);
                            txtStudentGuardianPhone.setText(retrieveGuardianPhone);
                            txtStudentAddress.setText(retrieveAddress);
                            txtStudentEmail.setText(retrieveEmail);

                            txtStudentRegular.setText(retriveRegular);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(StudentProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void StudentProfileUpdateActivity()
    {
        Intent studentProfileUpdateIntent = new Intent(this, StudentProfileUpdate.class);
        startActivity(studentProfileUpdateIntent);
    }
}
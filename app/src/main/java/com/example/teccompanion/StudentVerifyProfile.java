package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentVerifyProfile extends AppCompatActivity
{
    private String receivedUserID, email, password, image;

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private TextView txtStudentFullName, txtStudentId, txtStudentBatch, txtStudentSession, txtStudentRegular, txtStudentPhone, txtStudentGuardianPhone, txtStudentAddress, txtStudentEmail, txtStudentDept;
    private Button acceptButton, rejectButton;
    private CircleImageView profileImageView;

    private int batchInt;
    private String batchQuery;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_verify_profile);

        mToolbar = (Toolbar) findViewById(R.id.studentVerifyProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Student Profile Verify");

        RootRef = FirebaseDatabase.getInstance().getReference();

        receivedUserID = getIntent().getExtras().get("visit_student_id").toString();
        //Toast.makeText(this, "UserId: "+receivedUserID, Toast.LENGTH_SHORT).show();

        InitializeFields();
        RetrieveStudentInfo();

        acceptButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CreateDialogAccept();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoggingIn();
                CreateDialogReject();
            }
        });

    }

    private void CreateDialogAccept()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want to Accept the Request?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                SaveStudent();
                DeleteTemp();
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


    private void SaveStudent()
    {

        RootRef.child("Users").child(receivedUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if((snapshot.exists()))
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
                            String retrieveLevel = "1";
                            String retrievePassword = snapshot.child("Password").getValue().toString();
                            String retrieveType = snapshot.child("Type").getValue().toString();
                            String retrieveUID = snapshot.child("UID").getValue().toString();
                            String retrieveDept = snapshot.child("Dept").getValue().toString();

                            String retrieveRegularity = snapshot.child("Regularity").getValue().toString();

                            HashMap<String, String> profileMap = new HashMap<>();
                            profileMap.put("UID", retrieveUID);
                            profileMap.put("FullName", retrieveFullName);
                            profileMap.put("Dept", retrieveDept);
                            profileMap.put("ID", retrieveID);
                            profileMap.put("Batch", retrieveBatch);
                            profileMap.put("Session", retrieveSession);
                            profileMap.put("Phone", retrievePhone);
                            profileMap.put("Guardian Phone", retrieveGuardianPhone);
                            profileMap.put("Address", retrieveAddress);
                            profileMap.put("Email", retrieveEmail);
                            profileMap.put("Password", retrievePassword);
                            profileMap.put("Type", retrieveType);
                            profileMap.put("Level", retrieveLevel);
                            profileMap.put("ImageUrl", retrieveProfileImage);

                            profileMap.put("Regularity", retrieveRegularity);


                            RootRef.child("UsersVerified").child(receivedUserID)
                                    .setValue(profileMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(StudentVerifyProfile.this, "Verified Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(StudentVerifyProfile.this, "Unsuccessful..." +message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                            batchQuery = retrieveBatch+"_"+retrieveID;

                            batchInt = Integer.parseInt(retrieveBatch);

                            if(retrieveRegularity.equals("Irregular-1"))
                            {
                                batchInt = batchInt + 1;
                                batchQuery = batchInt + "_"+retrieveID;
                            }

                            if(retrieveRegularity.equals("Irregular-2"))
                            {
                                batchInt = batchInt + 2;
                                batchQuery = batchInt + "_"+retrieveID;
                            }



                            HashMap<String, String> profileMapStudent = new HashMap<>();
                            profileMapStudent.put("UID", retrieveUID);
                            profileMapStudent.put("FullName", retrieveFullName);
                            profileMapStudent.put("Dept", retrieveDept);
                            profileMapStudent.put("ID", retrieveID);
                            profileMapStudent.put("Batch", retrieveBatch);
                            profileMapStudent.put("Session", retrieveSession);
                            profileMapStudent.put("Phone", retrievePhone);
                            profileMapStudent.put("Guardian Phone", retrieveGuardianPhone);
                            profileMapStudent.put("Address", retrieveAddress);
                            profileMapStudent.put("Email", retrieveEmail);
                            profileMapStudent.put("Password", retrievePassword);
                            profileMapStudent.put("Type", retrieveType);
                            profileMapStudent.put("Level", retrieveLevel);
                            profileMapStudent.put("ImageUrl", retrieveProfileImage);

                            profileMapStudent.put("Regularity",retrieveRegularity);
                            profileMapStudent.put("BatchQuery", batchQuery);

                            RootRef.child("VerifiedStudents").child(receivedUserID)
                                    .setValue(profileMapStudent)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(StudentVerifyProfile.this, "Verified Batch Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(StudentVerifyProfile.this, "Unsuccessful..." +message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            HashMap<String, String> profileBatch = new HashMap<>();
                            profileBatch.put("Batch", retrieveBatch);

                            RootRef.child("Batch").child("batch-"+retrieveBatch)
                                    .setValue(profileBatch)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                //Toast.makeText(StudentVerifyProfile.this, "Verified Batch Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(StudentVerifyProfile.this, "Unsuccessful..." +message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                        else
                        {
                            //Toast.makeText(StudentVerifyProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void DeleteTemp()
    {
        RootRef.child("Users").child(receivedUserID)
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        Intent studentVerifyIntent = new Intent(StudentVerifyProfile.this, StudentVerify.class);
                        startActivity(studentVerifyIntent);
                        finish();
                    }
                });
    }


    private void InitializeFields()
    {
        txtStudentFullName = (TextView)  findViewById(R.id.studentVerifyProfile_FullNameId);
        txtStudentId = (TextView)  findViewById(R.id.studentVerifyProfile_IDId);
        txtStudentDept = (TextView)  findViewById(R.id.studentVerifyProfile_DeptId);
        txtStudentBatch = (TextView)  findViewById(R.id.studentVerifyProfile_BatchId);
        txtStudentSession = (TextView)  findViewById(R.id.studentVerifyProfile_SessionId);
        txtStudentPhone = (TextView)  findViewById(R.id.studentVerifyProfile_PhoneNoId);
        txtStudentGuardianPhone = (TextView)  findViewById(R.id.studentVerifyProfile_GuardPhoneNoId);
        txtStudentAddress = (TextView)  findViewById(R.id.studentVerifyProfile_AddressId);
        txtStudentEmail = (TextView)  findViewById(R.id.studentVerifyProfile_EmailId);
        profileImageView = (CircleImageView) findViewById(R.id.studentVerifyProfile_ImageStudentId);

        txtStudentRegular = (TextView)  findViewById(R.id.studentVerifyProfile_RegularId);

        acceptButton = (Button) findViewById(R.id.studentVerifyProfile_AcceptButtonId);
        rejectButton = (Button) findViewById(R.id.studentVerifyProfile_RejectButtonId);
        loadingBar = new ProgressDialog(this);
    }

    private void RetrieveStudentInfo()
    {
        RootRef.child("Users").child(receivedUserID)
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

                            email = retrieveEmail;
                            password = snapshot.child("Password").getValue().toString();
                            image = retrieveProfileImage;

                            txtStudentFullName.setText(retrieveFullName);
                            txtStudentId.setText(retrieveID);
                            txtStudentBatch.setText(retrieveBatch);
                            txtStudentSession.setText(retrieveSession);
                            txtStudentPhone.setText(retrievePhone);
                            txtStudentGuardianPhone.setText(retrieveGuardianPhone);
                            txtStudentAddress.setText(retrieveAddress);
                            txtStudentEmail.setText(retrieveEmail);
                            txtStudentDept.setText(retrieveDept);

                            txtStudentRegular.setText(retrieveRegular);

                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(StudentVerifyProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void LoggingIn()
    {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(StudentVerifyProfile.this, "Login Successful...", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(StudentVerifyProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void CreateDialogReject()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want to Reject the Request?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //Toast.makeText(StudentVerifyProfile.this, "No Action For Now", Toast.LENGTH_SHORT).show();
                DeleteStudent();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(StudentVerifyProfile.this, "Logout Success...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create();
        builder.show();

    }

    private void DeleteStudent()
    {
        RootRef.child("Users").child(receivedUserID)
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            StorageDelete();
                                            Intent studentVerifyIntent = new Intent(StudentVerifyProfile.this, StudentVerify.class);
                                            startActivity(studentVerifyIntent);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(StudentVerifyProfile.this, "There is a problem", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void StorageDelete()
    {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("User Profile Images").child(receivedUserID+".jpg");
        storageRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                        Toast.makeText(StudentVerifyProfile.this, "Image is Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}
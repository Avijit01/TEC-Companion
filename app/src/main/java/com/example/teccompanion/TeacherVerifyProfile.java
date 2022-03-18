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

public class TeacherVerifyProfile extends AppCompatActivity
{
    private String receivedUserID, email, password, image;

    private Toolbar mToolbar;
    private ProgressDialog loadingBar;
    private TextView txtTeacherFullName, txtTeacherPhone, txtTeacherAddress, txtTeacherEmail, txtTeacherDept;
    private Button acceptButton, rejectButton;
    private CircleImageView profileImageView;

    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_verify_profile);

        mToolbar = (Toolbar) findViewById(R.id.teacherVerifyProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Teacher Profile");

        RootRef = FirebaseDatabase.getInstance().getReference();

        receivedUserID = getIntent().getExtras().get("visit_teacher_id").toString();
        Toast.makeText(TeacherVerifyProfile.this, "ID: "+ receivedUserID, Toast.LENGTH_SHORT).show();

        InitializeFields();
        RetrieveTeacherInfo();

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

    private void InitializeFields()
    {
        txtTeacherFullName = (TextView)  findViewById(R.id.teacherVerifyProfile_FullNameId);
        txtTeacherPhone = (TextView)  findViewById(R.id.teacherVerifyProfile_PhoneNoId);
        txtTeacherAddress = (TextView)  findViewById(R.id.teacherVerifyProfile_AddressId);
        txtTeacherEmail = (TextView)  findViewById(R.id.teacherVerifyProfile_EmailId);
        txtTeacherDept = (TextView)  findViewById(R.id.teacherVerifyProfile_DeptId);
        profileImageView = (CircleImageView) findViewById(R.id.teacherVerifyProfile_ImageTeacherId);
        acceptButton = (Button) findViewById(R.id.teacherVerifyProfile_AcceptButtonId);
        rejectButton = (Button) findViewById(R.id.teacherVerifyProfile_RejectButtonId);
        loadingBar = new ProgressDialog(this);

    }

    private void RetrieveTeacherInfo()
    {
        RootRef.child("Users").child(receivedUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if((snapshot.exists()) && (snapshot.hasChild("FullName")))
                        {
                            String retrieveFullName = snapshot.child("FullName").getValue().toString();

                            String retrieveAddress = snapshot.child("Address").getValue().toString();
                            String retrieveEmail = snapshot.child("Email").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();
                            String retrievePhone = snapshot.child("Phone").getValue().toString();
                            String retrieveDept = snapshot.child("Dept").getValue().toString();

                            email = retrieveEmail;
                            password = snapshot.child("Password").getValue().toString();
                            image = retrieveProfileImage;

                            txtTeacherFullName.setText(retrieveFullName);
                            txtTeacherDept.setText(retrieveDept);
                            txtTeacherAddress.setText(retrieveAddress);
                            txtTeacherEmail.setText(retrieveEmail);
                            txtTeacherPhone.setText(retrievePhone);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(TeacherVerifyProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
                SaveTeacher();
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

    private void SaveTeacher()
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

                            HashMap<String, String> profileMap = new HashMap<>();
                            profileMap.put("UID", retrieveUID);
                            profileMap.put("FullName", retrieveFullName);
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
                            profileMap.put("Dept", retrieveDept);
                            profileMap.put("ImageUrl", retrieveProfileImage);

                            RootRef.child("UsersVerified").child(receivedUserID)
                                    .setValue(profileMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(TeacherVerifyProfile.this, "Verified Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(TeacherVerifyProfile.this, "Unsuccessful..." +message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            HashMap<String, String> profileMapTeacher = new HashMap<>();
                            profileMapTeacher.put("UID", retrieveUID);
                            profileMapTeacher.put("FullName", retrieveFullName);
                            profileMapTeacher.put("Phone", retrievePhone);
                            profileMapTeacher.put("Address", retrieveAddress);
                            profileMapTeacher.put("Email", retrieveEmail);
                            profileMapTeacher.put("Password", retrievePassword);
                            profileMapTeacher.put("Type", retrieveType);
                            profileMapTeacher.put("Level", retrieveLevel);
                            profileMapTeacher.put("Dept", retrieveDept);
                            profileMapTeacher.put("ImageUrl", retrieveProfileImage);

                            RootRef.child("VerifiedTeachers").child(receivedUserID)
                                    .setValue(profileMapTeacher)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(TeacherVerifyProfile.this, "Verified Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(TeacherVerifyProfile.this, "Unsuccessful..." +message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                        else
                        {
                            Toast.makeText(TeacherVerifyProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
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
                        Intent teacherVerifyIntent = new Intent(TeacherVerifyProfile.this, TeacherVerify.class);
                        startActivity(teacherVerifyIntent);
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
                            Toast.makeText(TeacherVerifyProfile.this, "Login Successful...", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(TeacherVerifyProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                DeleteTeacher();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(TeacherVerifyProfile.this, "Logout Success...", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create();
        builder.show();

    }

    private void DeleteTeacher()
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
                                            Intent teacherVerifyIntent = new Intent(TeacherVerifyProfile.this, TeacherVerify.class);
                                            startActivity(teacherVerifyIntent);
                                        }
                                        else
                                        {
                                            Toast.makeText(TeacherVerifyProfile.this, "There is a problem", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TeacherVerifyProfile.this, "Image is Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

    }


}
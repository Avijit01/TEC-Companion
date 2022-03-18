package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherProfileUpdate extends AppCompatActivity
{
    private Toolbar mToolbar;
    private TextView txtTeacherFullName, txtTeacherDept, txtTeacherPhone, txtTeacherAddress, txtTeacherEmail, txtTeacherPassword;
    private Button updateButton;
    private CircleImageView profileImageView;
    private String uid, type, level, dept;
    private String fullName, id, batch, session, phone, guardianPhone, address, email, password, image;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;
    private static final int galleryPick = 1;
    private StorageReference userProfileImagesRef;
    private Uri userImageUri;
    private StorageReference filePath;
    private DatabaseReference dataStore;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile_update);

        mToolbar = (Toolbar) findViewById(R.id.teacherProfileUpdate_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile Update");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("User Profile Images");

        InitializeFields();
        RetrieveUserInfo();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                UpdateProfile();
            }
        });
    }

    private void SendToStudentProfile()
    {
        Intent teacherProfileIntent = new Intent(TeacherProfileUpdate.this, TeacherList.class);
        startActivity(teacherProfileIntent);
        finish();
    }

    private void InitializeFields()
    {
        txtTeacherFullName = (TextView)  findViewById(R.id.teacherProfileUpdate_FullNameId);
        txtTeacherDept = (TextView)  findViewById(R.id.teacherProfileUpdate_DeptId);
        txtTeacherPhone = (TextView)  findViewById(R.id.teacherProfileUpdate_PhoneId);
        txtTeacherAddress = (TextView)  findViewById(R.id.teacherProfileUpdate_AddressId);
        txtTeacherEmail = (TextView)  findViewById(R.id.teacherProfileUpdate_EmailId);
        txtTeacherPassword = (TextView)  findViewById(R.id.teacherProfileUpdate_PasswordId);
        profileImageView = (CircleImageView) findViewById(R.id.teacherProfileUpdate_circularImageTeacherId);
        updateButton = (Button) findViewById(R.id.teacherProfileUpdate_ButtonId);
        loadingBar = new ProgressDialog(this);
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
                            String retrieveBatch = snapshot.child("Batch").getValue().toString();
                            String retrieveSession = snapshot.child("Session").getValue().toString();
                            String retrievePhone = snapshot.child("Phone").getValue().toString();
                            String retrieveGuardianPhone = snapshot.child("Guardian Phone").getValue().toString();
                            String retrieveAddress = snapshot.child("Address").getValue().toString();
                            String retrieveEmail = snapshot.child("Email").getValue().toString();
                            String retrievePassword = snapshot.child("Password").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();

                            id = retrieveID;
                            batch = retrieveBatch;
                            session = retrieveSession;
                            guardianPhone = retrieveGuardianPhone;
                            dept = snapshot.child("Dept").getValue().toString();
                            type = snapshot.child("Type").getValue().toString();
                            uid = snapshot.child("UID").getValue().toString();
                            level = snapshot.child("Level").getValue().toString();
                            image = snapshot.child("ImageUrl").getValue().toString();

                            txtTeacherFullName.setText(retrieveFullName);
                            txtTeacherPhone.setText(retrievePhone);
                            txtTeacherAddress.setText(retrieveAddress);
                            txtTeacherEmail.setText(retrieveEmail);
                            txtTeacherPassword.setText(retrievePassword);
                            txtTeacherDept.setText(dept);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(TeacherProfileUpdate.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void UpdateProfile()
    {
        fullName = txtTeacherFullName.getText().toString();
        phone = txtTeacherPhone.getText().toString();
        address = txtTeacherAddress.getText().toString();
        email = txtTeacherEmail.getText().toString();
        password = txtTeacherPassword.getText().toString();

        if(TextUtils.isEmpty(fullName))
        {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(dept))
        {
            Toast.makeText(this, "Please enter department", Toast.LENGTH_SHORT).show();
        }

        if(!dept.equals("CSE") && !dept.equals("EEE") && !dept.equals("Civil") && !dept.equals("Textile") && !dept.equals("ICE"))
        {
            Toast.makeText(this, "Please enter department correctly", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter phone No.", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(address))
        {
            Toast.makeText(this, "Please enter current address", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }

        if(password.length() < 6 || password.length() >12)
        {
            Toast.makeText(this, "Password length should be 6 to 12", Toast.LENGTH_SHORT).show();
        }



        if(flag == 1)
        {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("User Profile Images").child(currentUserID+".jpg");
            storageRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused)
                        {
                            Toast.makeText(TeacherProfileUpdate.this, "Image is Deleted", Toast.LENGTH_SHORT).show();
                        }
                    });

            dataStore = FirebaseDatabase.getInstance().getReference().child("UsersVerified").child(currentUserID);
            filePath = userProfileImagesRef.child(currentUserID + ".jpg");


            filePath.putFile(userImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if(task.isSuccessful())
                    {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("UID", uid);
                                profileMap.put("FullName", fullName);
                                profileMap.put("ID", id);
                                profileMap.put("Batch", batch);
                                profileMap.put("Session", session);
                                profileMap.put("Phone", phone);
                                profileMap.put("Guardian Phone", guardianPhone);
                                profileMap.put("Address", address);
                                profileMap.put("Email", email);
                                profileMap.put("Password", password);
                                profileMap.put("Type", type);
                                profileMap.put("Level", level);
                                profileMap.put("Dept", dept);
                                profileMap.put("ImageUrl", String.valueOf(uri));


                                RootRef.child("UsersVerified").child(currentUserID).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(TeacherProfileUpdate.this, "Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(TeacherProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                                currentUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused)
                                    {
                                        Toast.makeText(TeacherProfileUpdate.this, "Email Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Toast.makeText(TeacherProfileUpdate.this, "Email Update Failed"+e, Toast.LENGTH_SHORT).show();
                                    }
                                });


                                currentUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused)
                                    {
                                        Toast.makeText(TeacherProfileUpdate.this, "Password Update Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        Toast.makeText(TeacherProfileUpdate.this, "Password Update Failed"+e, Toast.LENGTH_SHORT).show();
                                    }
                                });


                                //VerifiedTeachers
                                HashMap<String, Object> profileMapTeacher = new HashMap<>();
                                profileMapTeacher.put("UID", uid);
                                profileMapTeacher.put("FullName", fullName);
                                profileMapTeacher.put("Phone", phone);
                                profileMapTeacher.put("Address", address);
                                profileMapTeacher.put("Email", email);
                                profileMapTeacher.put("Password", password);
                                profileMapTeacher.put("Type", type);
                                profileMapTeacher.put("Level", level);
                                profileMapTeacher.put("Dept", dept);
                                profileMapTeacher.put("ImageUrl", String.valueOf(uri));

                                RootRef.child("VerifiedTeachers").child(currentUserID).updateChildren(profileMapTeacher)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(TeacherProfileUpdate.this, "Verified Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(TeacherProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        });

                        //Toast.makeText(TeacherProfileUpdate.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        String msg = task.getException().toString();
                        Toast.makeText(TeacherProfileUpdate.this, "Error: " +msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });


            //SendToStudentProfile();

        }


        if(flag == 0)
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("UID", uid);
            profileMap.put("FullName", fullName);
            profileMap.put("ID", id);
            profileMap.put("Batch", batch);
            profileMap.put("Session", session);
            profileMap.put("Phone", phone);
            profileMap.put("Guardian Phone", guardianPhone);
            profileMap.put("Address", address);
            profileMap.put("Email", email);
            profileMap.put("Password", password);
            profileMap.put("Type", type);
            profileMap.put("Level", level);
            profileMap.put("Dept", dept);
            profileMap.put("ImageUrl", image);


            RootRef.child("UsersVerified").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(TeacherProfileUpdate.this, "Database Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(TeacherProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            currentUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused)
                {
                    Toast.makeText(TeacherProfileUpdate.this, "Email Update Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(TeacherProfileUpdate.this, "Email Update Failed"+e, Toast.LENGTH_SHORT).show();
                }
            });


            currentUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused)
                {
                    Toast.makeText(TeacherProfileUpdate.this, "Password Update Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(TeacherProfileUpdate.this, "Password Update Failed"+e, Toast.LENGTH_SHORT).show();
                }
            });


            //VerifiedTeachers
            HashMap<String, Object> profileMapTeacher2 = new HashMap<>();
            profileMapTeacher2.put("UID", uid);
            profileMapTeacher2.put("FullName", fullName);
            profileMapTeacher2.put("Phone", phone);
            profileMapTeacher2.put("Address", address);
            profileMapTeacher2.put("Email", email);
            profileMapTeacher2.put("Password", password);
            profileMapTeacher2.put("Type", type);
            profileMapTeacher2.put("Level", level);
            profileMapTeacher2.put("Dept", dept);
            profileMapTeacher2.put("ImageUrl",image);

            RootRef.child("VerifiedTeachers").child(currentUserID).updateChildren(profileMapTeacher2)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(TeacherProfileUpdate.this, "Verified Database Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(TeacherProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            //SendToStudentProfile();

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == galleryPick &&resultCode == RESULT_OK && data != null)
        {
            Uri imageUri = data.getData();
            flag = 1;

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            userImageUri = result.getUri();
            profileImageView.setImageURI(userImageUri);

        }

    }
}
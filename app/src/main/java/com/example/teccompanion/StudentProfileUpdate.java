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

public class StudentProfileUpdate extends AppCompatActivity
{
    private Toolbar mToolbar;
    private TextView txtStudentFullName, txtStudentId, txtStudentBatch, txtStudentSession, txtStudentRegular, txtStudentPhone, txtStudentGuardianPhone, txtStudentAddress, txtStudentEmail, txtStudentPassword;
    private Button updateButton;
    private CircleImageView profileImageView;
    private String uid, type, level, dept;
    private String fullName, id, batch, session, regular, phone, guardianPhone, address, email, password, image;

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
    private int batchInt;
    private String batchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_update);

        mToolbar = (Toolbar) findViewById(R.id.studentProfileUpdate_toolbarId);
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
                loadingBar.setTitle("Loading");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                UpdateProfile();

                loadingBar.dismiss();
            }
        });
    }

    private void SendToStudentProfile()
    {
        Intent studentProfileIntent = new Intent(StudentProfileUpdate.this, StudentList.class);
        startActivity(studentProfileIntent);
        finish();
    }


    private void InitializeFields()
    {
        txtStudentFullName = (TextView)  findViewById(R.id.studentProfileUpdate_FullNameId);
        txtStudentId = (TextView)  findViewById(R.id.studentProfileUpdate_IdId);
        txtStudentBatch = (TextView)  findViewById(R.id.studentProfileUpdate_BatchId);
        txtStudentSession = (TextView)  findViewById(R.id.studentProfileUpdate_SessionId);
        txtStudentPhone = (TextView)  findViewById(R.id.studentProfileUpdate_PhoneId);
        txtStudentGuardianPhone = (TextView)  findViewById(R.id.studentProfileUpdate_GuardianPhoneId);
        txtStudentAddress = (TextView)  findViewById(R.id.studentProfileUpdate_AddressId);
        txtStudentEmail = (TextView)  findViewById(R.id.studentProfileUpdate_EmailId);
        txtStudentPassword = (TextView)  findViewById(R.id.studentProfileUpdate_PasswordId);
        profileImageView = (CircleImageView) findViewById(R.id.studentProfileUpdate_ImageStudentId);

        txtStudentRegular = (TextView)  findViewById(R.id.studentProfileUpdate_RegularId);

        updateButton = (Button) findViewById(R.id.studentProfileUpdate_ButtonId);
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

                            String retrieveRegular = snapshot.child("Regularity").getValue().toString();

                            dept = snapshot.child("Dept").getValue().toString();
                            type = snapshot.child("Type").getValue().toString();
                            uid = snapshot.child("UID").getValue().toString();
                            level = snapshot.child("Level").getValue().toString();
                            image = snapshot.child("ImageUrl").getValue().toString();

                            txtStudentFullName.setText(retrieveFullName);
                            txtStudentId.setText(retrieveID);
                            txtStudentBatch.setText(retrieveBatch);
                            txtStudentSession.setText(retrieveSession);
                            txtStudentPhone.setText(retrievePhone);
                            txtStudentGuardianPhone.setText(retrieveGuardianPhone);
                            txtStudentAddress.setText(retrieveAddress);
                            txtStudentEmail.setText(retrieveEmail);
                            txtStudentPassword.setText(retrievePassword);
                            txtStudentRegular.setText(retrieveRegular);
                            Picasso.get().load(retrieveProfileImage).into(profileImageView);
                        }
                        else
                        {
                            Toast.makeText(StudentProfileUpdate.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void UpdateProfile()
    {
        fullName = txtStudentFullName.getText().toString();
        id = txtStudentId.getText().toString();
        batch = txtStudentBatch.getText().toString();
        session = txtStudentSession.getText().toString();
        phone = txtStudentPhone.getText().toString();
        guardianPhone = txtStudentGuardianPhone.getText().toString();
        address = txtStudentAddress.getText().toString();
        email = txtStudentEmail.getText().toString();
        password = txtStudentPassword.getText().toString();

        regular = txtStudentRegular.getText().toString();


        if(TextUtils.isEmpty(fullName))
        {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(id))
        {
            Toast.makeText(this, "Please enter ID", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(batch))
        {
            Toast.makeText(this, "Please enter batch", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(session))
        {
            Toast.makeText(this, "Please enter session", Toast.LENGTH_SHORT).show();
        }


        else if(TextUtils.isEmpty(regular))
        {
            Toast.makeText(this, "Please enter Regular/Irregular-1/Irregular-2", Toast.LENGTH_SHORT).show();
        }

        else if((!regular.equals("Regular")) && (!regular.equals("Irregular-1")) && (!regular.equals("Irregular-2")))
        {
            Toast.makeText(this, "Please enter Regular/Irregular-1/Irregular-2 Correctly", Toast.LENGTH_SHORT).show();
        }


        else if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter phone No.", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(guardianPhone))
        {
            Toast.makeText(this, "Please enter guardian phone No.", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(address))
        {
            Toast.makeText(this, "Please enter current address", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email address", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        }

        else if(password.length() < 6 || password.length() >12)
        {
            Toast.makeText(this, "Password length should be 6 to 12", Toast.LENGTH_SHORT).show();
        }


        else
        {

            if(flag == 1)
            {

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("User Profile Images").child(currentUserID+".jpg");
                storageRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused)
                            {
                                Toast.makeText(StudentProfileUpdate.this, "Image is Deleted", Toast.LENGTH_SHORT).show();
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
                                    profileMap.put("Regularity",regular);


                                    RootRef.child("UsersVerified").child(currentUserID).updateChildren(profileMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                       // Toast.makeText(StudentProfileUpdate.this, "Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        String message = task.getException().toString();
                                                        Toast.makeText(StudentProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                    currentUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused)
                                        {
                                           // Toast.makeText(StudentProfileUpdate.this, "Email Update Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(StudentProfileUpdate.this, "Email Update Failed"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    currentUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused)
                                        {
                                           // Toast.makeText(StudentProfileUpdate.this, "Password Update Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(StudentProfileUpdate.this, "Password Update Failed"+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });




                                    //Verified Students

                                    batchQuery = batch+"_"+id;

                                    batchInt = Integer.parseInt(batch);

                                    if(regular.equals("Irregular-1"))
                                    {
                                        batchInt = batchInt + 1;
                                        batchQuery = batchInt + "_"+id;
                                    }

                                    if(regular.equals("Irregular-2"))
                                    {
                                        batchInt = batchInt + 2;
                                        batchQuery = batchInt + "_"+id;
                                    }





                                    HashMap<String, Object> profileMapVerified = new HashMap<>();
                                    profileMapVerified.put("FullName", fullName);
                                    profileMapVerified.put("ID", id);
                                    profileMapVerified.put("Batch", batch);
                                    profileMapVerified.put("Session", session);
                                    profileMapVerified.put("Phone", phone);
                                    profileMapVerified.put("Guardian Phone", guardianPhone);
                                    profileMapVerified.put("Address", address);
                                    profileMapVerified.put("Email", email);
                                    profileMapVerified.put("Password", password);
                                    profileMapVerified.put("Type", type);
                                    profileMapVerified.put("Level", level);
                                    profileMapVerified.put("Dept", dept);
                                    profileMapVerified.put("ImageUrl", String.valueOf(uri));

                                    profileMapVerified.put("Regularity", regular);
                                    profileMapVerified.put("BatchQuery", batchQuery);

                                    RootRef.child("VerifiedStudents").child(currentUserID).updateChildren(profileMapVerified)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                       // Toast.makeText(StudentProfileUpdate.this, "Verified Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        String message = task.getException().toString();
                                                        Toast.makeText(StudentProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            });

                            //Toast.makeText(StudentProfileUpdate.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            String msg = task.getException().toString();
                            Toast.makeText(StudentProfileUpdate.this, "Error: " +msg, Toast.LENGTH_SHORT).show();
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
                profileMap.put("Regularity",regular);


                RootRef.child("UsersVerified").child(currentUserID).updateChildren(profileMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                   // Toast.makeText(StudentProfileUpdate.this, "Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    String message = task.getException().toString();
                                    Toast.makeText(StudentProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                currentUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                       // Toast.makeText(StudentProfileUpdate.this, "Email Update Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(StudentProfileUpdate.this, "Email Update Failed"+e, Toast.LENGTH_SHORT).show();
                    }
                });


                currentUser.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused)
                    {
                       // Toast.makeText(StudentProfileUpdate.this, "Password Update Successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(StudentProfileUpdate.this, "Password Update Failed"+e, Toast.LENGTH_SHORT).show();
                    }
                });


                //Verified Students
                batchQuery = batch+"_"+id;

                batchInt = Integer.parseInt(batch);

                if(regular.equals("Irregular-1"))
                {
                    batchInt = batchInt + 1;
                    batchQuery = batchInt + "_"+id;
                }

                if(regular.equals("Irregular-2"))
                {
                    batchInt = batchInt + 2;
                    batchQuery = batchInt + "_"+id;
                }




                HashMap<String, Object> profileMapVerified2 = new HashMap<>();
                profileMapVerified2.put("FullName", fullName);
                profileMapVerified2.put("ID", id);
                profileMapVerified2.put("Batch", batch);
                profileMapVerified2.put("Session", session);
                profileMapVerified2.put("Phone", phone);
                profileMapVerified2.put("Guardian Phone", guardianPhone);
                profileMapVerified2.put("Address", address);
                profileMapVerified2.put("Email", email);
                profileMapVerified2.put("Password", password);
                profileMapVerified2.put("Type", type);
                profileMapVerified2.put("Level", level);
                profileMapVerified2.put("Dept", dept);
                profileMapVerified2.put("ImageUrl", image);

                profileMapVerified2.put("Regularity",regular);
                profileMapVerified2.put("BatchQuery", batchQuery);

                RootRef.child("VerifiedStudents").child(currentUserID).updateChildren(profileMapVerified2)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if(task.isSuccessful())
                                {
                                   // Toast.makeText(StudentProfileUpdate.this, "Verified Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    String message = task.getException().toString();
                                    Toast.makeText(StudentProfileUpdate.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                //SendToStudentProfile();

            }

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
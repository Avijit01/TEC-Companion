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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignupTeacher extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button createAccountButton;
    private CircleImageView userImage;
    private EditText userFullName, userPhone, userCurrentAddress, userEmail, userPassword, userDept;
    private String fullName, id, batch, session, regular, phone, guardianPhone, currentAddress, email, password, userType, level, dept;
    private TextView alreadyHaveAnAccount;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserID;

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
        setContentView(R.layout.activity_signup_teacher);

        mToolbar = (Toolbar) findViewById(R.id.SignupTeacher_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Registration");

        mAuth = FirebaseAuth.getInstance();
        //currentUserID = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("User Profile Images");

        InitializeFields();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPick);
            }
        });

        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateNewAccount();
            }
        });

    }

    private void InitializeFields()
    {
        createAccountButton = (Button) findViewById(R.id.SignupTeacher_CreateAccountButtonId);

        userFullName = (EditText) findViewById(R.id.SignupTeacher_FullNameId);
        userPhone = (EditText) findViewById(R.id.SignupTeacher_PhoneId);
        userCurrentAddress = (EditText) findViewById(R.id.SignupTeacher_CurrentAddressId);
        userEmail = (EditText) findViewById(R.id.SignupTeacher_EmailId);
        userPassword = (EditText) findViewById(R.id.SignupTeacher_PasswordId);
        userDept = (EditText) findViewById(R.id.SignupTeacher_DeptId);

        alreadyHaveAnAccount = (TextView) findViewById(R.id.SignupTeacher_alreadyHaveAnAccountId);
        loadingBar = new ProgressDialog(this);
        userImage = (CircleImageView) findViewById(R.id.SignupTeacher_circularImageTeacherId);
    }

    private void CreateNewAccount()
    {
        fullName = userFullName.getText().toString();
        id = "";
        batch = "";
        session = "";
        phone = userPhone.getText().toString();
        guardianPhone = "";
        currentAddress = userCurrentAddress.getText().toString();
        email = userEmail.getText().toString();
        password = userPassword.getText().toString();
        userType = "Teacher";
        level = "0";
        regular = "";
        dept = userDept.getText().toString();

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


        if(TextUtils.isEmpty(currentAddress))
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


        if(flag == 0)
        {
            Toast.makeText(this, "Please choose your profile Image", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                currentUserID = mAuth.getCurrentUser().getUid();
                                rootRef.child("Users").child(currentUserID).setValue("");
                                dataStore = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

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
                                                    HashMap<String, String> profileMap = new HashMap<>();
                                                    profileMap.put("UID", currentUserID);
                                                    profileMap.put("FullName", fullName);
                                                    profileMap.put("ID", id);
                                                    profileMap.put("Batch", batch);
                                                    profileMap.put("Session", session);
                                                    profileMap.put("Phone", phone);
                                                    profileMap.put("Guardian Phone", guardianPhone);
                                                    profileMap.put("Address", currentAddress);
                                                    profileMap.put("Email", email);
                                                    profileMap.put("Password", password);
                                                    profileMap.put("Type", userType);
                                                    profileMap.put("Level", level);
                                                    profileMap.put("Dept", dept);
                                                    profileMap.put("ImageUrl", String.valueOf(uri));

                                                    profileMap.put("Regularity",regular);

                                                    dataStore.setValue(profileMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if(task.isSuccessful())
                                                                    {
                                                                        Toast.makeText(SignupTeacher.this, "Database Updated Successfully", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    else
                                                                    {
                                                                        String message = task.getException().toString();
                                                                        Toast.makeText(SignupTeacher.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            });

                                            Toast.makeText(SignupTeacher.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        }

                                        else
                                        {
                                            String msg = task.getException().toString();
                                            Toast.makeText(SignupTeacher.this, "Error: " +msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                /**/



/*
                                //Image Store
                                StorageReference filePath = userProfileImagesRef.child(currentUserID + ".jpg");
                                filePath.putFile(userImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(Signup.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                            /*
                                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri)
                                                {
                                                    //DatabaseReference imageStore = rootRef.child("Users").child(currentUserID);

                                                    HashMap<String,String> hashMap = new HashMap<>();
                                                    hashMap.put("ImageUri", String.valueOf(uri));

                                                    rootRef.child("Users").child(currentUserID).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused)
                                                        {
                                                            Toast.makeText(Signup.this, "Image Finally Stored", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            });

                                        }

                                        else
                                        {
                                            String msg = task.getException().toString();
                                            Toast.makeText(Signup.this, "Error: " +msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                //image store
                      */

                                SendUserToMainActivity();
                                Toast.makeText(SignupTeacher.this, "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(SignupTeacher.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
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
            userImage.setImageURI(userImageUri);

            /*
            if(resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();
                String currentUserID = mAuth.getCurrentUser().getUid();
                StorageReference filePath = userProfileImageRef.child(currentUserID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Signup.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(Signup.this, "Error"+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            */
        }

    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(SignupTeacher.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SignupTeacher.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
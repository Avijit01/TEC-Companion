package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AdminLogin extends AppCompatActivity {

    private Button adminLoginBtn, adminAddUserBtn;
    private EditText adminUserNameEdit, adminPasswordEdit;
    private ProgressDialog loadingBar;

    private DatabaseReference root;
    private DatabaseReference dataStore;
    DatabaseReference admin;
    String adminUserNameStr,adminPasswordStr;
    String username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        root = FirebaseDatabase.getInstance().getReference();

        Initializations();


        adminAddUserBtn.setVisibility(View.INVISIBLE);
        adminAddUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CreateAdminAccount();
            }
        });

        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AdminLoginMethod();
            }
        });
    }


    private void Initializations()
    {
        adminLoginBtn = (Button) findViewById(R.id.AdminLogin_LoginButtonId);
        adminAddUserBtn = (Button) findViewById(R.id.AdminLogin_AddUserButtonId);
        adminUserNameEdit = (EditText) findViewById(R.id.AdminLogin_UserNameId);
        adminPasswordEdit = (EditText) findViewById(R.id.AdminLogin_PasswordId);
        loadingBar = new ProgressDialog(this);

    }

    private void CreateAdminAccount()
    {

        String adminUserNameStr = adminUserNameEdit.getText().toString();
        String adminPasswordStr = adminPasswordEdit.getText().toString();

        if(TextUtils.isEmpty(adminUserNameStr))
        {
            Toast.makeText(this, "Please enter Username", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(adminPasswordStr))
        {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("Creating New Admin Account");
            loadingBar.setMessage("Please wait, while we are creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            root.child("Admin").child(adminUserNameStr).setValue("");
            dataStore = FirebaseDatabase.getInstance().getReference().child("Admin").child(adminUserNameStr);

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("Username", adminUserNameStr);
            hashMap.put("Password", adminPasswordStr);

            dataStore.setValue(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(AdminLogin.this, "Database Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(AdminLogin.this, "Error: " +message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            loadingBar.dismiss();

        }

    }

    private void AdminLoginMethod()
    {
        adminUserNameStr = adminUserNameEdit.getText().toString();
        adminPasswordStr = adminPasswordEdit.getText().toString();



        if(TextUtils.isEmpty(adminUserNameStr))
        {
            Toast.makeText(AdminLogin.this, "Please enter Username", Toast.LENGTH_SHORT).show();
        }

        if(TextUtils.isEmpty(adminPasswordStr))
        {
            Toast.makeText(AdminLogin.this, "Please enter Password", Toast.LENGTH_SHORT).show();
        }


        else
        {
            admin = FirebaseDatabase.getInstance().getReference().child("Admin").child(adminUserNameStr);

            admin.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    username = String.valueOf(snapshot.child("Username").getValue());

                    if(username.equals(adminUserNameStr))
                    {
                        admin.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                password = String.valueOf(snapshot.child("Password").getValue());

                                if(password.equals(adminPasswordStr))
                                {
                                    SendToAdminHome();
                                }

                                else
                                {
                                    Toast.makeText(AdminLogin.this, "Enter Correct Password.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });
                    }

                    else
                    {
                        Toast.makeText(AdminLogin.this, "Enter Correct Username", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

        }
    }


    private void SendToAdminHome()
    {
        Intent adminHomeIntent = new Intent(AdminLogin.this, AdminHome.class);
        adminHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(adminHomeIntent);
        finish();
    }
}
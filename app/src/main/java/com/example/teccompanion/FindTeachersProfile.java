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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindTeachersProfile extends AppCompatActivity
{
    private Toolbar mToolbar;
    private TextView txtTeacherFullName, txtTeacherDept,txtTeacherPhone, txtTeacherAddress, txtTeacherEmail ;
    private Button sendButton, rejectButton;
    private CircleImageView profileImageView;
    private String imageSend, receivedUserID, senderUserID, current_State;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private DatabaseReference RootRef, ContactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teachers_profile);

        mToolbar = (Toolbar) findViewById(R.id.findTeachersProfile_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Teacher Profile");

        current_State = "new";
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        senderUserID = currentUser.getUid();

        RootRef = FirebaseDatabase.getInstance().getReference();
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        receivedUserID = getIntent().getExtras().get("visit_teacher_id").toString();

        //Toast.makeText(FindTeachersProfile.this, "UID: "+receivedUserID, Toast.LENGTH_SHORT).show();

        InitializeFields();
        RetrieveTeacherInfo();

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToImageFullScreen();
            }
        });
    }

    private void InitializeFields()
    {
        txtTeacherFullName = (TextView)  findViewById(R.id.findTeachersProfile_FullNameId);
        txtTeacherDept = (TextView)  findViewById(R.id.findTeachersProfile_DeptId);
        txtTeacherPhone = (TextView)  findViewById(R.id.findTeachersProfile_PhoneNoId);
        txtTeacherAddress = (TextView)  findViewById(R.id.findTeachersProfile_AddressId);
        txtTeacherEmail = (TextView)  findViewById(R.id.findTeachersProfile_EmailId);

        profileImageView = (CircleImageView) findViewById(R.id.findTeachersProfile_ImageId);

        sendButton = (Button) findViewById(R.id.findTeachersProfile_RequestButtonId);
        rejectButton = (Button) findViewById(R.id.findTeachersProfile_RejectButtonId);
    }


    private void RetrieveTeacherInfo()
    {
        RootRef.child("UsersVerified").child(receivedUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if((snapshot.exists()) && (snapshot.hasChild("FullName")))
                        {
                            String retrieveFullName = snapshot.child("FullName").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();
                            String retrieveDept = snapshot.child("Dept").getValue().toString();
                            String retrievePhone = snapshot.child("Phone").getValue().toString();
                            String retrieveEmail = snapshot.child("Email").getValue().toString();
                            String retrieveAddress = snapshot.child("Address").getValue().toString();

                            imageSend = retrieveProfileImage;

                            txtTeacherFullName.setText(retrieveFullName);
                            txtTeacherDept.setText(retrieveDept);
                            txtTeacherAddress.setText(retrieveAddress);
                            txtTeacherPhone.setText(retrievePhone);
                            txtTeacherEmail.setText(retrieveEmail);

                            Picasso.get().load(retrieveProfileImage).into(profileImageView);

                            ManageChatRequests();
                        }
                        else
                        {
                            Toast.makeText(FindTeachersProfile.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void ManageChatRequests()
    {
        RootRef.child("ChatRequests").child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.hasChild(receivedUserID))
                        {
                            String request_type = snapshot.child(receivedUserID).child("request_type").getValue().toString();

                            if(request_type.equals("sent"))
                            {
                                current_State = "request_sent";
                                sendButton.setText("Unsend Request");
                            }

                            else if(request_type.equals("received"))
                            {
                                current_State = "request_received";
                                sendButton.setText("Accept Request");

                                rejectButton.setVisibility(View.VISIBLE);
                                rejectButton.setEnabled(true);

                                rejectButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        CancelChatRequest();
                                    }
                                });

                            }
                        }

                        else
                        {
                            ContactsRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if(snapshot.hasChild(receivedUserID))
                                            {
                                                current_State = "friends";
                                                sendButton.setText("Remove Contact");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if(!senderUserID.equals(receivedUserID))
        {
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    sendButton.setEnabled(false);

                    if(current_State.equals("new"))
                    {
                        SendChatRequest();
                    }

                    if(current_State.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }

                    if(current_State.equals("request_received"))
                    {
                        AcceptRequest();
                    }

                    if(current_State.equals("friends"))
                    {
                        RemoveContact();
                    }
                }
            });
        }

        else
        {
            sendButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveContact()
    {
        ContactsRef.child(senderUserID).child(receivedUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            ContactsRef.child(receivedUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendButton.setEnabled(true);
                                                current_State = "new";
                                                sendButton.setText("Send Request");

                                                rejectButton.setVisibility(View.INVISIBLE);
                                                rejectButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void AcceptRequest()
    {
        ContactsRef.child(senderUserID).child(receivedUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            ContactsRef.child(receivedUserID).child(senderUserID)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                RootRef.child("ChatRequests").child(senderUserID).child(receivedUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    RootRef.child("ChatRequests").child(receivedUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    sendButton.setEnabled(true);
                                                                                    current_State = "friends";
                                                                                    sendButton.setText("Remove Contact");

                                                                                    rejectButton.setVisibility(View.INVISIBLE);
                                                                                    rejectButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void CancelChatRequest()
    {
        RootRef.child("ChatRequests").child(senderUserID).child(receivedUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            RootRef.child("ChatRequests").child(receivedUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendButton.setEnabled(true);
                                                current_State = "new";
                                                sendButton.setText("Send Request");

                                                rejectButton.setVisibility(View.INVISIBLE);
                                                rejectButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


    private void SendChatRequest()
    {
        RootRef.child("ChatRequests").child(senderUserID).child(receivedUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            RootRef.child("ChatRequests").child(receivedUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                sendButton.setEnabled(true);
                                                current_State = "request_sent";
                                                sendButton.setText("Unsend Request");
                                            }
                                        }
                                    });
                        }
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
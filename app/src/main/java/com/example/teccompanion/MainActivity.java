package com.example.teccompanion;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView profileImage;
    private TextView userFullName;

    private CardView studentListCardView, teacherListCardView, chatCardView, documentCardView;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;

    private String typeForButton;
    private ProgressDialog loadingBar;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.mainActivity_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TEC Companion");

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        profileImage = (CircleImageView) findViewById(R.id.mainActivity_ImageStudentId);
        userFullName = (TextView)  findViewById(R.id.mainActivity_StudentFullName);


        RetrieveUser();

        studentListCardView = (CardView) findViewById(R.id.studentListButtonMainActivityId);
        teacherListCardView = (CardView) findViewById(R.id.teacherListButtonMainActivityId);
        chatCardView = (CardView) findViewById(R.id.chatButtonMainActivityId);
        documentCardView = (CardView) findViewById(R.id.documentButtonMainId);



        studentListCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentListActivity();
            }
        });

        teacherListCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherListActivity();
            }
        });

        chatCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity();
            }
        });

        documentCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(typeForButton.equals("Student"))
                    StudentProfileActivity();

                if(typeForButton.equals("Teacher"))
                    TeacherProfileActivity();
            }
        });

        userFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(typeForButton.equals("Student"))
                    StudentProfileActivity();

                if(typeForButton.equals("Teacher"))
                    TeacherProfileActivity();
            }
        });
    }

    @Override
    protected void onStart()
    {
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        super.onStart();
        state = "online";
        Checking();

    }

/*
    @Override
    protected void onStop()
    {
        super.onStop();

        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();

        if(currentUser != null)
        {
            state = "offline";
            UpdateUserStatus();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();

        if(currentUser != null)
        {
            state = "offline";
            UpdateUserStatus();
        }
    }

*/


    private void TeacherListActivity()
    {
        Intent teacherListIntent = new Intent(this, TeacherList.class);
        startActivity(teacherListIntent);
    }


    private void StudentListActivity()
    {
        Intent studentListIntent = new Intent(this, StudentList.class);
        startActivity(studentListIntent);
    }


    private void RetrieveUser()
    {
        RootRef.child("UsersVerified").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            String retrieveFullName = snapshot.child("FullName").getValue().toString();
                            String retrieveProfileImage = snapshot.child("ImageUrl").getValue().toString();
                            typeForButton = snapshot.child("Type").getValue().toString();

                            userFullName.setText(retrieveFullName);
                            Picasso.get().load(retrieveProfileImage).into(profileImage);
                            loadingBar.dismiss();
                        }
                        else
                        {
                            //Toast.makeText(MainActivity.this, "Please Set Your Profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void StudentProfileActivity()
    {
        Intent studentProfileIntent = new Intent(this, StudentProfile.class);
        startActivity(studentProfileIntent);
    }

    private void TeacherProfileActivity()
    {
        Intent teacherProfileIntent = new Intent(this, TeacherProfile.class);
        startActivity(teacherProfileIntent);
    }


    private void ChatActivity()
    {
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);

    }





    private void Checking()
    {
        RootRef.child("UsersVerified").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            UpdateUserStatus();
                            loadingBar.dismiss();
                        }

                        else
                        {
                            mAuth.signOut();
                            Intent WaitingIntent = new Intent(MainActivity.this, Waiting.class);
                            startActivity(WaitingIntent);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(this, Login.class);
        startActivity(loginIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menuLogOutId)
        {
            mAuth.signOut();
            SendUserToLoginActivity();
            finish();
            state = "offline";
            UpdateUserStatus();
        }


        return true;
    }

    private void UpdateUserStatus()
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);


        RootRef.child("UsersVerified").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);
    }

}
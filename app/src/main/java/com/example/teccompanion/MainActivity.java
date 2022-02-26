package com.example.teccompanion;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView profileImage;
    private TextView userFullName;

    private CardView chatCardView, documentCardView;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.mainActivity_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TEC Companion");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        profileImage = (CircleImageView) findViewById(R.id.mainActivity_ImageStudentId);
        userFullName = (TextView)  findViewById(R.id.mainActivity_StudentFullName);


        RetrieveUser();

        chatCardView = (CardView) findViewById(R.id.chatButtonMainActivityId);
        documentCardView = (CardView) findViewById(R.id.documentButtonMainId);

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
            public void onClick(View v) {
                StudentProfileActivity();
            }
        });

        userFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentProfileActivity();
            }
        });
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

                            userFullName.setText(retrieveFullName);
                            Picasso.get().load(retrieveProfileImage).into(profileImage);
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


    public void ChatActivity()
    {
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);

    }


    @Override
    protected void onStart()
    {
        super.onStart();
        Checking();
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
                            //Toast.makeText(MainActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
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
        }


        return true;
    }

}
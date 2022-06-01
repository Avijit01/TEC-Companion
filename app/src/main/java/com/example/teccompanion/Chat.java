package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Chat extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID, state = "offline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar) findViewById(R.id.chat_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        myViewPager = (ViewPager) findViewById(R.id.chat_tabsPagerId);
        myTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.chat_tabLayoutId);
        myTabLayout.setupWithViewPager(myViewPager);

    }

    /*
    @Override
    protected void onStart()
    {
        super.onStart();
        state = "online";
        UpdateUserStatus();
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        if(currentUser != null)
        {
            state = "offline";
            UpdateUserStatus();
            currentUserID = "";
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(currentUser != null)
        {
            state = "offline";
            UpdateUserStatus();
            currentUserID = "";
        }
    }

     */




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.chatMenuTeachersId)
        {
            SendToFindTeachers();
        }

        if(item.getItemId() == R.id.chatMenuStudentsId)
        {
            SendToFindStudents();
        }

        if(item.getItemId() == R.id.chatMenuLogOutId)
        {
            state = "offline";
            UpdateUserStatus();
            mAuth.signOut();
            SendUserToLoginActivity();
            finish();
            currentUserID = "";
        }


        return true;
    }

    private void SendToFindTeachers()
    {
        Intent findTeachersIntent = new Intent(this, FindTeachers.class);
        startActivity(findTeachersIntent);
    }


    private void SendToFindStudents()
    {
        Intent findStudentsIntent = new Intent(this, FindStudents.class);
        startActivity(findStudentsIntent);
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent = new Intent(this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
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
        state = "";
    }

}
package com.example.teccompanion;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CardView chatCardView, documentCardView;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();



        mToolbar = (Toolbar) findViewById(R.id.mainActivity_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("TEC Companion");

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
    }



    public void ChatActivity()
    {
        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);
        //Toast.makeText(this, "user"+ currentUser, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Email: "+ currentUser.getEmail().toString(), Toast.LENGTH_SHORT).show();
    }

/*
    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser == null)
        {
            SendUserToLoginActivity();
        }
    }
*/

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
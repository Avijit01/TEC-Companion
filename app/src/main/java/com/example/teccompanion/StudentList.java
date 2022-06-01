package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentList extends AppCompatActivity
{
    Button allStudentsBtn, batch1Btn, batch2Btn, batch3Btn, batch4Btn, batch5Btn, batch6Btn, batch7Btn, batch8Btn, batch9Btn, batch10Btn, batch11Btn, batch12Btn;
    private Toolbar mToolbar;
    private String batchNo;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        mToolbar = (Toolbar) findViewById(R.id.studentList_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Student List");

        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        allStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToAllStudents();
            }
        });

        BatchButtonVisibility();
        BatchButtonTasks();
    }

    private void BatchButtonVisibility()
    {
        RootRef.child("Batch").child("batch-7")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            batch7Btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        RootRef.child("Batch").child("batch-8")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            batch8Btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        RootRef.child("Batch").child("batch-9")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            batch9Btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        RootRef.child("Batch").child("batch-10")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            batch10Btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        RootRef.child("Batch").child("batch-11")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            batch11Btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        RootRef.child("Batch").child("batch-12")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.exists())
                        {
                            batch12Btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void InitializeFields()
    {
        allStudentsBtn = (Button) findViewById(R.id.studentList_AllButtonId);
        batch1Btn = (Button) findViewById(R.id.studentList_Batch1ButtonId);
        batch2Btn = (Button) findViewById(R.id.studentList_Batch2ButtonId);
        batch3Btn = (Button) findViewById(R.id.studentList_Batch3ButtonId);
        batch4Btn = (Button) findViewById(R.id.studentList_Batch4ButtonId);
        batch5Btn = (Button) findViewById(R.id.studentList_Batch5ButtonId);
        batch6Btn = (Button) findViewById(R.id.studentList_Batch6ButtonId);
        batch7Btn = (Button) findViewById(R.id.studentList_Batch7ButtonId);
        batch8Btn = (Button) findViewById(R.id.studentList_Batch8ButtonId);
        batch9Btn = (Button) findViewById(R.id.studentList_Batch9ButtonId);
        batch10Btn = (Button) findViewById(R.id.studentList_Batch10ButtonId);
        batch11Btn = (Button) findViewById(R.id.studentList_Batch11ButtonId);
        batch12Btn = (Button) findViewById(R.id.studentList_Batch12ButtonId);
    }



    private void SendToAllStudents()
    {
        Intent allStudentsIntent = new Intent(this, AllStudents.class);
        startActivity(allStudentsIntent);
    }

    private void SendToBatchList()
    {
        Intent batchListIntent = new Intent(this, BatchList.class);
        batchListIntent.putExtra("get_batch_no",batchNo);
        startActivity(batchListIntent);
    }

    private void BatchButtonTasks()
    {
        batch1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "1";
                SendToBatchList();
            }
        });

        batch2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "2";
                SendToBatchList();
            }
        });

        batch3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "3";
                SendToBatchList();
            }
        });

        batch4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "4";
                SendToBatchList();
            }
        });

        batch5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "5";
                SendToBatchList();
            }
        });

        batch6Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "6";
                SendToBatchList();
            }
        });

        batch7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "7";
                SendToBatchList();
            }
        });

        batch8Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "8";
                SendToBatchList();
            }
        });

        batch9Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "9";
                SendToBatchList();
            }
        });

        batch10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "10";
                SendToBatchList();
            }
        });

        batch11Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "11";
                SendToBatchList();
            }
        });

        batch12Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                batchNo = "12";
                SendToBatchList();
            }
        });
    }
}
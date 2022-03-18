package com.example.teccompanion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentList extends AppCompatActivity
{
    Button allStudentsBtn, batch1Btn, batch2Btn, batch3Btn, batch4Btn, batch5Btn, batch6Btn, batch7Btn, batch8Btn, batch9Btn, batch10Btn;
    private Toolbar mToolbar;
    private String batchNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        mToolbar = (Toolbar) findViewById(R.id.studentList_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Student List");

        InitializeFields();

        allStudentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToAllStudents();
            }
        });

        BatchButtonTasks();
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
    }
}
package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class BatchList extends AppCompatActivity
{
    private Toolbar mToolbar;
    RecyclerView recView;
    String receiveBatchNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_list);

        receiveBatchNo = getIntent().getExtras().get("get_batch_no").toString();

        mToolbar = (Toolbar) findViewById(R.id.batchList_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Batch - "+receiveBatchNo);

        recView = (RecyclerView) findViewById(R.id.recycler_BatchListId);
        recView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ModelStudentVerify> options =
                new FirebaseRecyclerOptions.Builder<ModelStudentVerify>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("UsersVerified"), ModelStudentVerify.class)
                        .build();
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<ModelStudentVerify> options =
                new FirebaseRecyclerOptions.Builder<ModelStudentVerify>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("UsersVerified").orderByChild("Batch").equalTo(""+receiveBatchNo), ModelStudentVerify.class)
                        .build();


        FirebaseRecyclerAdapter<ModelStudentVerify, BatchList.BatchListViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelStudentVerify, BatchList.BatchListViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull BatchList.BatchListViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ModelStudentVerify model)
                    {
                        holder.fullName.setText(model.getFullName());
                        holder.Id.setText(model.getID());
                        holder.Session.setText(model.getSession());
                        Glide.with(holder.img.getContext()).load(model.getImageUrl()).into(holder.img);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                String visit_student_id = getRef(position).getKey();

                                Intent batchListProfileIntent = new Intent(BatchList.this, BatchListProfile.class);
                                batchListProfileIntent.putExtra("visit_student_id",visit_student_id);
                                startActivity(batchListProfileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public BatchList.BatchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
                        BatchList.BatchListViewHolder viewHolder = new BatchList.BatchListViewHolder(view);
                        return viewHolder;
                    }
                };

        recView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class BatchListViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView fullName, Id, Session;

        public BatchListViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.singleRow_ImageId);
            fullName = (TextView) itemView.findViewById(R.id.singleRow_FullNameId);
            Id = (TextView) itemView.findViewById(R.id.singleRow_IDId);
            Session = (TextView) itemView.findViewById(R.id.singleRow_SessionId);
        }
    }
}
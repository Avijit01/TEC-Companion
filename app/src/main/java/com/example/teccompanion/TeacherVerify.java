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

public class TeacherVerify extends AppCompatActivity
{
    private Toolbar mToolbar;
    RecyclerView recView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_verify);

        mToolbar = (Toolbar) findViewById(R.id.teacherVerify_toolbarId);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Verify Teachers");

        recView = (RecyclerView) findViewById(R.id.recycler_TeacherVerifyId);
        recView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<ModelTeacherVerify> options =
                new FirebaseRecyclerOptions.Builder<ModelTeacherVerify>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users"), ModelTeacherVerify.class)
                        .build();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<ModelTeacherVerify> options =
                new FirebaseRecyclerOptions.Builder<ModelTeacherVerify>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("Type").equalTo("Teacher"), ModelTeacherVerify.class)
                        .build();


        FirebaseRecyclerAdapter<ModelTeacherVerify, TeacherVerify.TeacherVerifyViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelTeacherVerify, TeacherVerify.TeacherVerifyViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull TeacherVerify.TeacherVerifyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ModelTeacherVerify model)
                    {
                        holder.fullName.setText(model.getFullName());
                        holder.dept.setText(model.getDept());
                        Glide.with(holder.img.getContext()).load(model.getImageUrl()).into(holder.img);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                String visit_teacher_id = getRef(position).getKey();

                                Intent profileIntent = new Intent(TeacherVerify.this, TeacherVerifyProfile.class);
                                profileIntent.putExtra("visit_teacher_id",visit_teacher_id);
                                startActivity(profileIntent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public TeacherVerify.TeacherVerifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_teacher, parent, false);
                        TeacherVerify.TeacherVerifyViewHolder viewHolder = new TeacherVerify.TeacherVerifyViewHolder(view);
                        return viewHolder;
                    }
                };

        recView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class TeacherVerifyViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView fullName, dept;

        public TeacherVerifyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.singleRowTeacher_ImageId);
            fullName = (TextView) itemView.findViewById(R.id.singleRowTeacher_FullNameId);
            dept = (TextView) itemView.findViewById(R.id.singleRowTeacher_DeptId);
        }
    }
}
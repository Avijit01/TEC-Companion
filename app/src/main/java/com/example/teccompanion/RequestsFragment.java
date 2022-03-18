package com.example.teccompanion;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment
{
    private View RequestsFragmentView;
    private RecyclerView recView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId, typeForSent, typeForSent2;
    private DatabaseReference ChatRequestsRef, UsersVerified, ContactsRef;

    public RequestsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);

        recView = (RecyclerView) RequestsFragmentView.findViewById(R.id.recycler_RequestsFragmentId);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("ChatRequests");
        UsersVerified = FirebaseDatabase.getInstance().getReference().child("UsersVerified");
        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        return RequestsFragmentView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions <ModelContacts> options =
                new FirebaseRecyclerOptions.Builder<ModelContacts>()
                        .setQuery(ChatRequestsRef.child(currentUserId), ModelContacts.class)
                        .build();

        FirebaseRecyclerAdapter<ModelContacts, RequestsViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelContacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ModelContacts model)
                    {
                        holder.itemView.findViewById(R.id.singleRowContacts_AcceptButtonId).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.singleRowContacts_RejectButtonId).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef = getRef(position).child("request_type").getRef();


                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.exists())
                                {
                                    String type = snapshot.getValue().toString();


                                    if(type.equals("sent"))
                                    {
                                        holder.rejectButton.setVisibility(View.INVISIBLE);
                                        holder.acceptButton.setText("Sent");


                                        UsersVerified.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.hasChild("ImageUrl"))
                                                {

                                                    String requestUserName = snapshot.child("FullName").getValue().toString();
                                                    String requestType = snapshot.child("Type").getValue().toString();
                                                    String requestImage = snapshot.child("ImageUrl").getValue().toString();

                                                    typeForSent2 = requestType;

                                                    holder.fullName.setText(requestUserName);
                                                    holder.type.setText(requestType);
                                                    Picasso.get().load(requestImage).into(holder.img);

                                                }



                                                if(typeForSent2.equals("Teacher"))
                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {

                                                            Intent profileIntent = new Intent(getContext(), FindTeachersProfile.class);
                                                            profileIntent.putExtra("visit_teacher_id",list_user_id);
                                                            startActivity(profileIntent);

                                                    }
                                                });

                                                if(typeForSent2.equals("Student"))
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v)
                                                        {

                                                            Intent profileIntent = new Intent(getContext(), FindStudentsProfile.class);
                                                            profileIntent.putExtra("visit_student_id",list_user_id);
                                                            startActivity(profileIntent);

                                                        }
                                                    });



                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }


                                    if(type.equals("received"))
                                    {

                                        UsersVerified.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.hasChild("ImageUrl"))
                                                {

                                                    String requestUserName = snapshot.child("FullName").getValue().toString();
                                                    String requestType = snapshot.child("Type").getValue().toString();
                                                    String requestImage = snapshot.child("ImageUrl").getValue().toString();

                                                    typeForSent = requestType;

                                                    holder.fullName.setText(requestUserName);
                                                    holder.type.setText(requestType);
                                                    Picasso.get().load(requestImage).into(holder.img);

                                                }


                                                if(typeForSent.equals("Teacher"))
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v)
                                                        {

                                                            Intent profileIntent = new Intent(getContext(), FindTeachersProfile.class);
                                                            profileIntent.putExtra("visit_teacher_id",list_user_id);
                                                            startActivity(profileIntent);

                                                        }
                                                    });

                                                if(typeForSent.equals("Student"))
                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v)
                                                        {

                                                            Intent profileIntent = new Intent(getContext(), FindStudentsProfile.class);
                                                            profileIntent.putExtra("visit_student_id",list_user_id);
                                                            startActivity(profileIntent);

                                                        }
                                                    });


                                                holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        ContactsRef.child(currentUserId).child(list_user_id).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    ContactsRef.child(list_user_id).child(currentUserId).child("Contacts").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if(task.isSuccessful())
                                                                            {
                                                                                ChatRequestsRef.child(currentUserId).child(list_user_id)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {
                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    ChatRequestsRef.child(list_user_id).child(currentUserId)
                                                                                                            .removeValue()
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                                                {
                                                                                                                    if(task.isSuccessful())
                                                                                                                    {
                                                                                                                        Toast.makeText(getContext(), "Contact Saved", Toast.LENGTH_SHORT).show();
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
                                                            }
                                                        });
                                                    }
                                                });

                                                holder.rejectButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        ChatRequestsRef.child(currentUserId).child(list_user_id)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                    {
                                                                        if(task.isSuccessful())
                                                                        {
                                                                            ChatRequestsRef.child(list_user_id).child(currentUserId)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            if(task.isSuccessful())
                                                                                            {
                                                                                                Toast.makeText(getContext(), "Request Removed", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_contacts,parent,false);
                        RequestsViewHolder viewHolder = new RequestsViewHolder(view);
                        return viewHolder;
                    }
                };

        recView.setAdapter(adapter);
        adapter.startListening();

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView fullName, type;
        Button acceptButton, rejectButton;

        public RequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.singleRowContacts_ImageId);
            fullName = (TextView) itemView.findViewById(R.id.singleRowContacts_FullNameId);
            type = (TextView) itemView.findViewById(R.id.singleRowContacts_TypeId);
            acceptButton = (Button) itemView.findViewById(R.id.singleRowContacts_AcceptButtonId);
            rejectButton = (Button) itemView.findViewById(R.id.singleRowContacts_RejectButtonId);
        }
    }


}
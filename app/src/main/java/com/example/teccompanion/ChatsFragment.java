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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment
{
    private  View chatsView;
    private RecyclerView recView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId;
    private DatabaseReference RootRef, ChatsRef, UsersVerified;

    String contactType;


    public ChatsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         chatsView =  inflater.inflate(R.layout.fragment_chats, container, false);

        recView = (RecyclerView) chatsView.findViewById(R.id.recycler_ChatsFragment);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        RootRef = FirebaseDatabase.getInstance().getReference();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        UsersVerified = FirebaseDatabase.getInstance().getReference().child("UsersVerified");

        return chatsView;
    }


    @Override
    public void onStart()
    {
        super.onStart();


        FirebaseRecyclerOptions<ModelContacts> options =
                new FirebaseRecyclerOptions.Builder<ModelContacts>()
                        .setQuery(ChatsRef, ModelContacts.class)
                        .build();


        FirebaseRecyclerAdapter<ModelContacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelContacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ChatsViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ModelContacts model)
                    {
                        final String userIDs = getRef(position).getKey();
                        final String[] profileImage = {"default_image"};
                        final String[] contactFullName = {""};

                        UsersVerified.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.exists())
                                {
                                    if(snapshot.hasChild("ImageUrl"))
                                    {
                                        profileImage[0] = snapshot.child("ImageUrl").getValue().toString();
                                        contactFullName[0] = snapshot.child("FullName").getValue().toString();
                                        contactType = snapshot.child("Type").getValue().toString();

                                        holder.fullName.setText(contactFullName[0]);
                                        Picasso.get().load(profileImage[0]).into(holder.img);

                                        if(snapshot.child("userState").hasChild("state"))
                                        {
                                            String state = snapshot.child("userState").child("state").getValue().toString();
                                            String time = snapshot.child("userState").child("time").getValue().toString();
                                            String date = snapshot.child("userState").child("date").getValue().toString();

                                            if(state.equals("online"))
                                            {
                                                holder.type.setText("online");
                                            }

                                            else if(state.equals("offline"))
                                            {
                                                holder.type.setText("Last Seen: "+ date +" " + time);
                                            }
                                        }

                                        else
                                        {
                                            holder.type.setText("offline");
                                        }

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Intent chatIntent = new Intent(getContext(), ChatSection.class);
                                                chatIntent.putExtra("visit_user_id", userIDs);
                                                chatIntent.putExtra("visit_user_name", contactFullName[0]);
                                                chatIntent.putExtra("visit_user_image", profileImage[0]);
                                                startActivity(chatIntent);
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
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_contacts,parent,false);
                        ChatsViewHolder viewHolder = new ChatsViewHolder(view);
                        return viewHolder;
                    }
                };

        recView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView fullName, type;

        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.singleRowContacts_ImageId);
            fullName = (TextView) itemView.findViewById(R.id.singleRowContacts_FullNameId);
            type = (TextView) itemView.findViewById(R.id.singleRowContacts_TypeId);
        }
    }

}
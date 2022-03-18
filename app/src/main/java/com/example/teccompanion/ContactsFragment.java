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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment
{
    private  View ContactsView;
    private RecyclerView recView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId;
    private DatabaseReference ContactsRef, UsersVerified;


    public ContactsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ContactsView =  inflater.inflate(R.layout.fragment_contacts, container, false);

        recView = (RecyclerView) ContactsView.findViewById(R.id.recycler_FragmentContactsId);
        recView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        UsersVerified = FirebaseDatabase.getInstance().getReference().child("UsersVerified");

        return ContactsView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<ModelContacts>()
                        .setQuery(ContactsRef, ModelContacts.class)
                        .build();


        FirebaseRecyclerAdapter<ModelContacts, ContactsFragmentViewHolder> adapter =
                new FirebaseRecyclerAdapter<ModelContacts, ContactsFragmentViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ContactsFragmentViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ModelContacts model)
                    {
                        final String userIDs = getRef(position).getKey();

                        UsersVerified.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.exists())
                                {

                                    if(snapshot.hasChild("ImageUrl")) {

                                        if (snapshot.child("userState").hasChild("state")) {
                                            String state = snapshot.child("userState").child("state").getValue().toString();
                                            String time = snapshot.child("userState").child("time").getValue().toString();
                                            String date = snapshot.child("userState").child("date").getValue().toString();

                                            if (state.equals("online")) {
                                                holder.onlineIcon.setVisibility(View.VISIBLE);
                                            } else if (state.equals("offline")) {
                                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                                            }
                                        } else {
                                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                                        }
                                    }



                                    if(snapshot.hasChild("ImageUrl"))
                                    {
                                        String profileImage = snapshot.child("ImageUrl").getValue().toString();
                                        String contactFullName = snapshot.child("FullName").getValue().toString();
                                        String contactType = snapshot.child("Type").getValue().toString();

                                        holder.fullName.setText(contactFullName);
                                        holder.type.setText(contactType);
                                        Picasso.get().load(profileImage).into(holder.img);
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
                    public ContactsFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_contacts,parent,false);
                        ContactsFragmentViewHolder viewHolder = new ContactsFragmentViewHolder(view);
                        return viewHolder;
                    }
                };

        recView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class ContactsFragmentViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView img;
        TextView fullName, type;
        ImageView onlineIcon;

        public ContactsFragmentViewHolder(@NonNull View itemView)
        {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.singleRowContacts_ImageId);
            fullName = (TextView) itemView.findViewById(R.id.singleRowContacts_FullNameId);
            type = (TextView) itemView.findViewById(R.id.singleRowContacts_TypeId);
            onlineIcon = (ImageView) itemView.findViewById(R.id.singleRowContacts_OnlineId);
        }
    }

}
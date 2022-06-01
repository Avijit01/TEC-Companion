package com.example.teccompanion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPic, messageReceiverPic;

        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_messages_textId);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_messages_textId);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_imageId);
            messageSenderPic = (ImageView) itemView.findViewById(R.id.sender_messages_ImageId);
            messageReceiverPic = (ImageView) itemView.findViewById(R.id.receiver_messages_ImageId);
        }
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent,false);

        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("UsersVerified").child(fromUserID);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.hasChild("ImageUrl"))
                {
                    String receiverImage = snapshot.child("ImageUrl").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.avatar).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.messageSenderPic.setVisibility(View.GONE);
        holder.messageReceiverPic.setVisibility(View.GONE);


        if(fromMessageType.equals("text"))
        {

            if(fromUserID.equals(messageSenderID))
            {
                holder.senderMessageText.setVisibility(View.VISIBLE);

                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }

            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setVisibility(View.VISIBLE);

                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
            }
        }

        else if(fromMessageType.equals("image"))
        {
            if(fromUserID.equals(messageSenderID))
            {
                holder.messageSenderPic.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageSenderPic);
            }
            else
            {
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPic.setVisibility(View.VISIBLE);

                Picasso.get().load(messages.getMessage()).into(holder.messageReceiverPic);
            }
        }

    }



    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

}

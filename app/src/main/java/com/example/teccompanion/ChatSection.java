package com.example.teccompanion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatSection extends AppCompatActivity
{
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID, state = "online";

    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    private ImageButton sendMessageButton, sendFilesButton;
    private EditText messageInputText;

    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    private String saveCurrentTime, saveCurrentDate;
    private String checker ="", myUrl = "";
    private StorageTask uploadTask;
    private Uri fileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_section);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        messageSenderID = currentUser.getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_user_image").toString();


        Initializations();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.avatar).into(userImage);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });



        DisplayLastSeen();


        sendFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CharSequence options[] = new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "Ms Word Files"
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatSection.this);
                builder.setTitle("Select the File");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(which == 0)
                        {
                            checker = "image";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent, "Select image"),438);
                        }

                        if(which == 1)
                        {
                            checker = "pdf";
                        }

                        if(which == 2)
                        {
                            checker = "docx";
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void Initializations()
    {

        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.chatSection_toolbarId);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)  this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userImage = (CircleImageView)  findViewById(R.id.custom_chat_bar_ProfileImageId);
        userName = (TextView)  findViewById(R.id.custom_chat_bar_ProfileNameId);
        userLastSeen = (TextView)  findViewById(R.id.custom_chat_bar_LastSeenId);

        sendMessageButton = (ImageButton) findViewById(R.id.chatSection_SendButtonId);
        sendFilesButton = (ImageButton) findViewById(R.id.chatSection_FileButtonId);
        messageInputText = (EditText) findViewById(R.id.input_messageId);

        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.recycler_ChatSectionId);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);

        loadingBar = new ProgressDialog(this);


        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 438 && resultCode == RESULT_OK &&  data != null && data.getData()!=null)
        {

            loadingBar.setTitle("Sending File");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            fileUri = data.getData();

            if(!checker.equals("image"))
            {

            }

            else if (checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");

                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                final String messagePushID = userMessageKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

                uploadTask = filePath.putFile(fileUri);

                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();


                            Map messagePicBody = new HashMap();
                            messagePicBody.put("message", myUrl);
                            messagePicBody.put("name", fileUri.getLastPathSegment());
                            messagePicBody.put("type", checker);
                            messagePicBody.put("from", messageSenderID);

                            messagePicBody.put("to", messageReceiverID);
                            messagePicBody.put("messageID", messagePushID);
                            messagePicBody.put("time", saveCurrentTime);
                            messagePicBody.put("date", saveCurrentDate);

                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messagePicBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messagePicBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatSection.this, "Message sent successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatSection.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                    messageInputText.setText("");
                                }
                            });
                        }
                    }
                });

            }

            else
            {
                loadingBar.dismiss();
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayLastSeen()
    {
        RootRef.child("UsersVerified").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if(snapshot.hasChild("ImageUrl")) {

                            if (snapshot.child("userState").hasChild("state")) {
                                String state = snapshot.child("userState").child("state").getValue().toString();
                                String time = snapshot.child("userState").child("time").getValue().toString();
                                String date = snapshot.child("userState").child("date").getValue().toString();

                                if (state.equals("online")) {
                                    userLastSeen.setText("online now");
                                } else if (state.equals("offline")) {
                                    userLastSeen.setText("Last Seen: " + date + " " + time);
                                }
                            } else {
                                userLastSeen.setText("offline");
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    @Override
    protected void onStart()
    {
        super.onStart();




        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
                    {
                        Messages messages = snapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }



    private void SendMessage()
    {
        String messageText = messageInputText.getText().toString();

        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "First write your message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            messageTextBody.put("to", messageReceiverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatSection.this, "Message sent successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else 
                    {
                        Toast.makeText(ChatSection.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });

        }
    }
}
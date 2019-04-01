package com.example.haniup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ProgressDialog mProgress ;
    private static final int CAMERA_REQUEST_CODE = 1;
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

    private RecyclerView mChat , mMedia ;
    private RecyclerView.Adapter mChatAdapter , mMediaAdapter ;
    private RecyclerView.LayoutManager mChatLayoutManger , mMediaLayoutManger;

    ArrayList<MessageObject> messageList ;

    ChatObject mChatObject ;
    DatabaseReference mChatMessageDb ;
    Button mAddMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject") ;

        mChatMessageDb = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("messages");


        Button mSend = findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        mAddMedia = findViewById(R.id.addMedia);
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OPenGallery();
            }
        });







        initializaMessage();
        initializaMedia();
        getChatMessage();
    }



    private void getChatMessage() {

        mChatMessageDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){

                    String text = "" ,
                            creatorID = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();

                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();

                    if (dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();

                    if(dataSnapshot.child("media").getChildrenCount() > 0)
                        for (DataSnapshot mediaSnapShot : dataSnapshot.child("media").getChildren())
                            mediaUrlList.add(mediaSnapShot.getValue().toString());
                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey() , creatorID , text , mediaUrlList );

                    messageList.add(mMessage);
                    mChatLayoutManger.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    int totalMediaUpload = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();

    EditText mMessage;

    private void SendMessage() {

        mMessage = findViewById(R.id.Message);
        String messageId = mChatMessageDb.push().getKey();

        final DatabaseReference newMessageDb = mChatMessageDb.child(messageId);

        final  Map newMessageMap = new HashMap<>();

        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());


        if(!mMessage.getText().toString().isEmpty())


            newMessageMap.put("text", mMessage.getText().toString());

            if (!mediaUriList.isEmpty()) {

                for (String mediaUri : mediaUriList) {

                    String mediaId = newMessageDb.child("media").push().getKey();

                    mediaIdList.add(mediaId);

                    final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);

                    UploadTask uploadTask = filepath.putFile(Uri.parse(mediaUri));

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override
                                public void onSuccess(Uri uri) {

                                    newMessageMap.put("/media/" + mediaIdList.get(totalMediaUpload) + "/",uri.toString());

                                    totalMediaUpload++;

                                    if (totalMediaUpload == mediaUriList.size())

                                        UpdateDatabaseWithNewMessage(newMessageDb, newMessageMap);

                                }
                            });
                        }
                    });
                }
            } else {
                if (!mMessage.getText().toString().isEmpty())

                    UpdateDatabaseWithNewMessage(newMessageDb , newMessageMap);


        }
    }

    private void UpdateDatabaseWithNewMessage(DatabaseReference newMessageDb , Map newMessageMap){

        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        totalMediaUpload=0 ;
        mMediaAdapter.notifyDataSetChanged();

        String message;

        if (newMessageMap.get("text") != null)
            message = newMessageMap.get("text").toString();

        else

            message = "Sent Media";

        for (UserObject mUser : mChatObject.getUserObjectsArrayList()){

            if (!mUser.getUid().equals(FirebaseAuth.getInstance().getUid())){

                new SendNotification(message, "New Message", mUser.getNotificationKey());

            }
        }
    }

    private void  initializaMessage() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManger = new LinearLayoutManager(getApplicationContext() , LinearLayout.VERTICAL , false);
        mChat.setLayoutManager(mChatLayoutManger);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }

    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();
    private void initializaMedia() {

        mediaUriList = new ArrayList<>();
        mMedia = findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManger = new LinearLayoutManager(getApplicationContext() , LinearLayout.HORIZONTAL , false);
        mMedia.setLayoutManager(mMediaLayoutManger);
        mMediaAdapter = new MediaAdapter(getApplicationContext() , mediaUriList);
        mMedia.setAdapter(mMediaAdapter);

    }

    private void OPenGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE , true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE_INTENT);
    }

    protected void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK){
            if (requestCode == PICK_IMAGE_INTENT){
                if (data.getClipData() == null){

                    mediaUriList.add(data.getData().toString());
                }else
                {
                    for (int i=0 ; i<data.getClipData().getItemCount(); i++){

                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }

                }
                mMediaAdapter.notifyDataSetChanged();
            }
        }




    }


}

package com.example.haniup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Camera;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {





    ArrayList<MessageObject> messageList ;

    public MessageAdapter(ArrayList<MessageObject> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View LayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);

        RecyclerView.LayoutParams Ip = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutView.setLayoutParams(Ip);

        MessageViewHolder rcv = new MessageViewHolder(LayoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageViewHolder holder , final int position) {
holder.mMessage.setText(messageList.get(position).getMessage());
holder.mSender.setText(messageList.get(position).getSenderId());

if (messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty())
    holder.mViewMedia.setVisibility(View.GONE);
//Image
holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        new ImageViewer.Builder(v.getContext(),messageList.get(holder.getAdapterPosition()).getMediaUrlList()).setStartPosition(0).show();

    
    
    }
});
        if (messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty())
            holder.mUpload.setVisibility(View.GONE);
//Image
        holder.mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(),messageList.get(holder.getAdapterPosition()).getMediaUrlList()).setStartPosition(0).show();


                Intent intent =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);



            }
        });


    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView mMessage , mSender;
        LinearLayout mLayout;

        Button mViewMedia;
        Button mUpload;

        public MessageViewHolder(@NonNull View item) {
            super(item);

            mLayout = item.findViewById(R.id.layout);
        mMessage = item.findViewById(R.id.message);
        mSender = item.findViewById(R.id.sender);
        mViewMedia = item.findViewById(R.id.viewMedia);
        mUpload = item.findViewById(R.id.addImage);


        }
    }

}

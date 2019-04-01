package com.example.haniup;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObject> userList;


    public UserListAdapter(ArrayList<UserObject> userList) {

        this.userList = userList;
    }


    @Override
    public UserListViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View LayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);

        RecyclerView.LayoutParams Ip = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutView.setLayoutParams(Ip);

        UserListViewHolder rcv = new UserListViewHolder(LayoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserListViewHolder holder, final int position) {

        holder.mName.setText(userList.get(position).getName());
        holder.mPhone.setText(userList.get(position).getPhone());
        holder.mlayout.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                CreateChat(holder.getAdapterPosition());

            }
        });
        holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                userList.get(holder.getAdapterPosition()).setSelected(isChecked);
            }
        });

    }

   private void CreateChat(int position){
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        HashMap newChatMap = new HashMap();

        newChatMap.put("id", key);
        newChatMap.put("/users" + FirebaseAuth.getInstance().getUid(), true);
        newChatMap.put("/users" + userList.get(position).getUid(), true);

        DatabaseReference ChatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        ChatInfoDb.updateChildren(newChatMap);


        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");
        userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        userDb.child(userList.get(position).getUid()).child("chat").child(key).setValue(true);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
//(userList == null) ? userList.size() : 0




    public class UserListViewHolder extends RecyclerView.ViewHolder {

        public TextView mName, mPhone;
        public LinearLayout mlayout ;
        public CheckBox mAdd;

        public UserListViewHolder(View item) {
            super(item);
            mName = item.findViewById(R.id.name);
            mPhone = item.findViewById(R.id.phone);
            mAdd = item.findViewById(R.id.ADD);
            mlayout = item.findViewById(R.id.layout);
        }
    }
}

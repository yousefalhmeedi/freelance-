package com.example.haniup;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class FindUserActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManger;

    ArrayList<UserObject> userList , ContactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);



        userList = new ArrayList<>();
        ContactList = new ArrayList<>();

        Button mCreate = findViewById(R.id.create);
        mCreate.setOnClickListener(new View.OnClickListener() {
         @Override
        public void onClick(View v) {
         CreateChat();
       }
        });

        initializeRecyclerView();
        getContactList();
    }

    private void getContactList() {
        String IsoPrefix = getCountryISO();

        Cursor Phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null ,null,null ,null);
        while (Phones.moveToNext()){

            String name = Phones.getString(Phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = Phones.getString(Phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


            phone = phone.replace(" ","");
            phone = phone.replace("-","");
            phone = phone.replace("(","");
            phone = phone.replace(")","");

            if (!String.valueOf(phone.charAt(0)).equals("+"))
                phone = IsoPrefix + phone ;


            UserObject mContact = new UserObject( "" , name , phone);
            userList.add(mContact);
            //userList
          //  mUserListAdapter.notifyDataSetChanged();
            getUserDetails(mContact);
        }

    }




    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");

        Query queue = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
         queue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String phone = "" , name = "";

                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){

                        if (childSnapshot.child("phone").getValue() != null)
                            phone = childSnapshot.child("phone").getValue().toString();

                        if (childSnapshot.child("name").getValue() != null)
                            name = childSnapshot.child("name").getValue().toString();

                        UserObject mUser = new UserObject(childSnapshot.getKey() , name , phone);

                      if (name.equals(phone))
                          for (UserObject mcontactIterator : ContactList){
                              if (mcontactIterator.getPhone().equals(mUser.getPhone())){
                                  mUser.setName(mcontactIterator.getName());
                              }
                          }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
    private String getCountryISO(){

        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null )
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);



    }
    private void initializeRecyclerView() {
        //  userList = new ArrayList<>();
        mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManger = new LinearLayoutManager(getApplicationContext() , LinearLayout.VERTICAL , false);
        mUserList.setLayoutManager(mUserListLayoutManger);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);

    }
    private void CreateChat(){
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();


        DatabaseReference ChatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");


        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("user");


        HashMap newChatMap = new HashMap();


        newChatMap.put("id", key);
        newChatMap.put("/users" + FirebaseAuth.getInstance().getUid(), true);

        Boolean ValidChat = false;
        for (UserObject mUser : userList){
            if (mUser.getSelected()){
                ValidChat = true;
                newChatMap.put("/users" + mUser.getUid(), true);
                userDb.child(mUser.getUid()).child("chat").child(key).setValue(true);
            }
        }
        if (ValidChat){

            ChatInfoDb.updateChildren(newChatMap);
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);


        }






    }


}
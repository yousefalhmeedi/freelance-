package com.example.haniup;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {

    private String chatId;

    private ArrayList<UserObject> userObjectsArrayList = new ArrayList<>();

    public ChatObject(String chatId) {
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public ArrayList<UserObject> getUserObjectsArrayList(){
        return userObjectsArrayList;
    }

    public void addUserToArrayList(UserObject mUser){
        userObjectsArrayList.add(mUser);
    }
}

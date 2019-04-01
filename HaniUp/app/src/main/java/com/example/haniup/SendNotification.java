package com.example.haniup;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class SendNotification {

    public SendNotification(String message , String heading , String notificationKey){



        try {
            JSONObject notificationContent = new JSONObject(
                    "{'contents':{'en':'" + message + "'},"+
                    "'include_player_ids':['" + notificationKey + "']," +
                    "'headings':{'en': '" + heading + "'}}");
            OneSignal.postNotification(notificationContent , null);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
//  notificationKey = "a751f0fb-b2ef-4974-8b07-fcc6bf92d016";
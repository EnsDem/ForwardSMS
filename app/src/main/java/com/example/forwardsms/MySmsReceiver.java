package com.example.forwardsms;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MySmsReceiver extends BroadcastReceiver {

    DatabaseHelper DB;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle=intent.getExtras();
            SmsMessage[] msgs;
            String msg_from;


            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from=msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        Toast.makeText(context,"From: " + msg_from + ", Message: " +msgBody, Toast.LENGTH_SHORT).show();

                        String phoneNo= msg_from.trim();
                        String SMS = msgBody.trim();

                        MainActivity.forwardSMS(phoneNo, SMS, context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
package com.example.javasendsms;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    private static final String TAG =
            MyReceiver.class.getSimpleName();
    public static final String pdu_type = "pdus";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            // Retrieve the SMS Messages received

            Object[] sms = (Object[]) bundle.get("pdus");

            // For every SMS message received
            for (int i=0; i < sms.length; i++) {
                // Convert Object array
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody().toString();
                Log.d(TAG, "onReceive: " + message);
                //Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
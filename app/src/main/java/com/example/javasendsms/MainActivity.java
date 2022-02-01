package com.example.javasendsms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText phonenumber, message;
    Button send;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        send = findViewById(R.id.btnSend);
        phonenumber = findViewById(R.id.etNumber);
        message = findViewById(R.id.etMessage);
        Context context = this;
        send.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View view) {


/////////////
                if (isSimExists()) {
                    String number = phonenumber.getText().toString();
                    String msg = message.getText().toString();
                    try {
                        //SmsManager smsManager = SmsManager.getDefault();
                        //smsManager = SmsManager.getSmsManagerForSubscriptionId(1);
                        //Log.d("msgForSubscriptionId", "Create event called with name: " + smsManager.getSubscriptionId());
                        //smsManager.sendTextMessage(number, null, msg, sentPI, deliveredPI);
                        sendSMS(number, msg);

                        //get the subs id
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        String SIM_STATE = telephonyManager.getLine1Number();
                        Log.v("simId", ""+SIM_STATE);
                        //

                        SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);
                        List localList = localSubscriptionManager.getActiveSubscriptionInfoList();

                        Log.d("localList", ""+localList.toString());
                        //

                        Toast.makeText(getApplicationContext(),"Message Sent",Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(),SIM_STATE,Toast.LENGTH_LONG).show();
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"TryCatchError: "+e,Toast.LENGTH_LONG).show();
                        Log.v("TryCatchError", ""+e);
                        Toast.makeText(getApplicationContext(),"Some fields are Empty",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"No sim card found",Toast.LENGTH_LONG).show();
                }
            }
            @RequiresApi(api = Build.VERSION_CODES.M)
            public boolean isSimExists() {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                int SIM_STATE = telephonyManager.getSimState();

                if (SIM_STATE == TelephonyManager.SIM_STATE_READY)
                    return true;
                else {
                    // we can inform user about sim state
                    switch (SIM_STATE) {
                        case TelephonyManager.SIM_STATE_ABSENT:
                        case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                        case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                        case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                        case TelephonyManager.SIM_STATE_UNKNOWN:
                            break;
                    }
                    return false;
                }
            }//

            //
            private void sendSMS(String phoneNumber, String message)
            {
                String SENT = "SMS_SENT";
                String DELIVERED = "SMS_DELIVERED";
                PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
                PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,new Intent(DELIVERED), 0);



// ---when the SMS has been sent---
                context.registerReceiver(
                        new BroadcastReceiver()
                        {

                            @Override
                            public void onReceive(Context arg0,Intent arg1)
                            {
                                Log.d("SMS", "RESULT CODE" + getResultCode());
                                switch(getResultCode())
                                {
                                    case Activity.RESULT_OK:
                                        Log.d("SMS", "MESSAJE OK");
                                        break;
                                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                        Log.d("SMS", "MESSAJE ERROR GENERIC");
                                        break;
                                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                                        Log.d("SMS", "MESSAGE NO SERVICE");
                                        break;
                                    case SmsManager.RESULT_ERROR_NULL_PDU:
                                        Log.d("SMS", "NULL PDU");
                                        break;
                                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                                        Log.d("SMS", "RADIO OFF");
                                        break;
                                    default:
                                        Log.d("SMS", "ERROR SENDING THE FUCKING MESSAGE");
                                }
                            }
                        }, new IntentFilter(SENT));
                // ---when the SMS has been delivered---
                context.registerReceiver(
                        new BroadcastReceiver()
                        {

                            @Override
                            public void onReceive(Context arg0,Intent arg1)
                            {
                                if (getResultCode() == Activity.RESULT_OK){
                                    Log.d("SMS SENT", "LOOP IF, SUCCESS SENDING THE MESSAGE");
                                }
                                else {
                                    Log.d("ERROR", "ERROR SENDING THE MESSAGE");
                                }
                                switch(getResultCode())
                                {
                                    case Activity.RESULT_OK:
                                        Log.d("SMS SENT", "SUCCESS SENDING THE MESSAGE");
                                        break;
                                    case Activity.RESULT_CANCELED:
                                        Log.d("SMS SENT", "ERROR SENDING THE MESSAGE");
                                        break;
                                }
                            }
                        }, new IntentFilter(DELIVERED));


                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumber, null,message,sentPI, deliveredPI);
            }

        });

    }
}

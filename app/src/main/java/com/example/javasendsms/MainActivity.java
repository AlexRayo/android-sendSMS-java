package com.example.javasendsms;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.btnSend);
        phonenumber = findViewById(R.id.etNumber);
        message = findViewById(R.id.etMessage);
        send.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(View view) {
                if (isSimExists()) {
                    String number = phonenumber.getText().toString();
                    String msg = message.getText().toString();
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager = SmsManager.getSmsManagerForSubscriptionId(1);
                        Log.d("msgForSubscriptionId", "Create event called with name: " + smsManager.getSubscriptionId());
                        smsManager.sendTextMessage(number, "+50585000130", msg, null, null);

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

        });
    }
}
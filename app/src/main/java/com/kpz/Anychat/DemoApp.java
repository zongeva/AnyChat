package com.kpz.Anychat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.model.AuthService;
import com.vrv.imsdk.model.SDKClient;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Admin on 12-Dec-2017.
 */


public class DemoApp extends Application{

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String init_bool = "";

    @Override
    public void onCreate() {
        super.onCreate();

        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        init_bool = prefs.getString("init_bool", "");

        if(init_bool.equals("true")){
            boolean init = VIMClient.init(this, "com.kpz.AnyChat");
            if (!init){
                Log.e("UCC Log", "Code: 1101001 SDK failed to initial");

            }
            else {
                Log.e("UCC Log", "Code: 1101002 SDK successfully initial");
                SDKClient defaultClient = ClientManager.getDefault();
                final AuthService authService = defaultClient.getAuthService();

                if (defaultClient != null) {
                    defaultClient = ClientManager.getDefault();
                    VIMClient.registerReceiver(this);
                } else {
                    Toast.makeText(DemoApp.this, "Fail To Initialize SDK", Toast.LENGTH_SHORT).show();
                }

                VIMClient.registerReceiver(this);
            }
        }
        else if(init_bool.equals("")){
            Intent intent = new Intent(DemoApp.this, Request_Permission.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }
}

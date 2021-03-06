package com.kpz.AnyChat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.kpz.AnyChat.Others.RequestHelper;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.model.AuthService;
import com.vrv.imsdk.model.SDKClient;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Admin on 05-Jan-2018.
 */

public class DemoApp extends Application {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String init_bool = "";

    @Override
    public void onCreate() {
        super.onCreate();

        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        init_bool = prefs.getString("init_bool", "");

        if (init_bool.equals("true")) {
            boolean init = VIMClient.init(this, "com.kpz.AnyChat");
            if (!init) {
                Log.e("Log", "SDK failed to initialize");

            } else {
                Log.e("Log", "SDK successfully initialize");
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
        } else if (init_bool.equals("")) {
            Intent intent = new Intent(DemoApp.this, Request_Permission.class);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) DemoApp.this.getSystemService(Context.TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String deviceId = telephonyManager.getDeviceId();
        String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
        SharedPreferences prefs1 = DemoApp.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String shared_login_password = prefs1.getString("shared_login_password", "");//"No name defined" is the default value.
    }
}

package com.kpz.AnyChat.ProfileSetting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Account;

/**
 * Created by user on 10/3/2017.
 */

public class Profile_Setting_ChgGender extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Context context = this;
    String gender;
    TextView textView66;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_setgender);
        setTitle("Change Gender");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton2.setImageResource(R.drawable.maleselected);
        imageButton.setImageResource(R.drawable.femaleunselect);
        imageButton2.setImageResource(R.drawable.maleunselect);

        TextView textView66 = (TextView)findViewById(R.id.textView66);

        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());

        String url_setpw = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 3, url_setpw, id, textView66, deviceId, "", "","");
//        gt.execute();
        String selected =   textView66.getText().toString();

    }

    public void btn_select_sex(View view) {

        //Unknow =0
        //Male = 1
        //Female = 2
        Toast.makeText(context, "Gender Change Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,Profile_Setting_New.class);
        finish();
        startActivity(intent);
    }
    public void skipgender(View view) {
        Account account = new Account();
        account.setID(RequestHelper.getAccountInfo().getID());
        gender = "Unknown";
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton2.setImageResource(R.drawable.maleunselect);
        imageButton.setImageResource(R.drawable.femaleunselect);
        /*
        * UCC Server Gender
        * */
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String url_setpw = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 4, url_setpw, id, textView66, deviceId, gender, "","");
//        gt.execute();
        /*
        * Linkdood Server Gender
        * */
        account.setGender(0);
        Toast.makeText(context, "You Choose Unknown", Toast.LENGTH_SHORT).show();
        RequestHelper.updateAccountInfo(account, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {
                Log.e("Gender","Successfulyl Changed");
            }
        });



    }

    public void female_selected(View view) {
        Account account = new Account();
        account.setID(RequestHelper.getAccountInfo().getID());
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton.setImageResource(R.drawable.female);
        imageButton2.setImageResource(R.drawable.maleunselect);
        gender = "female";
                /*
        * UCC Server Gender
        * */
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String url_setpw = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 4, url_setpw, id, textView66, deviceId, gender, "","");
//        gt.execute();
        /*
        * Linkdood Server Gender
        * */
        account.setGender(1);
        Toast.makeText(context, "You Choose Female", Toast.LENGTH_SHORT).show();
        RequestHelper.updateAccountInfo(account, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {
                Log.e("Gender","Successfulyl Changed");
            }
        });

    }

    public void male_selected(View view) {
        Account account = new Account();
        account.setID(RequestHelper.getAccountInfo().getID());
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imageButton2);
        imageButton2.setImageResource(R.drawable.male);
        imageButton.setImageResource(R.drawable.femaleunselect);
        gender = "male";
        /*
        * UCC Server Gender
        * */
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String url_setpw = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 4, url_setpw, id, textView66, deviceId, gender, "","");
//        gt.execute();
        /*
        * Linkdood Server Gender
        * */
        account.setGender(2);
        Toast.makeText(context, "You Choose Male", Toast.LENGTH_SHORT).show();
        RequestHelper.updateAccountInfo(account, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {
                Log.e("Gender", "Successfulyl Changed");
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Profile_Setting_New.class);
        startActivity(intent);
    }
}

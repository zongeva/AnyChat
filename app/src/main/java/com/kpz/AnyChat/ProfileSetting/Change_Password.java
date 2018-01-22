package com.kpz.AnyChat.ProfileSetting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.linkdood.ucc.Network.Http_GetToken;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;

/**
 * Created by user on 9/28/2017.
 */

public class Change_Password extends AppCompatActivity {
    String oldpas;
    String newpas1;
    String newpas2;
    ProgressDialog pDialog;
    String countryCode, contactNo, verificationCode;
    public static final String MY_PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        setTitle("Change Password");
        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        final EditText oldpw = (EditText) findViewById(R.id.oldpw);
        final EditText newpw1 = (EditText) findViewById(R.id.newpw1);
        final EditText newpw2 = (EditText) findViewById(R.id.newpw2);
        Button changpw = (Button) findViewById(R.id.changepw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



//        countryCode = getIntent().getExtras().getString("countryCode");
//        contactNo = getIntent().getExtras().getString("contactNo");
//        verificationCode = getIntent().getExtras().getString("verificationCode");
        final String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
        final String deviceId = telephonyManager.getDeviceId();
        final String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        final TextView dummy = (TextView) findViewById(R.id.textView75);

        changpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if password are same
                oldpas = oldpw.getText().toString();
                newpas1 = newpw1.getText().toString();
                newpas2 = newpw2.getText().toString();

                if (!oldpas.equals("") && !newpas1.equals("") && !newpas2.equals("")) { // Not empty

                    if (!oldpw.equals(newpas1)) { //old pas NOT equals to new pass

                        if (newpas1.equals(newpas2)) {
                            Toast.makeText(Change_Password.this, "New password cant be same with old password.", Toast.LENGTH_SHORT).show();




                        }else if (!newpas1.equals(newpas2)) {
                            Toast.makeText(Change_Password.this, "New Password Not Match !", Toast.LENGTH_SHORT).show();
                        }


                    } else {//End of old pas NOT equals to new pass
                        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("shared_password", newpas1);
                        editor.commit();

//                        Http_GetToken gt = new Http_GetToken(Change_Password.this, 6, url_token, id, dummy, deviceId, oldpas, Utils.urlencode(newpas1), "");
//                        gt.execute();
                    }

                }//End of not empty
                 else {
                    Toast.makeText(Change_Password.this, "Ops, You Miss Something", Toast.LENGTH_SHORT).show();
                }

                }



        });

//        Intent intent = new Intent(this, Profile_Setting_Personal.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }


    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this,Profile_Setting_New.class);
        startActivity(intent);
        return true;
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Profile_Setting_New.class);
        startActivity(intent);
    }
}

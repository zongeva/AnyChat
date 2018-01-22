package com.kpz.AnyChat.ProfileSetting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hbb20.CountryCodePicker;
import com.kpz.AnyChat.Home_Activity.HomeActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Account;

/**
 * Created by user on 9/28/2017.
 */

public class Profile_Setting_Personal extends AppCompatActivity {
    Context context;
    CountryCodePicker ccp;

    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_profile_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView gender = (TextView) findViewById(R.id.gender);
        TextView whatsup = (TextView) findViewById(R.id.whatsups);
        TextView dob = (TextView) findViewById(R.id.dob);
        TextView region = (TextView) findViewById(R.id.region);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        whatsup.setText(RequestHelper.getAccountInfo().getSign());

        /*
        * Retrieve and show DOB on activity
        * Get From UCC Server
        * */
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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
        String deviceId = telephonyManager.getDeviceId();
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 1, url_token, id, dob, deviceId, "", "", "");
//        gt.execute();

        /*
        * Retrieve and show Gender on activity
        * Get from Linkdood Server since already Sync with UCC Server
        * */
        long genders = RequestHelper.getAccountInfo().getGender();
        if (genders == 0) {
            gender.setText("Unknown");
        } else if (genders == 1) {
            gender.setText("Female");
        } else if (genders == 2) {
            gender.setText("Male");
        }


        /*
        * Load and show Avatar on activity
        * */
        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);

        /*
        * Retrieve Regoin Message
        * */
        String regions = RequestHelper.getAccountInfo().getArea();
        region.setText(regions);


    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView dob = (TextView) findViewById(R.id.dob);
        TextView region = (TextView) findViewById(R.id.region);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);

                /*
        * Load and show Avatar on activity
        * */
        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);


        /*
        * Retrieve Regoin Message
        * */
        String regions = RequestHelper.getAccountInfo().getArea();
        region.setText(regions);


        /*
        * Retrieve and show DOB on activity
        * Get From UCC Server
        * */
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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
        String deviceId = telephonyManager.getDeviceId();
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 1, url_token, id, dob, deviceId, "", "","");
//        gt.execute();

    }

    public void set_avatar(View view) {
        Intent intent = new Intent(this, Change_Avatar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void go_changepw(View view) {
        Intent intent = new Intent(this, Change_Password.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    public void go_set_nick(View view) {
        //Change Nickname
        Toast.makeText(getApplicationContext(), "Change Nickname Feature Temporary Unavailable", Toast.LENGTH_SHORT).show();
    }

    public void go_qr_gen(View view) {
            //QR Code Generation
    }

    public void go_gender(View view) {
        Intent intent = new Intent(this,Profile_Setting_ChgGender.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void go_birthday(View view) {
        Intent intent = new Intent(this,Profile_Setting_Birthday.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void go_selectregion(View view) {
//        new MaterialDialog.Builder(this)
//                .title("Region")
//                .content("Write Something")
//                .inputType(InputType.TYPE_CLASS_TEXT )
//                .inputRangeRes(2, 20, R.color.red500)
//                .input("Ab...", "", new MaterialDialog.InputCallback() {
//                    @Override
//                    public void onInput(MaterialDialog dialog, CharSequence input) {
//                        Account account = new Account();
//                        account.setID(RequestHelper.getAccountInfo().getID());
//                        account.setArea(input.toString());
//
//
//                        RequestHelper.updateAccountInfo(account, new RequestCallBack() {
//                            @Override
//                            public void handleSuccess(Object o, Object o2, Object o3) {
//                                Intent intent = getIntent();
//                                finish();
//                                startActivity(intent);
//                                Log.e("Change Region","Successful");
//                            }
//                        });
//
//                    }
//                }).show();

        Intent intent = new Intent(Profile_Setting_Personal.this, CountryPicker.class);
        startActivity(intent);

//
//        Intent intent = new Intent(this,getIntent());
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);



    }


    public void go_whatsup(View view) {
        new MaterialDialog.Builder(this)
                .title("What's up !")
                .content("Write Something")
                .inputType(InputType.TYPE_CLASS_TEXT )
                .inputRangeRes(2, 20, R.color.red500)
                .input("Ab...", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        Account account = new Account();
                        account.setID(RequestHelper.getAccountInfo().getID());
                        account.setSign(input.toString());
                        RequestHelper.updateAccountInfo(account, new RequestCallBack() {
                            @Override
                            public void handleSuccess(Object o, Object o2, Object o3) {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                                Log.e("Change Sign(Whatsup)","Successful");
                            }
                        });
                    }
                }).show();

//        Intent intent = new Intent(this,Profile_Setting_Personal.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);


    }
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(Profile_Setting_Personal.this, HomeActivity.class);
//        finish();
        startActivity(intent);
        return true;
    }

}

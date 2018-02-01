package com.kpz.AnyChat.ProfileSetting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

//import com.linkdood.ucc.Ads.AdHome;
import com.kpz.AnyChat.Home_Activity.HomeActivity;
import com.kpz.AnyChat.LoginActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.kpz.AnyChat.SearchActivity;
import com.kpz.AnyChat.Utilities_Page;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.model.ResultCallBack;

import java.util.Objects;

/**
 * Created by user on 9/28/2017.
 */

public class Profile_Setting_Home extends AppCompatActivity {
    RadioGroup radioGroup;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    // ME SETTING HOME PAGE
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView me_nick = (TextView) findViewById(R.id.me_nickname);
        TextView me_ld = (TextView) findViewById(R.id.me_linkdood);
        //TextView energy = (TextView)findViewById(R.id.energies);
        ImageView avatar = (ImageView) findViewById(R.id.imageView);
        ImageView mpplus = (ImageView) findViewById(R.id.mpplus);
        ImageView go_profile = (ImageView) findViewById(R.id.go_profile);
        TextView on_energy_click = (TextView)findViewById(R.id.energy);
        Button go_logout = (Button) findViewById(R.id.go_logout);
        ImageView smth = (ImageView) findViewById(R.id.imageView16);

        mpplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Profile_Setting_Home.this, AdHome.class);
//                startActivity(intent);
            }
        });

        setTitle("Me");
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_conversation) {
                    Intent intent = new Intent(Profile_Setting_Home.this, HomeActivity.class);
                    startActivity(intent);
                } else if (checkedId == R.id.rb_search) {
                    Intent intent = new Intent(Profile_Setting_Home.this, SearchActivity.class);
                    startActivity(intent);
                } else if (checkedId == R.id.rb_utilities) {
                    Intent intent = new Intent(Profile_Setting_Home.this, Utilities_Page.class);
                    startActivity(intent);
                } else if (checkedId == R.id.rb_me) {
                    Intent intent = new Intent(Profile_Setting_Home.this, Profile_Setting_Home.class);
                    startActivity(intent);
                }
            }
        });
        //Set Avatar
        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);

        //Set NickName
        String abc = Objects.toString(RequestHelper.getAccountInfo().getID());
        String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + RequestHelper.getAccountInfo().getID();
//        Utils.setNickname(getApplicationContext(), url, abc, me_nick);

        //Set Skill Point from Share Preference
        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        int counts = prefs.getInt("shared_skill_point", 0);
//        me_ld.setText("Skill Point : " + counts);


        //Set Energy From Server

        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
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
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String url_join_group = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(Profile_Setting_Home.this, 9, url_join_group, ids, energy, deviceId,"" ,"", "");
//        gt.execute();

        //Set Skill Point from UCC API Server
        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gts = new Http_GetToken(getApplicationContext(), 13, url_token, ids, me_ld, deviceId, "", "","");
//        gts.execute();

    }

    public void go_profile(View view) {
        Intent intent = new Intent(this, Profile_Setting_Personal.class);
        startActivity(intent);
    }


    public void go_setting(View view) {
        Intent intent = new Intent(this,Profile_Setting_Setting.class);
        startActivity(intent);
    }

    public void go_version(View view) {
//        Toast.makeText(this, "Version Page Under Construction", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void logout(View view) {
        ClientManager.getDefault().getAuthService().logout(new ResultCallBack() {
            @Override
            public void onSuccess(Object o, Object o2, Object o3) {
                SharedPreferences.Editor editor = Profile_Setting_Home.this.getSharedPreferences(MY_PREFS_NAME, Profile_Setting_Home.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();

                Log.e("UCC Log", "Code: " + Utils.osType + "1501001 Logout Success");
                finishAffinity();
                Profile_Setting_Home.this.startActivity(new Intent(Profile_Setting_Home.this, LoginActivity.class));
            }

            @Override
            public void onError(int i, String s) {
                Log.e("UCC Log", "Code: " + Utils.osType + "1501002 Logout Failed");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageView avatar = (ImageView) findViewById(R.id.imageView);

        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);
    }
}

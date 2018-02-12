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
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
//import com.linkdood.ucc.Ads.AdHome;
import com.kpz.AnyChat.Home_Activity.HomeActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Account;

import java.util.Objects;

import static com.kpz.AnyChat.ProfileSetting.Profile_Setting_Home.MY_PREFS_NAME;

/**
 * Created by Lenovo on 19/1/2018.
 */

public class Profile_Setting_New extends AppCompatActivity {
    ImageView avatar,mpplus;
    TextView changepw_container,gender_container,birthday_container,regoin_container,whatsup_container,xp,mp,me_nickname,birthday,region,gender,whatsup;
    int endtime, begintime;
    boolean open;
    Boolean aOpen;
    ToggleButton toggleButton;
    TextView set_begin_tv;
    TextView set_end_tv;
    int realbegin, realend;
    String ampm ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersetting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");
        /*
        * Load Name
        * */
        me_nickname = (TextView)findViewById(R.id.me_nickname);
        String abc = Objects.toString(RequestHelper.getAccountInfo().getID());
        String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + RequestHelper.getAccountInfo().getID();
//        Utils.setNickname(getApplicationContext(), url, abc, me_nickname);

        /*
        * Load Xp and Mp
        * */
        xp = (TextView)findViewById(R.id.xp);
        mp = (TextView)findViewById(R.id.mp);
        final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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
        //Set MP from UCC API Server
        String deviceId = telephonyManager.getDeviceId();
        String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String url_join_group = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(Profile_Setting_New.this, 9, url_join_group, ids, mp, deviceId,"" ,"", "");
//        gt.execute();

        //Set XP from UCC API Server
        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gts = new Http_GetToken(Profile_Setting_New.this, 13, url_token, ids, xp, deviceId, "", "","");
//        gts.execute();


        /*
        * Load and show Avatar on activity
        * */
        avatar = (ImageView)findViewById(R.id.avatar);
        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);

        /*
        * Change Avatar
        * */
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Setting_New.this, Change_Avatar.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });




        /*
        * Go to Change Password Page
        * */
        changepw_container = (TextView)findViewById(R.id.changepw_container);
        changepw_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Setting_New.this, Change_Password.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        /*
        * Retrieve and show Gender on activity
        * Get from Linkdood Server since already Sync with UCC Server
        * */
        gender = (TextView)findViewById(R.id.gender);
        long genders = RequestHelper.getAccountInfo().getGender();
        if (genders == 0) {
            gender.setText("Unknown");
        } else if (genders == 1) {
            gender.setText("Female");
        } else if (genders == 2) {
            gender.setText("Male");
        }

        /*
        * Change Gender
        * */
        gender_container = (TextView)findViewById(R.id.gender_container);
        gender_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Setting_New.this,Profile_Setting_ChgGender.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        /*
        * Retrieve Birthday
        * */
        birthday = (TextView)findViewById(R.id.birthday);
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

        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        String get_dob_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken get_dob = new Http_GetToken(getApplicationContext(), 1, get_dob_token, id, birthday, deviceId, "", "", "");
//        get_dob.execute();



        /*
        Change Birthday
        */
        birthday_container = (TextView)findViewById(R.id.birthday_container);
        birthday_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Setting_New.this,Profile_Setting_Birthday.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        /*
        * Retrieve Region
        * */
        region = (TextView)findViewById(R.id.regoin);
        String regions = RequestHelper.getAccountInfo().getArea();
        region.setText(regions);


        /*
        * Change Region
        * */
        regoin_container = (TextView)findViewById(R.id.regoin_container);
        regoin_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Setting_New.this, CountryPicker.class);
                startActivity(intent);
            }
        });


        /*
        * Retrieve What's up
        * */
        whatsup = (TextView)findViewById(R.id.whatsup);
        whatsup.setText(RequestHelper.getAccountInfo().getSign());

        /*
        * Change What's Up
        * */
        whatsup_container = (TextView)findViewById(R.id.whatsup_container);
        whatsup_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(Profile_Setting_New.this)
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

            }
        });

        mpplus = (ImageView)findViewById(R.id.mpplus);
        mpplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Profile_Setting_New.this, AdHome.class);
//                startActivity(intent);
            }
        });

        /*
        Global Mute Notification
        */
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        RequestHelper.getGlobalNoDisturbMode(new RequestCallBack<Integer, Integer, Boolean>() {
            @Override
            public void handleSuccess(Integer integer, Integer integer2, Boolean aBoolean) {
                open = aBoolean;
                Log.e("OnOpen ", open+"");

                if(open == true){
                    toggleButton.setChecked(true);
                    toggleButton.isChecked();
                }
                else {
                    if(toggleButton.isChecked()){
                        toggleButton.setChecked(false);

                    }

                }
            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggleButton.isChecked()){
                    toggleButton.setChecked(true);
                    toggleButton.setActivated(true);
                    RequestHelper.setGlobalNoDisturbMode(0, 0, true, new RequestCallBack() {
                        @Override
                        public void handleSuccess(Object o, Object o2, Object o3) {
                            Toast.makeText(Profile_Setting_New.this, "Do Not Disturb Enabled", Toast.LENGTH_SHORT).show();

                        }
                    });

                    RequestHelper.getGlobalNoDisturbMode(new RequestCallBack<Integer, Integer, Boolean>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, Boolean aBoolean) {
                            Log.e("Set1 Begin ", integer+"");
                            Log.e("Set1 End ", integer2+"");
                            Log.e("Set1 Open ", aBoolean+"");

                        }
                    });

                }
                else if (!toggleButton.isChecked()){
                    toggleButton.setActivated(false);
                    RequestHelper.setGlobalNoDisturbMode(0, 0, false, new RequestCallBack() {
                        @Override
                        public void handleSuccess(Object o, Object o2, Object o3) {
                            Toast.makeText(Profile_Setting_New.this, "Do Not Disturb Disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestHelper.getGlobalNoDisturbMode(new RequestCallBack<Integer, Integer, Boolean>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, Boolean aBoolean) {
                            Log.e("Set2 Begin ", integer+"");
                            Log.e("Set2 End ", integer2+"");
                            Log.e("Set2 Open ", aBoolean+"");
                        }
                    });

                }
            }
        });

    }




    @Override
    protected void onStart() {
        super.onStart();
         birthday = (TextView) findViewById(R.id.dob);
         region = (TextView) findViewById(R.id.region);
         avatar = (ImageView) findViewById(R.id.avatar);

                /*
        * Load and show Avatar on activity
        * */
        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);


        /*
        * Retrieve Regoin Message
        * */
        region = (TextView)findViewById(R.id.regoin);
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
//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 1, url_token, id, birthday, deviceId, "", "","");
//        gt.execute();

    }

    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(Profile_Setting_New.this, HomeActivity.class);
//        finish();
        startActivity(intent);
        return true;
    }
}

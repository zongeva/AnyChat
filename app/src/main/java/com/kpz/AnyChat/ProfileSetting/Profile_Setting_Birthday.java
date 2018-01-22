package com.kpz.AnyChat.ProfileSetting;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

//import com.linkdood.ucc.Network.Http_GetToken;
import com.kpz.AnyChat.Others.*;
import com.kpz.AnyChat.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 10/3/2017.
 */

public class Profile_Setting_Birthday extends AppCompatActivity {
    Context context = this;
    private TextView dateView, birthday;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private int years, months, days;

    String date;
    final Calendar cf = Calendar.getInstance();
    String shared_login_password;
    String value;
    TextView token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_birthday);
        setTitle("Change Birthday");
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        dateView = (TextView) findViewById(R.id.textView3);
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.

        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;

//        Http_GetToken gt = new Http_GetToken(getApplicationContext(), 1, url_token, id, dateView, deviceId, "", "","");
//        gt.execute();


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
//        showDialog(999);
        // TODO Auto-generated method stub
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(Profile_Setting_Birthday.this, datePickerListener,
                cf.get(Calendar.YEAR),
                cf.get(Calendar.MONTH),
                cf.get(Calendar.DAY_OF_MONTH)
                );
        dialog.setTitle("Select Birthday");
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.YEAR, -16 ); // Subtract 6 months
        long minDate = c.getTime().getTime() ;// Twice!
        dialog.getDatePicker().setMaxDate(minDate);
        dialog.show();


    }


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int months,
                              int days) {
            // TODO Auto-generated method stub
            cf.set(Calendar.YEAR, year);
            cf.set(Calendar.MONTH, months);
            cf.set(Calendar.DAY_OF_MONTH, days);


                dateView.setText(new StringBuilder()
                    .append(year)
                    .append("-")
                    .append(months + 1)
                    .append("-").append(days)
            );
        }

    };


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 999:

                return new DatePickerDialog(this, datePickerListener, years, months,
                        days);
        }
        return null;
    }


    public void btn_select_bitrhday(View view) {

        date = dateView.getText().toString();
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
        String url_setpw = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//        Http_GetToken gt = new Http_GetToken(Profile_Setting_Birthday.this, 2, url_setpw, id, dateView, deviceId, date, "","");
//        gt.execute();
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

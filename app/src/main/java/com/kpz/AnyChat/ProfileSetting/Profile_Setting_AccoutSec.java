package com.kpz.AnyChat.ProfileSetting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;

/**
 * Created by user on 10/3/2017.
 */

public class Profile_Setting_AccoutSec extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView id = (TextView) findViewById(R.id.linkdoodid);
        TextView phone_num = (TextView) findViewById(R.id.phonenum);
        TextView email = (TextView) findViewById(R.id.email);

        String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
        id.setText(ids);

        String phone = String.valueOf(RequestHelper.getAccountInfo().getName());
        phone_num.setText(phone);

        String emails = String.valueOf(RequestHelper.getAccountInfo().getEmail());
        email.setText("Email Address Temporary Unavailable");
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void chg_pw(View view) {
        Toast.makeText(getApplicationContext(), "Change Password Feature Temporary Unavailable", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, Change_Password.class);
//        startActivity(intent);
    }

}


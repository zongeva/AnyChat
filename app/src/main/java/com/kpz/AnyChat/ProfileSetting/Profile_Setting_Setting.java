package com.kpz.AnyChat.ProfileSetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.kpz.AnyChat.R;

/**
 * Created by user on 10/3/2017.
 */

public class Profile_Setting_Setting extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void go_account(View view) {
        Intent intent = new Intent(this,Profile_Setting_AccoutSec.class);
        startActivity(intent);
    }

    public void go_notify(View view) {
        Intent intent = new Intent(this, Profile_Setting_Notification.class);
        startActivity(intent);
    }

    public void go_general(View view) {
        Toast.makeText(this, "This Section is Reserved & Under Construction", Toast.LENGTH_SHORT).show();
    }

    public void go_privacy(View view) {
        Toast.makeText(this, "This Section is Reserved & Under Construction", Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }



}

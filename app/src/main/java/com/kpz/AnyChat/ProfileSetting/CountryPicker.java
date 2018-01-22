package com.kpz.AnyChat.ProfileSetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.juanpabloprado.countrypicker.CountryPickerListener;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Account;

/**
 * Created by Lenovo on 19/1/2018.
 */

public class CountryPicker extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_picker);
        setTitle("Select Region");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,
                        com.juanpabloprado.countrypicker.CountryPicker.getInstance(new CountryPickerListener() {
                            @Override
                            public void onSelectCountry(String name, String code) {
                                Account account = new Account();
                                account.setID(RequestHelper.getAccountInfo().getID());
                                account.setArea(name);
                                RequestHelper.updateAccountInfo(account, new RequestCallBack() {
                                    @Override
                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                        Intent intent = new Intent(CountryPicker.this,Profile_Setting_New.class);
                                        startActivity(intent);
                                        Log.e("Change Region", "Successful");
                                    }
                                });
                            }
                        }))
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CountryPicker.this,Profile_Setting_New.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        Intent intent = new Intent(CountryPicker.this,Profile_Setting_New.class);
        startActivity(intent);
        return true;
    }
}

package com.kpz.AnyChat.ProfileSetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;

public class ChangePasswordVerifyCode extends Activity {

    EditText et_verificationCode;
    Button btn_continue;
    String countryCode, contactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_verify_code);

        et_verificationCode = (EditText) findViewById(R.id.et_verificationCode);
        btn_continue = (Button) findViewById(R.id.btn_continue);



        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countryCode == null && contactNo == null) {
                    Toast.makeText(ChangePasswordVerifyCode.this, "Ops, You Miss Something", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent nextStep = new Intent(getApplicationContext(), Change_Password.class);
                    nextStep.putExtra("countryCode", countryCode);
                    nextStep.putExtra("contactNo", contactNo);
                    nextStep.putExtra("verificationCode", Utils.urlencode(et_verificationCode.getText().toString()));
                    startActivity(nextStep);
                }
            }
        });
    }
}

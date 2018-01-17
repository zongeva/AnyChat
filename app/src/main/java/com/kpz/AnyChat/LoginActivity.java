package com.kpz.AnyChat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.model.AuthService;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.SDKClient;

public class LoginActivity extends AppCompatActivity {

    EditText et_hpnum;
    EditText et_password;
    Button btn_login, btn_createnewaccount;
    public static final String MY_PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        et_hpnum = (EditText) findViewById(R.id.et_phonenumber);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_createnewaccount = (Button) findViewById(R.id.btn_createnewaccount);
        final String server = "im.linkdood.com";

        boolean init = VIMClient.init(this, "com.kpz.anychat");
        if (!init) {
            Log.e("UCC Log", "Code: 1101001 SDK failed to initialize");
        } else {
            Log.e("UCC Log", "Code: 1101002 SDK successfully initialize");
        }

        SDKClient defaultClient = ClientManager.getDefault();
//        final AuthService authService = null;
        final AuthService authService = defaultClient.getAuthService();

        btn_createnewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, Register.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_hpnum.getText().toString().equals("") || et_password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Phone number or password is empty", Toast.LENGTH_SHORT).show();
                } else {
                    byte accountType = 1;
                    String hpnum = et_hpnum.getText().toString();
                    String hpnumCountry = "006" + hpnum;
                    String password = et_password.getText().toString();
                    Toast.makeText(LoginActivity.this, hpnumCountry, Toast.LENGTH_SHORT).show();

                    authService.login(accountType, hpnumCountry, password, server, new ResultCallBack<Long, Void, Void>() {
                        @Override
                        public void onSuccess(Long aLong, Void aVoid, Void aVoid2) {
                            //go to chat
                            Toast.makeText(LoginActivity.this, "Log in success", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("test", "Authentication fail" + " " + i + " " + s);
                        }
                    });


                }
            }
        });
    }
}

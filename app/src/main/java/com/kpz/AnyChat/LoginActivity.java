package com.kpz.AnyChat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kpz.AnyChat.Home_Activity.HomeActivity;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.model.Account;
import com.vrv.imsdk.model.AuthService;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.SDKClient;
import com.vrv.imsdk.model.User;

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

        if(authService.getLastLoginInfo() != null){
            Log.e("test", " Last Login Info User ID = " + authService.getLastLoginInfo().getUserID());

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            if (prefs.getString("shared_login_password", null) != null){
                authService.autoLogin(authService.getLastLoginInfo().getUserID(), server, new ResultCallBack<Long, Void, Void>() {
                    @Override
                    public void onSuccess(Long aLong, Void aVoid, Void aVoid2) {
                        Log.e("test", " Auto Login Success User ID " + authService.getLastLoginInfo().getUserID());

                        LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("test", "Auto Login Failed User ID " + authService.getLastLoginInfo().getUserID());

                    }
                });
            }
            else {
                prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                if (prefs.getString("shared_login_password", null) != null) {

                    String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.

                    authService.offlineLogin(authService.getLastLoginInfo().getUserID(), shared_login_password, server, new ResultCallBack() {
                        @Override
                        public void onSuccess(Object o, Object o2, Object o3) {
                            Log.e("test", "Offline Login Success User ID " + authService.getLastLoginInfo().getUserID());

                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("test", "Offline Login Failed User ID " + authService.getLastLoginInfo().getUserID());
                        }
                    });
                } else {
//                    mView.dismiss();
                    Log.e("test", "Offline Login Failed due to password no found");
                }
            }
        }

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
                    final String password = et_password.getText().toString();
                    //Toast.makeText(LoginActivity.this, hpnumCountry, Toast.LENGTH_SHORT).show();

                    authService.login(accountType, hpnumCountry, password, server, new ResultCallBack<Long, Void, Void>() {
                        @Override
                        public void onSuccess(Long aLong, Void aVoid, Void aVoid2) {
                            //go to chat list
                            SharedPreferences.Editor editor = LoginActivity.this.getSharedPreferences(MY_PREFS_NAME, LoginActivity.this.MODE_PRIVATE).edit();
                            editor.putString("shared_login_countrycode", "006");
                            editor.putString("shared_login_contactno", et_hpnum.getText().toString());
                            editor.putString("shared_login_password", password);
                            editor.apply();

                            //User user = new User();
                            //user.setName("zong");
                            final Account account = new Account();
                            account.setAccount(RequestHelper.getMainAccount().getAccount());
//                            account.setName("zong");
//                            RequestHelper.updateAccountInfo(account, new RequestCallBack() {
//                                @Override
//                                public void handleSuccess(Object o, Object o2, Object o3) {
//                                    Toast.makeText(LoginActivity.this, "Update name success " + account.getName(), Toast.LENGTH_SHORT).show();
//
//                                }
//                            });

                            //problem: retrieve nickname?

                            Toast.makeText(LoginActivity.this, "Log in success " + "Welcome " + account.getName(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Log.e("test kr id", aLong.toString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("test", "Authentication fail" + " " + i + " " + s);
                            if (i==112)
                            {
                                Toast.makeText(LoginActivity.this, "Incorrect number or password", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }
            }
        });
    }
}

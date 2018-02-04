package com.kpz.AnyChat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.bean.PersonalData;
import com.vrv.imsdk.model.Account;
import com.vrv.imsdk.model.AccountService;
import com.vrv.imsdk.model.AuthService;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.User;
import com.vrv.imsdk.model.UserService;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {

    Button btn_requestVeriCode;
    Button btn_continue;
    EditText et_phonenumber;
    EditText et_password;
    EditText et_veriCode;
    EditText et_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_requestVeriCode = (Button) findViewById(R.id.btn_requestVeriCode);
        btn_continue = (Button) findViewById(R.id.btn_continue);
        et_phonenumber = (EditText) findViewById(R.id.et_phonenumber);
        et_password = (EditText) findViewById(R.id.et_password);
        et_veriCode = (EditText) findViewById(R.id.et_veriCode);
        et_nickname = (EditText) findViewById(R.id.et_nickname);

        final byte userType = 1;
        final String server = "im.linkdood.com";
        // String account = et_phonenumber.getText().toString();
        // String accountCountry = "006" + et_phonenumber.getText().toString();
        //final String password = et_password.getText().toString();
        //final String veriCode = et_veriCode.getText().toString();

        final AuthService authService = ClientManager.getDefault().getAuthService();
        final AccountService accountService = ClientManager.getDefault().getAccountService();
        final UserService userService = ClientManager.getDefault().getUserService();


        btn_requestVeriCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String accountCountry = "006" + et_phonenumber.getText().toString();
                Toast.makeText(Register.this,"this part ok" + accountCountry, Toast.LENGTH_SHORT).show();

                authService.getRegCode(userType, server, accountCountry, new ResultCallBack<Integer, Void, Void>() {
                    @Override
                    public void onSuccess(Integer integer, Void aVoid, Void aVoid2) {
                        Toast.makeText(Register.this,"Verification code will be sent to you via SMS", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(int i, String s) {

                        Toast.makeText(Register.this,"Fail to request code " + "s", Toast.LENGTH_SHORT).show();

                    }
                });




            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = et_phonenumber.getText().toString();
                String accountCountry = "006" + et_phonenumber.getText().toString();
                String password = et_password.getText().toString();
                String veriCode = et_veriCode.getText().toString();
                final String nickName = et_nickname.getText().toString();
                if(account == ""){
                    Toast.makeText(Register.this,"Please enter your phone number", Toast.LENGTH_SHORT).show();
                }
                else if (password == ""){
                    Toast.makeText(Register.this,"Please enter your password", Toast.LENGTH_SHORT).show();

                }
                else if (veriCode == ""){
                    Toast.makeText(Register.this,"Please enter your verification code", Toast.LENGTH_SHORT).show();

                }
                else{
                    authService.signUp(veriCode, accountCountry, password, new ResultCallBack<Long, Void, Void>() {
                        @Override
                        public void onSuccess(Long aLong, Void aVoid, Void aVoid2) {
                            Toast.makeText(Register.this,"Account created", Toast.LENGTH_SHORT).show();
                            Register.this.startActivity(new Intent(Register.this, LoginActivity.class));
//                            Account accountNew = new Account();
//                            accountNew.setAccount(RequestHelper.getMainAccount().getAccount());
//                            accountNew.setName(nickName);
//                            RequestHelper.updateAccountInfo();

                            //problem: set nickname when registering




                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(Register.this,"Fail to create account", Toast.LENGTH_SHORT).show();
                            Log.e("test", String.valueOf(i) + " " + s);

                        }
                    });
                }
            }
        });


    }
}

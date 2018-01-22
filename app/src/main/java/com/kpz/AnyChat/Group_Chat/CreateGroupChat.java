package com.kpz.AnyChat.Group_Chat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kpz.AnyChat.Home_Activity.HomeActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
//import com.kpz.AnyChat.Others.MsgReceiver;
import com.kpz.AnyChat.Others.MsgReceiver;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.model.ResultCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 9/15/2017.
 */

public class CreateGroupChat extends AppCompatActivity {
    List<Long> userList = new ArrayList<Long>();
    String group_name;
    EditText txt,keyword1,keyword2,keyword3;
    TextView txts;
    RadioButton private_button;
    RadioButton public_button;
    ProgressDialog pDialog;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_groupchat);
        setTitle("Create Group Chat");
        long conv1 = RequestHelper.getAccountInfo().getID(); //get self ID and put into group
        long conv2 = 9151316648393693993L;
        userList.add(conv1);
        userList.add(conv2);
        private_button = (RadioButton) findViewById(R.id.private_button);
        public_button = (RadioButton) findViewById(R.id.public_button);
        keyword1 = (EditText)findViewById(R.id.keyword1);
        keyword2 = (EditText)findViewById(R.id.keyword2);
        keyword3 = (EditText)findViewById(R.id.keyword3);

        keyword1.setText("");
        keyword2.setText("");
        keyword3.setText("");

        try {
            startService(new Intent(this, MsgReceiver.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //phone number or email radio buttons
        private_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (private_button.isChecked() == true) {

                    public_button.setChecked(false);
                }
            }
        });

        public_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (public_button.isChecked() == true) {

                    private_button.setChecked(false);
                }
            }
        });
    }


    public void go_create_group(View view) {

                        txt = (EditText) findViewById(R.id.create_group_name);
                        txts = (TextView) findViewById(R.id.temp);
                        if(!keyword1.getText().toString().equals("") ) {

                            if ((!txt.equals("") && !txt.equals(" ") && !txt.equals("   "))) {
                                pDialog = new ProgressDialog(CreateGroupChat.this);
                                pDialog.setTitle("Requesting server for create group");
                                pDialog.setMessage("Please Wait...");
                                pDialog.setCancelable(false);
                                pDialog.show();
                                ClientManager.getDefault().getGroupService().create(1, txt.getText().toString(), userList, new ResultCallBack<Long, Void, Void>() {
                                    @Override
                                    public void onSuccess(Long aLong, Void aVoid, Void aVoid2) {
                                        pDialog.dismiss();
                                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                        TelephonyManager telephonyManager;
                                        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        if (ActivityCompat.checkSelfPermission(CreateGroupChat.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
                                        String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                                        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.

                                        String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//                                        Http_GetToken gt = new Http_GetToken(CreateGroupChat.this, 5, url_token, id, txts, deviceId, aLong + "", RequestHelper.getGroupInfo(aLong).getName(), keyword1.getText().toString() + ","+ keyword2.getText().toString() + "," + keyword3.getText().toString());
//                                        gt.execute();

                                        Log.e("Create Group", "Success");

                                        if (public_button.isChecked() == true) {
                                            RequestHelper.transferGroup(aLong, 9151316648393693993L, new RequestCallBack() {
                                                @Override
                                                public void handleSuccess(Object o, Object o2, Object o3) {
                                                    Log.e("Group Transfer", "Success");
                                                }
                                            });
                                            Intent intent = new Intent(CreateGroupChat.this, HomeActivity.class);
                                            intent.putExtra("othersideid", aLong);
                                            startActivity(intent);

//                                        Toast.makeText(CreateGroupChat.this, "Group Created Successfullly", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(CreateGroupChat.this, HomeActivity.class);
                                            intent.putExtra("othersideid", aLong);
                                            startActivity(intent);
//                                        Toast.makeText(CreateGroupChat.this, "Group Created Successfullly", Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        pDialog.dismiss();
                                        Toast.makeText(CreateGroupChat.this, "Invalid Group Parameter", Toast.LENGTH_SHORT).show();
                                        Log.e("Group Create ", "Fail");
                                        Log.e("Name", txt.getText().toString());


                                    }
                                });
                            } else {
                                txt.setError("Invalid Group Name");
                            }

                        }
                        else {
                            Toast.makeText(this, "Please at least enter one keyword for your group", Toast.LENGTH_SHORT).show();
                        }


    }


}

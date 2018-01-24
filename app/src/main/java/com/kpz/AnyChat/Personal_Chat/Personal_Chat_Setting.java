package com.kpz.AnyChat.Personal_Chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;

/**
 * Created by user on 9/22/2017.
 */

public class Personal_Chat_Setting extends AppCompatActivity {
    long othersideid;
    byte value;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
        othersideid = getIntent().getExtras().getLong("othersideid");
        final TextView noti = (TextView) findViewById(R.id.notification_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//     * @param callBack 传入接收结果回调  _1
//                              错误信息  _2
//                                用户ID _3
//          设置的值:  1.通知详情 2.通知源，隐藏内容 3.完全隐藏

        RequestHelper.getMsgNotifyMode(othersideid, new RequestCallBack<Long, Byte, Void>() {
            @Override
            public void handleSuccess(Long aLong, Byte aByte, Void aVoid) {
                if (value == 1) {
                    noti.setText("Enabled");
                } else {
                    noti.setText("Disabled");
                }
            }
        });
    }


    public void go_user_notification(View view) {

        new MaterialDialog.Builder(this)
                .title("Notification Setting")
                .items(R.array.my_array)
                .itemsCallback(new MaterialDialog.ListCallback() {

                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (which == 0) {
                            value = 1;
                        } else {
                            value = 3;
                        }
                        final TextView noti = (TextView) findViewById(R.id.notification_user);
                        RequestHelper.setMsgNotifyMode(othersideid, value, new RequestCallBack() {
                            @Override
                            public void handleSuccess(Object o, Object o2, Object o3) {
                                if (value == 1) {
                                    Toast.makeText(Personal_Chat_Setting.this, "Notification Enabled", Toast.LENGTH_SHORT).show();
                                    noti.setText("Enabled");
                                } else {
                                    Toast.makeText(Personal_Chat_Setting.this, "Notification Disabled", Toast.LENGTH_SHORT).show();
                                    noti.setText("Disabled");
                                }
                            }
                        });
                    }
                })
                .show();


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


}
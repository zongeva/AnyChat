package com.kpz.AnyChat.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.kpz.AnyChat.GenerateQRCode;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Contact;

import java.util.Objects;

/**
 * Created by user on 9/22/2017.
 */

public class Chat_Profile extends AppCompatActivity {
    long othersideid;
    long othersideidd;
    String username;
    String Avatar_Url, region, whatsup;
    Context context;
    int gender;
    Contact contact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_profile);
        othersideid = getIntent().getExtras().getLong("othersideid");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView avatar = (ImageView) findViewById(R.id.personal_avatar);
        TextView current_username = (TextView) findViewById(R.id.current_username);

        final TextView gender = (TextView)findViewById(R.id.gender);
        final TextView userregion = (TextView) findViewById(R.id.region);
        final TextView usersign = (TextView) findViewById(R.id.whatsup);

        contact = RequestHelper.getContactInfo(othersideid);
        String abc = Objects.toString(othersideid);
        String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + othersideid;
//        Utils.setNickname(getApplicationContext(), url, abc, current_username);

        ImageButton generate_QR_code_btn = (ImageButton) findViewById(R.id.generate_QR_code_btn);

//        final String userID_str = String.valueOf(othersideid);
//        generate_QR_code_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GenerateQRCode.class);
//
//                intent.putExtra("userids", userID_str);
//                startActivity(intent);
//            }
//        });




        RequestHelper.getUserInfo(othersideid, new RequestCallBack<Contact, Void, Void>() {
            @Override
            public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                ImageView avatar = (ImageView) findViewById(R.id.personal_avatar);
                Avatar_Url =contact.getAvatar();
                Log.e("Avatar URL ",contact.getAvatar());
                Utils.loadHead(getApplicationContext(), Avatar_Url, avatar, R.mipmap.vim_icon_default_user);
            }
        });




        /*
        * Retrieve Region
        * */
        RequestHelper.getUserInfo(othersideid, new RequestCallBack<Contact, Void, Void>() {
            @Override
            public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                String regions = contact.getArea();
                userregion.setText(regions);
            }
        });


        /*
        * Retrieve What's Up
        * */
        RequestHelper.getUserInfo(othersideid, new RequestCallBack<Contact, Void, Void>() {
            @Override
            public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                String sign = contact.getSign();
                usersign.setText(sign);
            }
        });



        /*
        * Retrieve and show Gender on activity
        * */

        RequestHelper.getUserInfo(othersideid, new RequestCallBack<Contact, Void, Void>() {
            @Override
            public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                if(contact.getGender() == 0)
                {
                    gender.setText("Unknow");
                }
                else if (contact.getGender() == 1)
                {
                    gender.setText("Female");
                }
                else if (contact.getGender() == 2)
                {
                    gender.setText("Male");
                }
            }
        });





    }


    public void go_message(View view) {
        othersideid = getIntent().getExtras().getLong("othersideid");
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("othersideid", othersideid);
        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void go_voice_call(View view) {
        Toast.makeText(context, "Voice Call Function Is Under Construction", Toast.LENGTH_SHORT).show();
    }
}

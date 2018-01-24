package com.kpz.AnyChat.ProfileSetting;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kpz.AnyChat.Others.ImageFilePath;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.model.ChatService;

/**
 * Created by user on 9/28/2017.
 */

public class Change_Avatar extends AppCompatActivity {
    static final int SELECT_CAPTURED_IMAGE = 1;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    ProgressDialog pDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Initialize Avatar
        ImageView user_avatar = (ImageView) findViewById(R.id.user_avatar);
        String URL = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), URL, user_avatar, R.mipmap.vim_icon_default_user);
    }

    public void change_avatar(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_CAPTURED_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CAPTURED_IMAGE) {

            final String selectedImagePath;
            if(data != null) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = ImageFilePath.getPath(this, selectedImageUri);

                Log.e("Image File Path", "" + selectedImagePath);
                final String URLs = RequestHelper.getAccountInfo().getAvatar();

                Toast.makeText(Change_Avatar.this, "Uploading Avatar..." , Toast.LENGTH_LONG).show();
                Toast.makeText(Change_Avatar.this, "Please Wait..." , Toast.LENGTH_SHORT).show();
                RequestHelper.updateAccountAvatar(selectedImagePath, new RequestCallBack() {
                    ImageView user_avatar = (ImageView) findViewById(R.id.user_avatar);
                    @Override
                    public void handleSuccess(Object o, Object o2, Object o3) {

                        new CountDownTimer(1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                Toast.makeText(Change_Avatar.this, "Success" , Toast.LENGTH_SHORT).show();
                                Log.e("Update Avatar", "Success");
                                String URL = RequestHelper.getAccountInfo().getAvatar();
                                Utils.loadHead(getApplicationContext(), selectedImagePath, user_avatar, R.mipmap.vim_icon_default_user);
                            }

                            public void onFinish() {
                                Utils.loadHead(getApplicationContext(), selectedImagePath, user_avatar, R.mipmap.vim_icon_default_user);
                                Intent intent = new Intent(Change_Avatar.this,Profile_Setting_New.class);
                                startActivity(intent);
                            }

                        }.start();


                    }
                });
            }else {
                Toast.makeText(Change_Avatar.this, "Avatar Change Cancelled", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,Profile_Setting_New.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        ImageView user_avatar = (ImageView) findViewById(R.id.user_avatar);
        String URL = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), URL, user_avatar, R.mipmap.vim_icon_default_user);
    }
}

package com.kpz.AnyChat.Group_Chat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
//import com.kpz.AnyChat.GenerateQRCode;
import com.kpz.AnyChat.Home_Activity.HomeActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.ImageFilePath;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.TinyGroup;

/**
 * Created by user on 9/15/2017.
 */

public class Group_Chat_Setting extends AppCompatActivity {
    long othersideid, creatorID, selfid;
    byte value, verifyType, isAllow;
    TinyGroup tinygroup;
    String message;
    static final int SELECT_CAPTURED_IMAGE = 1;
    String msg;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    ProgressDialog pDialog;
    public TinyGroup getTinygroup(long othersideid) {
        return RequestHelper.getGroupInfo(othersideid).getInfo();

    }
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        othersideid = getIntent().getExtras().getLong("user_group_id");

        TextView change_group_names = (TextView) findViewById(R.id.change_group_name); //change group name
        TextView group_names = (TextView) findViewById(R.id.setting_group_name); //current_group_name
        TextView group_member_lists = (TextView) findViewById(R.id.group_member_list); //group_member_list
        TextView group_notifications = (TextView) findViewById(R.id.group_notification); //Notification setting
        ToggleButton toggles = (ToggleButton) findViewById(R.id.toggle_verification); // on for verification\
        TextView request = (TextView)findViewById(R.id.textView14);
        TextView request1 = (TextView)findViewById(R.id.textView18);
        Button delete_groups = (Button) findViewById(R.id.delete_group);
        ImageView avatar = (ImageView) findViewById(R.id.group_avatar);
        Button chg_avatar =(Button)findViewById(R.id.change_avatar);
        String URL = RequestHelper.getGroupInfo(othersideid).getAvatar();
        Utils.loadHead(getApplicationContext(), URL, avatar, R.mipmap.vim_icon_default_group);
        ImageButton generate_QR_code_btn = (ImageButton) findViewById(R.id.generate_QR_code_btn);



        creatorID = getTinygroup(othersideid).getCreatorID();
        selfid = RequestHelper.getAccountInfo().getID();


        String creator = Long.toString(creatorID);
        String self = Long.toString(selfid);

        if (creatorID == selfid) {
            delete_groups.setText("Delete Group");
            message = "Group - "+ RequestHelper.getGroupInfo(othersideid).getName() + " Deleted";
            msg = "Are you sure you want to delete the group?";

        } else if (creatorID != selfid) {
            delete_groups.setText("Leave Group");
            message = "You had left the group -  "+ RequestHelper.getGroupInfo(othersideid).getName() ;
            msg ="Are you sure you want to leave the group?";
            chg_avatar.setVisibility(View.INVISIBLE);
            request.setVisibility(View.INVISIBLE);
            request1.setVisibility(View.INVISIBLE);
            toggles.setVisibility(View.INVISIBLE);

        }


        //////---Show Defauly Group Name Activity

        Group group = RequestHelper.getGroupInfo(othersideid);
        RequestHelper.getMsgNotifyMode(othersideid, new RequestCallBack<Long, Byte, Void>() {
            @Override
            public void handleSuccess(Long aLong, Byte aByte, Void aVoid) {
                TextView noti = (TextView) findViewById(R.id.notification_group);
                if (aByte == 1) {
                    noti.setText("Enabled");
                } else {
                    noti.setText("Disabled");

                }
            }
        });

        RequestHelper.getGroupSet(othersideid, new RequestCallBack<Byte, Byte, Void>() {
            ToggleButton toggles = (ToggleButton) findViewById(R.id.toggle_verification); // on for verification\

            @Override
            public void handleSuccess(Byte aByte, Byte aByte2, Void aVoid) {
                if (aByte == 2)
                {
                    toggles.setChecked(true);
                }
                else{
                    toggles.setChecked(false);
                }
            }
        });
        String chat_name = group.getName();
        group_names.setText(chat_name.toString()); //set group name

        final String groupidstring = String.valueOf(group.getID());



//        generate_QR_code_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), GenerateQRCode.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("groupids", groupidstring);
//                startActivity(intent);
//            }
//        });

    }

//    //////---Change Group Name Activity
//    public void go_change_groupname(View view) {
//        new MaterialDialog.Builder(this)
//                .title("New Group Name")
//                .content("Please Insert Your New Group Name")
//                .inputType(InputType.TYPE_CLASS_TEXT)
//                .input("Group Name", "", new MaterialDialog.InputCallback() {
//                    @Override
//                    public void onInput(MaterialDialog dialog, final CharSequence input) {
//                        CharSequence cs = input;
//                        String s = cs.toString();
//                        final TextView group_names = (TextView) findViewById(R.id.setting_group_name); //current_group_name
//
//
//                        if (input != null) {
//                            GroupUpdate groupUpdate = new GroupUpdate();
//                            groupUpdate.setName(s);
//
//                            RequestHelper.setGroupInfo(othersideid, groupUpdate, new RequestCallBack() {
//                                @Override
//                                public void handleSuccess(Object o, Object o2, Object o3) {
//                                    Toast.makeText(Group_Chat_Setting.this, "Group Name Change Successfully", Toast.LENGTH_SHORT).show();
//                                    group_names.setText(input);
//                                }
//                            });
//                        } else {
//                            Toast.makeText(Group_Chat_Setting.this, "Group Name Cannot Be Empty ! ", Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    }
//                }).show();
//    }

    public void go_group_member_list(View view) {
        Intent intent = new Intent(this, SelectGroupMemberActivity.class);
        intent.putExtra("user_group_id", othersideid);
        startActivity(intent);
    }


    public void go_group_notification(View view) {

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
                        final TextView noti = (TextView) findViewById(R.id.notification_group);
                        RequestHelper.setMsgNotifyMode(othersideid, value, new RequestCallBack() {
                            @Override
                            public void handleSuccess(Object o, Object o2, Object o3) {
                                if (value == 1) {
                                    Toast.makeText(Group_Chat_Setting.this, "Notification Enabled", Toast.LENGTH_SHORT).show();

                                    noti.setText("Enabled");
                                } else {
                                    Toast.makeText(Group_Chat_Setting.this, "Notification Disabled", Toast.LENGTH_SHORT).show();
                                    noti.setText("Disabled");
                                }
                            }
                        });
                    }
                })
                .show();


    }

    //验证类型: 1.不允许任何人添加, 2.需要验证信息, 3.允许任何人添加.
//是否允许群成员邀请好友加入群: 1.允许 2.不允许.

    public void go_group_verification(View view) {


        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            isAllow = 2;
            verifyType = 2;
        } else {
            isAllow = 2;
            verifyType = 3;

        }
        final TextView noti = (TextView) findViewById(R.id.notification_group);
        RequestHelper.setGroupSet(othersideid, verifyType, isAllow, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {
                if (verifyType == 2) {
                    Toast.makeText(Group_Chat_Setting.this, "Join Group Verification Enabled", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Group_Chat_Setting.this, "Join Group Verification Disabled", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }





    public void go_remove_group(View view) {

        new MaterialDialog.Builder(this)
                .title("Group")
                .content(msg)
                .positiveText("Agree")
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        RequestHelper.removeGroup(othersideid, new RequestCallBack() {
                            @Override
                            public void handleSuccess(Object o, Object o2, Object o3) {
                                TelephonyManager telephonyManager;
                                telephonyManager = (TelephonyManager) Group_Chat_Setting.this.getSystemService(Context.TELEPHONY_SERVICE);
                                @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
                                String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
                                SharedPreferences prefs = Group_Chat_Setting.this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
                                String url_get_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;

//                                Http_GetToken gt = new Http_GetToken(Group_Chat_Setting.this, 14, url_get_token, ids, new TextView(Group_Chat_Setting.this), deviceId, String.valueOf(othersideid), "", "");
//                                gt.execute();
                                Log.e("Delete Group Token ", url_get_token);

                                RequestHelper.removeChat(othersideid, new RequestCallBack() {
                                    @Override
                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                        //Delet Group Success Message
                                    }
                                });
                            }
                        });


//                        Toast.makeText(Group_Chat_Setting.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Group_Chat_Setting.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }

                            }
                ).show();

    }


    public void change_avatar(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_CAPTURED_IMAGE);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Image Selected
        if (requestCode == SELECT_CAPTURED_IMAGE) {
            final String selectedImagePath;
            if(data != null) {
                Uri selectedImageUri = data.getData();
                ChatMsgBuilder builder = new ChatMsgBuilder(othersideid);

                selectedImagePath = ImageFilePath.getPath(this, selectedImageUri);
                Log.e("Image File Path", "" + selectedImagePath);

                final String URLs = RequestHelper.getGroupInfo(othersideid).getAvatar();
                RequestHelper.setGroupInfoAvatar(othersideid, selectedImagePath, new RequestCallBack() {
                    @Override
                    public void handleSuccess(Object o, Object o2, Object o3) {

                        new CountDownTimer(3500, 1000) {
                            public void onTick(long millisUntilFinished) {
                                Toast.makeText(Group_Chat_Setting.this, "Avatar Change Successfully", Toast.LENGTH_SHORT).show();
                                ImageView avatar = (ImageView) findViewById(R.id.group_avatar);
                                String URL = RequestHelper.getGroupInfo(othersideid).getAvatar();
                                Utils.loadHead(getApplicationContext(), URL, avatar, R.mipmap.vim_icon_default_group);
                            }

                            public void onFinish() {
                                ImageView avatar = (ImageView) findViewById(R.id.group_avatar);
                                String URL = RequestHelper.getGroupInfo(othersideid).getAvatar();
                                Utils.loadHead(getApplicationContext(), URL, avatar, R.mipmap.vim_icon_default_group);
                            }

                        }.start();


                    }
                });
            }
            else{
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageView avatar = (ImageView) findViewById(R.id.group_avatar);
        String URL = RequestHelper.getGroupInfo(othersideid).getAvatar();
        Utils.loadHead(getApplicationContext(), URL, avatar, R.mipmap.vim_icon_default_group);
    }
}

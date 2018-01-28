package com.kpz.AnyChat.Home_Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
//import com.kpz.AnyChat.BarcodeReader;
import com.kpz.AnyChat.Group_Chat.CreateGroupChat;
//import com.kpz.AnyChat.Loading.CatLoadingView;
import com.kpz.AnyChat.LoginActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
//import com.kpz.AnyChat.Others.App;
import com.kpz.AnyChat.Others.MsgReceiver;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.ProfileSetting.Profile_Setting_Home;
import com.kpz.AnyChat.ProfileSetting.Profile_Setting_New;
import com.kpz.AnyChat.R;
import com.kpz.AnyChat.RecyclerViewChat.Chat_RecyclerView_Activity;
import com.kpz.AnyChat.SearchActivity;
import com.kpz.AnyChat.SystemBoxActivity;
import com.kpz.AnyChat.Utilities_Page;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.listener.ReceiveMsgListener;
import com.vrv.imsdk.listener.ReceiverChatListener;
import com.vrv.imsdk.model.Account;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.SystemMsg;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    ViewPager pager;
    RadioGroup radioGroup;
    ListView ah_listView;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    ArrayList<Long> Ids;
//    CatLoadingView mView;
    int count;
    private long time;
    Account account;
    ImageButton profile,notification,scan_qr,search,add;

    @Override
    public void onAttachedToWindow() {

        super.onAttachedToWindow();

        RequestHelper.getChatList();
        ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("AnyChat");
//        mView = new CatLoadingView();
//        mView.show(getSupportFragmentManager(), "");
//        mView.dismiss();
        RequestHelper.login_status(HomeActivity.this);



        final long selfid = RequestHelper.getAccountInfo().getID();
        final Account account = new Account();
        if(String.valueOf(selfid) != RequestHelper.getAccountInfo().getName().toString()) {
            account.setName(selfid + "");
            RequestHelper.updateAccountInfo(account, new RequestCallBack() {
                @Override
                public void handleSuccess(Object o, Object o2, Object o3) {
                }
            });
        }

//        RequestHelper.login_status(HomeActivity.this);
         profile = (ImageButton)findViewById(R.id.profile);
         notification = (ImageButton)findViewById(R.id.notification);
         scan_qr = (ImageButton)findViewById(R.id.scanqr);
         search  = (ImageButton)findViewById(R.id.searchgroup);
         add  = (ImageButton)findViewById(R.id.plus);

        String Avatar_Url = RequestHelper.getAccountInfo().getAvatar();
        Utils.loadHead(getApplicationContext(), Avatar_Url, profile, R.mipmap.vim_icon_default_user);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Profile_Setting_New.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent = new Intent(HomeActivity.this, SystemBoxActivity.class);
//                App.finishApp(HomeActivity.this);
//                startActivity(intent);

                long krid = 9151316648393694037L;
                ChatMsgBuilder builder = new ChatMsgBuilder(krid);

                ChatMsg msgtest = builder.createTxtMsg("asdasdsaqwrqwrq test");
                chatService.sendMsg(msgtest, new ResultCallBack<Void, Void, Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                        Toast.makeText(HomeActivity.this, "Message sent successfully", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                }, new ResultCallBack<Integer, Integer, String>() {
                    @Override
                    public void onSuccess(Integer integer, Integer integer2, String s) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, CreateGroupChat.class);
//                startActivity(intent);


            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        scan_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), BarcodeReader.class);
//                startActivity(intent);
            }
        });



        try {

            startService(new Intent(this, MsgReceiver.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        ah_listView = (ListView) findViewById(R.id.ah_listView);
        ah_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                Intent intent = new Intent(HomeActivity.this, Chat_RecyclerView_Activity.class);



                intent.putExtra("othersideid", ClientManager.getDefault().getChatService().getList().get(position).getID());
                finishAffinity();
                startActivity(intent);

            }
        });

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_conversation) {
                    Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else if (checkedId == R.id.rb_search) {
                    Intent intent = new Intent(HomeActivity.this,SearchActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }  else if (checkedId == R.id.rb_utilities) {
                    Intent intent = new Intent(HomeActivity.this,Utilities_Page.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }else if (checkedId == R.id.rb_me) {
                    Intent intent = new Intent(HomeActivity.this,Profile_Setting_Home.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        });

        List<Chat> list = chatService.getList();
        Log.e("UCC Log", "code " + Utils.osType + "1504001 Check chat list size  " + list.size());

        chatService.observeChatListener(new ReceiverChatListener() {
            @Override
            public void onReceive(Chat chat) {
                List<Chat> list = chatService.getList();
                RequestHelper.getChatList();
                ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));
                ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));

            }
        },true);

        RequestHelper.getChatList();
        ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));
        ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));
        ah_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final long ids = ClientManager.getDefault().getChatService().getList().get(position).getID();

                new MaterialDialog.Builder(HomeActivity.this)
                        .title("Delete Conversation")
                        .content("Do You Want To Delete This Conversation?")
                        .positiveText("Yes")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                RequestHelper.removeChat(ids, new RequestCallBack() {
                                    @Override
                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }
                                });
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //Cancel
                            }
                        })
                        .show();
                return true;
            }
        });



        chatService.observeMsgListener(new ReceiveMsgListener() {


            @Override
            public void receive(ChatMsg chatMsg) {

            if(chatMsg.getBody().equals("Check app version")){
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
                final TextView dummyi = (TextView)findViewById(R.id.dummies);
                String url_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + prefs.getString("shared_login_password", null) + "&AppRandomKey=" + deviceId;
                Log.e("Login URL",url_token);

                String msytrings = "0.1.0";

//                Http_GetToken gt = new Http_GetToken(getApplicationContext(), 7, url_token, "", dummyi, "", msytrings, "","");
//                gt.execute();
            }
                List<Chat> list = chatService.getList();
                        if (list.size() != 0) {
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));


        } else {
            Toast.makeText(getApplicationContext(), "No History To Retrieve", Toast.LENGTH_SHORT).show();
        }
            }

            @Override
            public void update(ChatMsg chatMsg) {


        List<Chat> list = chatService.getList();
        if (list.size() != 0) {
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));

        } else {
            Toast.makeText(getApplicationContext(), "No History To Retrieve", Toast.LENGTH_SHORT).show();
        }
            }
        },true);





    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            ClientManager.getDefault().getAuthService().logout(new ResultCallBack() {
                @Override
                public void onSuccess(Object o, Object o2, Object o3) {
                    Log.e("UCC Log", "Code: " + Utils.osType + "1501001 Logout Success");
                    finishAffinity();
                    HomeActivity.this.startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                }

                @Override
                public void onError(int i, String s) {
                    Log.e("UCC Log", "Code: " + Utils.osType + "1501002 Logout Failed");
                }
            });
            return true;
        }


        if(id == R.id.action_Scan_QR_code){
//            Intent intent = new Intent(getApplicationContext(), BarcodeReader.class);
//            startActivity(intent);
        }
        if(id == R.id.action_search_group){
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    public void go_personal_list(View view) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void go_group_list(View view) {
        Intent intent = new Intent(getApplicationContext(), Utilities_Page.class);
        startActivity(intent);
    }

    public void go_test(View view) {
        Intent intent = new Intent(getApplicationContext(), Chat_RecyclerView_Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestHelper.getSysMessages(0, 100, time, Constants.FLAG_UP, new RequestCallBack<List<SystemMsg>, Void, Void>() {
            @Override
            public void handleSuccess(List<SystemMsg> result, Void aVoid, Void aVoid2) {
//                counts = (TextView) findViewById(R.id.textView76);
                for (int i =0;i<result.size();i++){
                    if (result.get(i).getIsRead() == 0)
                    {
                        count = count +1;
                    }
                }

                if (count > 0) {
                    notification.setImageResource(R.mipmap.ic_menu_notify_occupied);

                } else if (count > 99) {
                    notification.setImageResource(R.mipmap.ic_menu_notify_occupied);

//                    counts = (TextView) findViewById(R.id.textView76);
//                    counts.setVisibility(View.VISIBLE);
//                    counts.setText("99+");
                }
            }
        });



        List<Chat> list = chatService.getList();
        if (list.size() != 0) {
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));

        } else {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatService.observeChatListener(new ReceiverChatListener() {
            @Override
            public void onReceive(Chat chat) {
                List<Chat> list = chatService.getList();
                RequestHelper.getChatList();
                ah_listView.setAdapter(new ChatHistoryArrayAdapter(HomeActivity.this, ClientManager.getDefault().getChatService().getList()));
                ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));

            }
        },false);
    }


    @Override
    protected void onStop() {
        super.onStop();
        RequestHelper.getSysMessages(0, 100, time, Constants.FLAG_UP, new RequestCallBack<List<SystemMsg>, Void, Void>() {
            @Override
            public void handleSuccess(List<SystemMsg> result, Void aVoid, Void aVoid2) {
//                ClientManager.getDefault().getSysMsgService().setMsgRead(result);
//                counts = (TextView) findViewById(R.id.textView76);
                for (int i =0;i<result.size();i++){
                    if (result.get(i).getIsRead() == 0)
                    {
                        count = count +1;
                    }
                }

                if (count > 0) {
                notification.setImageResource(R.mipmap.ic_menu_notify_occupied);
                } else if (count > 99) {

//                    counts = (TextView) findViewById(R.id.textView76);
//                    counts.setVisibility(View.VISIBLE);
//                    counts.setText("99+");
                }
            }
        });
    }


}




package com.kpz.AnyChat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

//import com.kpz.AnyChat.DropBox.SendTextContent;
import com.kpz.AnyChat.Group_Chat.CreateGroupChat;
import com.kpz.AnyChat.Home_Activity.HomeActivity;
//import com.kpz.AnyChat.Others.App;
import com.kpz.AnyChat.Others.MsgReceiver;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.ProfileSetting.Profile_Setting_Home;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.GroupService;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.SystemMsg;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.valueOf;

public class Utilities_Page extends AppCompatActivity {

    GroupService grouService = ClientManager.getDefault().getGroupService();
    private long time;
    RadioGroup radioGroup;
    private List<SystemMsg> list = new ArrayList<>();
    Context context;
    TextView counts;
    private RecyclerView recyclerView;
    ChatService chatService = ClientManager.getDefault().getChatService();
    List<Long> userList = new ArrayList<Long>();
    int count;

    String conv1 = "9151316648393684041";
    String conv3 = "9151316648393684049";
    Chat chat = ClientManager.getDefault().getChatService().findItemByID(ChatMsgApi.getSysMsgID());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility);
        setTitle("Utilities");

        Button button12 = (Button)findViewById(R.id.button12);
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Utilities_Page.this,SendTextContent.class);
//                startActivity(intent);
            }
        });


        RequestHelper.login_status(Utilities_Page.this);
     try {

         startService(new Intent(this, MsgReceiver.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        userList.add(valueOf(conv1));
        userList.add(valueOf(conv3));

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_conversation) {
                    Intent intent = new Intent(Utilities_Page.this,HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                } else if (checkedId == R.id.rb_search) {
                    Intent intent = new Intent(Utilities_Page.this,SearchActivity.class);
                    finish();startActivity(intent);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                    startActivity(intent);
                }  else if (checkedId == R.id.rb_utilities) {
                    Intent intent = new Intent(Utilities_Page.this,Utilities_Page.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }else if (checkedId == R.id.rb_me) {
                    Intent intent = new Intent(Utilities_Page.this,Profile_Setting_Home.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
            }
        });
    }



    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
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
                    Utilities_Page.this.startActivity(new Intent(Utilities_Page.this, LoginActivity.class));
                }

                @Override
                public void onError(int i, String s) {
                    Log.e("UCC Log", "Code: " + Utils.osType + "1501002 Logout Failed");
                }
            });
            return true;
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

    public void go_search(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void go_sysrequest(View view) {
        count = 0;
//        Intent intent = new Intent(this, SystemBoxActivity.class);
//        App.finishApp(Utilities_Page.this);
//        startActivity(intent);
    }

    public void go_create_groupchat(View view) {
        Intent intent = new Intent(this, CreateGroupChat.class);
        startActivity(intent);
    }

    public void go_me(View view) {
        Intent intent = new Intent(this, Profile_Setting_Home.class);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        RequestHelper.getSysMessages(0, 100, time, Constants.FLAG_UP, new RequestCallBack<List<SystemMsg>, Void, Void>() {
            @Override
            public void handleSuccess(List<SystemMsg> result, Void aVoid, Void aVoid2) {
                counts = (TextView) findViewById(R.id.textView76);
                for (int i =0;i<result.size();i++){
                    if (result.get(i).getIsRead() == 0)
                    {
                        count = count +1;
                    }
                }

                if (count > 0) {
                    counts.setVisibility(View.VISIBLE);
                    counts.setText(count + "");
                } else if (count > 99) {
                    counts = (TextView) findViewById(R.id.textView76);
                    counts.setVisibility(View.VISIBLE);
                    counts.setText("99+");
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestHelper.getSysMessages(0, 100, time, Constants.FLAG_UP, new RequestCallBack<List<SystemMsg>, Void, Void>() {
            @Override
            public void handleSuccess(List<SystemMsg> result, Void aVoid, Void aVoid2) {
//                ClientManager.getDefault().getSysMsgService().setMsgRead(result);
                counts = (TextView) findViewById(R.id.textView76);
                for (int i =0;i<result.size();i++){
                    if (result.get(i).getIsRead() == 0)
                    {
                        count = count +1;
                    }
                }

                if (count > 0) {
                    counts.setVisibility(View.VISIBLE);
                    counts.setText(count + "");
                } else if (count > 99) {
                    counts = (TextView) findViewById(R.id.textView76);
                    counts.setVisibility(View.VISIBLE);
                    counts.setText("99+");
                }
            }
        });
    }


}



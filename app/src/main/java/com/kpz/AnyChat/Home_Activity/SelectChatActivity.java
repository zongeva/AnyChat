package com.kpz.AnyChat.Home_Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
//import com.linkdood.ucc.Loading.CatLoadingView;
import com.kpz.AnyChat.Others.DialogUtil;
import com.kpz.AnyChat.Others.MsgReceiver;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.kpz.AnyChat.RecyclerViewChat.Chat_RecyclerView_Activity;
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationView;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.listener.ProgressListener;
import com.vrv.imsdk.listener.ReceiveMsgListener;
import com.vrv.imsdk.listener.ReceiverChatListener;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.Member;
import com.vrv.imsdk.model.ResultCallBack;

import java.util.ArrayList;
import java.util.List;

import static com.kpz.AnyChat.Home_Activity.HomeActivity.MY_PREFS_NAME;

public class SelectChatActivity extends AppCompatActivity {
    public BottomNavigationView bottomNavigationView;
    ViewPager pager;
    RadioGroup radioGroup;
    ListView ah_listView;
    String user_id;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    ArrayList<Long> Ids;
    Boolean forward = false;
    String msgtype;
//    CatLoadingView mView;


    int time = 1000,counter,flag = 0,processing_type;
    int TYPE_TEXT = 0;
    int TYPE_IMAGE = 1;
    int TYPE_VIDEO= 2;
    int TYPE_ETC = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat);
        setTitle("Forward");
//        mView = new CatLoadingView();
        final Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        try {
            startService(new Intent(this, MsgReceiver.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                processing_type = TYPE_TEXT;
            } else if (type.startsWith("image/")) {
                processing_type = TYPE_IMAGE;
            }else if (type.startsWith("video/")) {
                processing_type = TYPE_VIDEO;
            } else {
                processing_type = TYPE_ETC;
            }
        }


        msgtype = getIntent().getExtras().getString("type");


        ah_listView = (ListView) findViewById(R.id.ah_listView);
        ah_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                Log.e("Forward To  ", ClientManager.getDefault().getChatService().getList().get(position).getID() +"");

                if(!forward && msgtype != null) {
                    long fwd_tgt_id = getIntent().getExtras().getLong("fwd_tgt_id"); // From ID
                    long fwd_msg_id = getIntent().getExtras().getLong("fwd_msg_id"); // From MsgID
                    final long fwd_to_id = ClientManager.getDefault().getChatService().getList().get(position).getID(); // To ID
                    Log.e("Forward MsgID From ",fwd_msg_id+"");
                    Log.e("Forward ID From ",fwd_tgt_id+"");
                    // Get From Target
                    ClientManager.getDefault().getChatService().getMessages(fwd_tgt_id, fwd_msg_id, 1, 0, new ResultCallBack<Long, List<ChatMsg>, Void>() {
                        @Override
                        public void onSuccess(Long aLong, final List<ChatMsg> chatMsgs, Void aVoid) {
                            //Forward once get successfully

                            //Forward to target

                            DialogUtil.buildSelectDialog(SelectChatActivity.this, "Forward", "Are you Sdure you want to forward this message?", new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    RequestHelper.transferMsg(fwd_to_id, chatMsgs.get(0), new ResultCallBack<Void, Void, Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                                            Intent intent = new Intent(SelectChatActivity.this, Chat_RecyclerView_Activity.class);
                                            intent.putExtra("othersideid", ClientManager.getDefault().getChatService().getList().get(position).getID());

                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onError(int i, String s) {

                                        }
                                    });
                                }
                            }, new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    Toast.makeText(SelectChatActivity.this, "Forward Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });



                        }

                        @Override
                        public void onError(int i, String s) {
                        Log.e("Error","GetMsg Fail" + i + " " + s);
                        }
                    });

                    forward = true;

                }
                else{

                    Log.e("Processing Type",processing_type+"");
                        if (processing_type == TYPE_TEXT){
                            handleSendText(intent, ClientManager.getDefault().getChatService().getList().get(position).getID());
                        } else if (processing_type == TYPE_IMAGE){
                            handleSendImage(intent, ClientManager.getDefault().getChatService().getList().get(position).getID());
                        } else if (processing_type == TYPE_VIDEO){
                            handleSendVideo(intent, ClientManager.getDefault().getChatService().getList().get(position).getID());
                        } else {
                            handleOtherMimeType(intent, ClientManager.getDefault().getChatService().getList().get(position).getID());
                        }


//                    Toast.makeText(SelectChatActivity.this, "Sending To " + position, Toast.LENGTH_SHORT).show();
                }
            }
        });


        List<Chat> list = chatService.getList();
        Log.e("UCC Log", "code " + Utils.osType + "1504001 Check chat list size  " + list.size());

        chatService.observeChatListener(new ReceiverChatListener() {
            @Override
            public void onReceive(Chat chat) {
                RequestHelper.getChatList();
                ah_listView.setAdapter(new ChatHistoryArrayAdapter(SelectChatActivity.this, ClientManager.getDefault().getChatService().getList()));

            }
        },true);

        RequestHelper.getChatList();
        ah_listView.setAdapter(new ChatHistoryArrayAdapter(SelectChatActivity.this, ClientManager.getDefault().getChatService().getList()));
        ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));
        ah_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final long ids = ClientManager.getDefault().getChatService().getList().get(position).getID();

                new MaterialDialog.Builder(SelectChatActivity.this)
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
                                        Intent intent = new Intent(getApplicationContext(), SelectChatActivity.class);
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
                List<Chat> list = chatService.getList();
                        if (list.size() != 0) {
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(SelectChatActivity.this, ClientManager.getDefault().getChatService().getList()));
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));


        } else {
            Toast.makeText(getApplicationContext(), "No History To Retrieve", Toast.LENGTH_SHORT).show();
        }
            }

            @Override
            public void update(ChatMsg chatMsg) {
        List<Chat> list = chatService.getList();
        if (list.size() != 0) {
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(SelectChatActivity.this, ClientManager.getDefault().getChatService().getList()));
            ah_listView.setAdapter(new ChatHistoryArrayAdapter(getApplicationContext(), list));


        } else {
            Toast.makeText(getApplicationContext(), "No History To Retrieve", Toast.LENGTH_SHORT).show();
        }
            }
        },true);

    }

    void handleSendText(Intent intent, long dest) {
        final long othersideid = dest;
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(dest);
            final ChatMsg chatMsg = chatMsgBuilder.createTxtMsg(sharedText);

            chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                @Override
                public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                    final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                    counter = prefs.getInt("shared_send_counter", counter);
                    counter = counter + 1;
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                    editor.putInt("shared_send_counter", counter);
                    editor.apply();

                    if (ChatMsgApi.isGroup(othersideid)) {
                        RequestHelper.getMemberList(othersideid, new RequestCallBack<List<Member>, Void, Void>() {
                            @Override
                            public void handleSuccess(List<Member> members, Void aVoid, Void aVoid2) {
                                for (int i = 0; i < members.size(); i++) {
                                    if (members.get(i).getID() != 9151316648393693993L) {

                                        Log.e("Mem List", String.valueOf(members.get(i).getID()));
                                        Context contexts = getApplicationContext();
                                        user_id = String.valueOf(members.get(i).getID());
                                        String msg_id = String.valueOf(chatMsg.getMsgID());
                                        String read_status = "PR";
                                        String msg_type = "99";
//                                        Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

                                        Log.e("UCC Log", "code " + Utils.osType + "1601013 Success to Push group msg data into db");
                                    }
                                }

                            }
                        });
                    } else {
                        Context contexts = getApplicationContext();
                        user_id = String.valueOf(othersideid);
                        String msg_id = String.valueOf(chatMsg.getMsgID());
                        String read_status = "PA";
                        String msg_type = "99";
//                        Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
//                        Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                    }
//                    Log.e("UCC Log", "code " + Utils.osType + "1601010 Success to send message");


                }

                @Override
                public void onError(int i, String s) {

                }
            }, new RequestCallBack<Integer, Integer, String>() {
                @Override
                public void handleSuccess(Integer integer, Integer integer2, String s) {

                }
            });

        }
    }

    void handleSendImage(Intent intent, long dest) {
        final long othersideid = dest;

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            final String finalpath = Utils.getFilePathFromURI(SelectChatActivity.this,imageUri);
            final ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(dest);

            DialogUtil.buildSelectDialog(SelectChatActivity.this, "Send Image to chat : ", "", new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                    mView.show(getSupportFragmentManager(), "");

                    Log.e("File Path ",finalpath);
                    final ChatMsg chatMsg = chatMsgBuilder.createImageMsg(finalpath, "", false);
                    chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
//                            mView.dismiss();
                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            counter = prefs.getInt("shared_send_counter", counter);
                            counter = counter + 1;
                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                            editor.putInt("shared_send_counter", counter);
                            editor.apply();
                            chatService.observeProgressListener(new ProgressListener() {
                                @Override
                                public void progress(long l, int i, String s) {
                                    if (i < 100) {

                                    } else {
//                                    Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, true);
                            if (ChatMsgApi.isGroup(othersideid)) {
                                RequestHelper.getMemberList(othersideid, new RequestCallBack<List<Member>, Void, Void>() {
                                    @Override
                                    public void handleSuccess(List<Member> members, Void aVoid, Void aVoid2) {
                                        for (int i = 0; i < members.size(); i++) {
                                            if (members.get(i).getID() != 9151316648393693993L) {

                                                Log.e("Mem List", String.valueOf(members.get(i).getID()));
                                                Context contexts = getApplicationContext();
                                                user_id = String.valueOf(members.get(i).getID());
                                                String msg_id = String.valueOf(chatMsg.getMsgID());
                                                String read_status = "PR";
                                                String msg_type = "99";
//                                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

                                                Log.e("UCC Log", "code " + Utils.osType + "1601013 Success to Push group msg data into db");
                                            }
                                        }

                                    }
                                });
                            } else {
                                Context contexts = getApplicationContext();
                                user_id = String.valueOf(othersideid);
                                String msg_id = String.valueOf(chatMsg.getMsgID());
                                String read_status = "PA";
                                String msg_type = "99";
//                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                            }
                            Log.e("UCC Log", "code " + Utils.osType + "1601008 Success to send message");

                            Intent intent = new Intent(SelectChatActivity.this, Chat_RecyclerView_Activity.class);
                            intent.putExtra("othersideid",othersideid);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("UCC Log", "code " + Utils.osType + "1601009 Failed to send message");
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
            }, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Intent intent = new Intent(SelectChatActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    void handleSendVideo(Intent intent, long dest) {
        final long othersideid = dest;

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            final String finalpath = Utils.getFilePathFromURI(SelectChatActivity.this,imageUri);
            final ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(dest);

            DialogUtil.buildSelectDialog(SelectChatActivity.this, "Send Video to chat :  ", "", new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                    mView.show(getSupportFragmentManager(), "");

                    Log.e("File Path ",finalpath);
                    final ChatMsg chatMsg = chatMsgBuilder.createMiniVideoMsg(othersideid, false, finalpath, 200, 200, time);
                    chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
//                            mView.dismiss();
                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            counter = prefs.getInt("shared_send_counter", counter);
                            counter = counter + 1;
                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                            editor.putInt("shared_send_counter", counter);
                            editor.apply();
                            chatService.observeProgressListener(new ProgressListener() {
                                @Override
                                public void progress(long l, int i, String s) {
                                    if (i < 100) {

                                    } else {
//                                    Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, true);
                            if (ChatMsgApi.isGroup(othersideid)) {
                                RequestHelper.getMemberList(othersideid, new RequestCallBack<List<Member>, Void, Void>() {
                                    @Override
                                    public void handleSuccess(List<Member> members, Void aVoid, Void aVoid2) {
                                        for (int i = 0; i < members.size(); i++) {
                                            if (members.get(i).getID() != 9151316648393693993L) {

                                                Log.e("Mem List", String.valueOf(members.get(i).getID()));
                                                Context contexts = getApplicationContext();
                                                user_id = String.valueOf(members.get(i).getID());
                                                String msg_id = String.valueOf(chatMsg.getMsgID());
                                                String read_status = "PR";
                                                String msg_type = "99";
//                                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

                                                Log.e("UCC Log", "code " + Utils.osType + "1601013 Success to Push group msg data into db");
                                            }
                                        }

                                    }
                                });
                            } else {
                                Context contexts = getApplicationContext();
                                user_id = String.valueOf(othersideid);
                                String msg_id = String.valueOf(chatMsg.getMsgID());
                                String read_status = "PA";
                                String msg_type = "99";
//                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                            }
                            Log.e("UCC Log", "code " + Utils.osType + "1601008 Success to send message");

                            Intent intent = new Intent(SelectChatActivity.this, Chat_RecyclerView_Activity.class);
                            intent.putExtra("othersideid",othersideid);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("UCC Log", "code " + Utils.osType + "1601009 Failed to send message");
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
            }, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Intent intent = new Intent(SelectChatActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    void handleOtherMimeType(Intent intent, long dest) {
        final long othersideid = dest;

        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            final String finalpath = Utils.getFilePathFromURI(SelectChatActivity.this,imageUri);
            final ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(dest);

            DialogUtil.buildSelectDialog(SelectChatActivity.this, "Send File to chat :  ", "", new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                    mView.show(getSupportFragmentManager(), "");

                    Log.e("File Path ",finalpath);
                    final ChatMsg chatMsg = chatMsgBuilder.createFileMsg(finalpath);
                    chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
//                            mView.dismiss();
                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                            counter = prefs.getInt("shared_send_counter", counter);
                            counter = counter + 1;
                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                            editor.putInt("shared_send_counter", counter);
                            editor.apply();
                            chatService.observeProgressListener(new ProgressListener() {
                                @Override
                                public void progress(long l, int i, String s) {
                                    if (i < 100) {

                                    } else {
//                                    Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, true);
                            if (ChatMsgApi.isGroup(othersideid)) {
                                RequestHelper.getMemberList(othersideid, new RequestCallBack<List<Member>, Void, Void>() {
                                    @Override
                                    public void handleSuccess(List<Member> members, Void aVoid, Void aVoid2) {
                                        for (int i = 0; i < members.size(); i++) {
                                            if (members.get(i).getID() != 9151316648393693993L) {

                                                Log.e("Mem List", String.valueOf(members.get(i).getID()));
                                                Context contexts = getApplicationContext();
                                                user_id = String.valueOf(members.get(i).getID());
                                                String msg_id = String.valueOf(chatMsg.getMsgID());
                                                String read_status = "PR";
                                                String msg_type = "99";
//                                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

                                                Log.e("UCC Log", "code " + Utils.osType + "1601013 Success to Push group msg data into db");
                                            }
                                        }

                                    }
                                });
                            } else {
                                Context contexts = getApplicationContext();
                                user_id = String.valueOf(othersideid);
                                String msg_id = String.valueOf(chatMsg.getMsgID());
                                String read_status = "PA";
                                String msg_type = "99";
//                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                            }
                            Log.e("UCC Log", "code " + Utils.osType + "1601008 Success to send message");

                            Intent intent = new Intent(SelectChatActivity.this, Chat_RecyclerView_Activity.class);
                            intent.putExtra("othersideid",othersideid);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("UCC Log", "code " + Utils.osType + "1601009 Failed to send message");
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
            }, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    Intent intent = new Intent(SelectChatActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}




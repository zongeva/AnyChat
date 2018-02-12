package com.kpz.AnyChat.RecyclerViewChat;

/**
 * Created by Leo on 10/1/2017.
 */


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.kpz.AnyChat.Group_Chat.Group_Chat_Setting;
import com.kpz.AnyChat.Group_Chat.SelectGroupMemberTag;
import com.kpz.AnyChat.Home_Activity.HomeActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.ImageFilePath;
import com.kpz.AnyChat.Others.MaterialSimpleListAdapter;
import com.kpz.AnyChat.Others.MaterialSimpleListItem;
import com.kpz.AnyChat.Others.OnItemClickListener;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.Personal_Chat.Personal_Chat_Setting;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.listener.ProgressListener;
import com.vrv.imsdk.listener.ReceiveMsgListener;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.Member;
import com.vrv.imsdk.model.ResultCallBack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class Chat_RecyclerView_Activity extends AppCompatActivity {


    List<String> temp = new ArrayList<String>();
    List<String> mMember = new ArrayList<String>();
    public Map<Long, String> atUserMap = new HashMap<Long, String>();
    private List<ChatMsg> chatMsgList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewMultiChat_Adaptor mAdapter;
    int width, height, counter, clickcounter = 0, time, freeze_time, position, positions,refresh;
    long lastMsgID, othersideid, fwd_id, touchTime, msgid, senduser;
    public boolean canGetHistory = true, burnChat = false, stunt = false, blackout = false, tornado = false, eat_char_bool = false, next_msg_eat;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private MediaRecorder mr;

    Dialog dialog;
    Byte flag = 0;
    Context context;
    String user_id, AudioPath, pitch, next_msg_eat_char = "", eat_char = "";
    ;
    File file;
    Uri imageUri, videoUri;
    ImageButton btnOpenPopup;
    PopupWindow popupWindow;
    EditText txtpopup, inputwindow, useEdit;
    ArrayList<Long> handler = new ArrayList<Long>();
    List<Long> relatedUsers = new ArrayList<>();

    private long touchStart = 0l;
    private long touchEnd = 0l;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    static final int SELECT_CAPTURED_IMAGE = 0;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 2;
    static final int FILE_SELECT_CODE = 4;
    static final int MINI_VIDEO_REQUEST_CODE = 5;
    static final int SELECT_GROUP_MEMBER = 6;
    static final int SELECT_FOWARD_TARGET = 7;
    static final int SELECT_GROUP_MEMBERS = 8;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_chatbase);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText inputwindow = (EditText) findViewById(R.id.inputmessage);
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        final ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        final EditText txtpopup = (EditText) findViewById(R.id.messageEditText);
        final ImageButton send = (ImageButton) findViewById(R.id.imageButton3);
        final ImageButton voice_note = (ImageButton) findViewById(R.id.voice);
        final ImageView imageview = (ImageView) findViewById(R.id.recording);

        RequestHelper.login_status(Chat_RecyclerView_Activity.this);

        send.setOnClickListener(listener);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        long msgID = 0;
        int count = 100;
        int flag = 0;

        othersideid = getIntent().getExtras().getLong("othersideid");
        positions = getIntent().getExtras().getInt("positions");

        Log.e("Redirect Positions",positions+"");

        if (ChatMsgApi.isGroup(othersideid)) {
            setTitle("[Group] " + RequestHelper.getGroupInfo(othersideid).getName());
        } else {
            TextView temp = (TextView) findViewById(R.id.textView79);
            String urls = Utils.serverAddress + "retrieveusername?LinkdoodID=" + othersideid;
//            Utils.setNickname(getApplicationContext(), urls, othersideid + "", temp);
            setTitle(temp.getText().toString());
        }

        ClientManager.getDefault().getChatService().getMessages(othersideid, msgID, count, flag, new RequestCallBack<Long, List<ChatMsg>, Void>() {
                    @Override
                    public void handleSuccess(Long aLong, List<ChatMsg> chatMsgs, Void aVoid) {

                        chatMsgList = chatMsgs;
                        recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgs));
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                        Log.e("ERROR", refresh+"");

                        if (chatMsgs.size() != 0) {
                            chatMsgList = chatMsgs;
                            recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgs));
                           //Refresh function for revoke
//                            if (refresh == 1){
//                                chatMsgList = chatMsgs;
//                                recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgs));
//                                refresh = 0;
//                            }

                            if (positions != 0) {
                                chatMsgList = chatMsgs;
                                recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgs));
                                recyclerView.scrollToPosition(positions);

                                Log.e("Index", positions + " This part of code did run");
                            } else {
                                chatMsgList = chatMsgs;
                                recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgs));
                                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

                            }
                        }

                    }
                }

        );

        /*
        * Swipe Up to load message
        * */
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeView.isRefreshing() == true && chatMsgList != null && chatMsgList.size() > 0) {
                    lastMsgID = chatMsgList.get(0).getMsgID() - 1;
                    Log.e("UCC Log", "code: 1602013 Refresh Msg ID" + lastMsgID);
                    long TargetID = chatMsgList.get(0).getTargetID();
                    long SendUserID = chatMsgList.get(0).getFromID();
                    int flag = Constants.FLAG_UP;
                    swipeView.setRefreshing(false);
                    RequestHelper.getMessages(TargetID, Math.max(lastMsgID, 0), 30, flag, SendUserID, new RequestCallBack<Long, List<ChatMsg>, Void>() {
                        @Override
                        public void handleSuccess(Long aLong, List<ChatMsg> list, Void aVoid) {


                            if (list != null && list.size() > 0) {
                                canGetHistory = (10 == list.size());
                                swipeView.setRefreshing(false);
                            }

                            //缓存数据
                            for (int i = list.size() - 1; i >= 0; i--) {
                                chatMsgList.add(0, list.get(i));
                                recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgList));
                                recyclerView.scrollToPosition(30);
                                swipeView.setRefreshing(false);
                            }
                        }

                        @Override
                        public void handleFailure(int code, String message) {
                            swipeView.setRefreshing(false);

                        }
                    });
                } else {
                    swipeView.setRefreshing(false);
                }
            }
        });
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        btnOpenPopup = (ImageButton) findViewById(R.id.openpopup);
        btnOpenPopup.setOnClickListener(new Button.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater
                        = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popout, null);
                popupWindow = new PopupWindow(popupView, width, height, true);
                popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    return;
                } else {
                    popupWindow.showAtLocation(btnOpenPopup, Gravity.BOTTOM, 0, 80);
                }

            }
        });

        txtpopup.setFocusable(false);
        txtpopup.setOnClickListener((new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                inputwindow.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Chat_RecyclerView_Activity.this.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                inputwindow.setVisibility(View.VISIBLE);
                back_btn.setVisibility(View.VISIBLE);
                inputwindow.setFocusableInTouchMode(true);
                inputwindow.requestFocus();

            }
        }));
        inputwindow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (count > 0) {// 文本框输入内容
//                    String substring = s.toString().substring(start, start + count);
//                    if (ChatMsgApi.isGroup(othersideid) && substring.equals("@")) {
//                        useEdit = inputwindow;
//                        hideSoftKeyboard();
//                        Intent intent = new Intent(Chat_RecyclerView_Activity.this, SelectGroupMemberTag.class);
//                        intent.putExtra("user_group_id", othersideid);
//                        startActivityForResult(intent, SELECT_GROUP_MEMBERS);
//                    }
//                    txtpopup.setText(s);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                inputwindow.setVisibility(GONE);
                back_btn.setVisibility(GONE);
            }
        });


        /*
        * OnReceive Listener
        * To Process Freeze, Blackout , Burn Chat
        * */
        chatService.observeReceiveListener(othersideid, new ReceiveMsgListener() {
            @Override
            public void receive(ChatMsg chatMsg) {

                chatMsgList.add(chatMsg);
                recyclerView.setAdapter(new RecyclerViewMultiChat_Adaptor(Chat_RecyclerView_Activity.this,chatMsgList));
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                mAdapter.notifyDataSetChanged();

                final ImageView freezes = (ImageView) findViewById(R.id.freeze);
                final ChatMsg tempmsg = chatMsg;
                if (chatMsg.isBurn() == true) {

                    Log.e("test burn", "received burn chat from listener");
//                    Toast.makeText(getApplicationContext(), "Burn Chat !", Toast.LENGTH_SHORT).show();


                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long time = millisUntilFinished / 1000;
                            Log.e("Burn", "Burning in " + String.valueOf(time) + " second");
                        }

                        @Override
                        public void onFinish() {
                            List<Long> listMsgID = new ArrayList<Long>();
                            listMsgID.add(tempmsg.getMsgID());
                            chatService.deleteMsgByID(othersideid, listMsgID, new ResultCallBack() {
                                @Override
                                public void onSuccess(Object o, Object o2, Object o3) {
                                    chatMsgList.remove(chatMsgList.size() - 1);
                                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                   //mAdapter.notifyDataSetChanged();
                                    //recyclerView.refreshDrawableState();
                                    //need to refresh/renew
                                    Log.e("UCC Log", "code " + Utils.osType + "1602001 Success to delete burn chat.");


                                    //reload recycler view
                                    Intent intent = new Intent(Chat_RecyclerView_Activity.this, Chat_RecyclerView_Activity.class);
                                    intent.putExtra("othersideid", chatMsgList.get(position).getID());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(int i, String s) {
                                    Log.e("UCC Log", "code " + Utils.osType + "1602002 Failed to delete burn chat.");
                                }
                            });
                        }
                    }.start();
                }


                String pre = chatMsg.getPreDefined();
                for (int i = 0; i < chatMsg.getRelatedUsers().size(); i++) {
                    Long exp = chatMsg.getRelatedUsers().get(i);
                    Log.e("CA Related User", exp + "");

                    if (pre != "" && pre != null) {

                        String[] split = pre.split(";");

                        /*
                        * Special Function Section
                        * 1 = Stunt
                        * 2 = Blackout
                        * 3 = Voice CHange
                        * 4 = Tornado
                         */
//                        Log.e("Pre_Test", split[1] + "");
                        Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
                        int item_count = chat.getUnreadCount();


                        if (split.length > 2) {
                            if (split[1].equals("1") && RequestHelper.isMyself(exp) && split.length > 0) {//Type = Stunt
                                freeze_time = freeze_time + 1;
                                Log.e("Freeze1", freeze_time + "");
                                new CountDownTimer(5000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                        long time = millisUntilFinished / 1000;
                                        Toast.makeText(getApplicationContext(), "Freeze !!", Toast.LENGTH_SHORT).show();
                                        freezes.setVisibility(View.VISIBLE);
                                        freezes.setImageResource(R.drawable.freeze);
                                        freezes.setBackground(getResources().getDrawable(R.color.white));
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }

                                    @Override
                                    public void onFinish() {
                                        relatedUsers.clear();
                                        Log.e("Text Related Cleared : ", relatedUsers + "");
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        Toast.makeText(getApplicationContext(), "Times Up ! You Can Move Now!", Toast.LENGTH_SHORT).show();
                                        freezes.setVisibility(View.GONE);
                                    }
                                }.start();


                            }//end of stunt function
                            else if (split[1].equals("2") && RequestHelper.isMyself(exp)) {//Type = Blackout

                                new CountDownTimer(5000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        long time = millisUntilFinished / 1000;
//                                    Toast.makeText(getApplicationContext(), "Blackout !!", Toast.LENGTH_SHORT).show();
                                        freezes.setImageResource(R.drawable.blackedout);
                                        freezes.setBackground(getResources().getDrawable(R.color.vim_black));
                                        freezes.setVisibility(View.VISIBLE);
                                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }

                                    @Override
                                    public void onFinish() {
                                        relatedUsers.clear();
                                        Log.e("Text Related Cleared : ", relatedUsers + "");
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                    Toast.makeText(getApplicationContext(), "Someone Open The Light For You", Toast.LENGTH_SHORT).show();
                                        freezes.setVisibility(View.GONE);
                                    }
                                }.start();


                            } else if (split[1].equals("4") && RequestHelper.isMyself(exp)) {//Type = Blackout

                                //do nothing, Process in ConversationHistoryArrayAdapter

                            } else if (split[1].equals("0") && RequestHelper.isMyself(exp)) {
                                //eat char
                                next_msg_eat_char = split[2];
                                next_msg_eat = true;
                                Log.e("UCC Log", "code: 1602011 next_msg_eat set to: TRUE");
                                SharedPreferences sharedPref = Chat_RecyclerView_Activity.this.getPreferences(Chat_RecyclerView_Activity.this.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
//                                editor.putBoolean(getString(R.string.next_msg_eat), next_msg_eat);
                                editor.commit();
                                Log.e("UCC Log", "code: 1602012 saved next_msg_eat = TRUE in shared preference.");
                                //Toast.makeText(ChatActivity.this, next_msg_eat_char, Toast.LENGTH_SHORT).show();
                            }
                        } else {


                        }
                    }

                }
            }


            @Override
            public void update(ChatMsg chatMsg) {
                mAdapter.notifyDataSetChanged();
            }
        });


//=========================================AUDIO RECORDING EVENT=========================================
        voice_note.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                imageview.setVisibility(View.VISIBLE);
//                mr = new MediaRecorder();
//                mr.setOnInfoListener(new MediaRecorder.OnInfoListener() {
//                    @Override
//                    public void onInfo(MediaRecorder mr, int what, int extra) {
//                    }
//                });
//                mr.setAudioSource(MediaRecorder.AudioSource.MIC);
//                mr.setOutputFormat(MediaRecorder.OutputFormat.3);
//                mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                AudioPath = Environment.getExternalStorageDirectory() + "/audio" + String.valueOf(System.currentTimeMillis()) + ".m4a";
//                mr.setOutputFile(AudioPath);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        AudioPath = Environment.getExternalStorageDirectory() + "/audio" + String.valueOf(System.currentTimeMillis()) + ".aac";


                        file = new File(AudioPath);
                        mr = new MediaRecorder();
                        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
//                        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        mr.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
                        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mr.setOutputFile(AudioPath);
                        Log.e("AudioPath", AudioPath);
                        try {
                            mr.prepare();
                        } catch (IllegalStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mr.start();
//                            Toast.makeText(getApplicationContext(), "Start Recording , Swipe up to cancel", Toast.LENGTH_SHORT).show();
                        touchStart = System.currentTimeMillis();


                        break;

                    case MotionEvent.ACTION_UP:
                        clickcounter = clickcounter + 1;
                        imageview.setVisibility(GONE);

                        if (mr != null) {
                            try {
                                Context cont = context;
                                mr.stop();
                                mr.reset();
//                                mr.release();
                                mr = null;

                            } catch (Exception z) {
                                Log.e("Fail To Record", z.toString());
                            }


                            float DownX = 0;
                            float DownY = 0;
                            float moveX = Math.abs(event.getX() - DownX);
                            float moveY = Math.abs(event.getY() - DownY);
                            if (moveY > 300) {
                                Toast.makeText(getApplicationContext(), "You Cancelled the record", Toast.LENGTH_SHORT).show();
                                imageview.setVisibility(GONE);
                                break;
                            }
                            touchEnd = System.currentTimeMillis();
                            touchTime = touchEnd - touchStart;
                            long doneButtonClickTime = SystemClock.elapsedRealtime();
                            long showStart = System.currentTimeMillis();

                            if (touchTime < 1500) {
                                if (AudioPath != null) {
                                    File file = new File(AudioPath);
                                    file.delete();
                                }

                                Toast toast_short = Toast.makeText(getApplicationContext(), "Duration Too Short , Please Record Again", Toast.LENGTH_SHORT);
                                if (clickcounter < 2) {
                                    toast_short.show();
                                } else if (clickcounter == 10) {
                                    clickcounter = 0;
                                } else {
                                    toast_short.cancel();
                                    imageview.setVisibility(GONE);
                                }

                                imageview.setVisibility(GONE);

                                break;
                            } else {
                                clickcounter = 0;
                                //successfully obtain audio file & path
                                Log.e("UCC Log Audio Record ", AudioPath);
                                imageview.setVisibility(GONE);

                                ChatMsgBuilder builder = new ChatMsgBuilder(othersideid);
                                final ChatMsg chatMsg = builder.createAudioMsg(AudioPath, touchTime);

                                final MaterialSimpleListAdapter adapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {


                                    @Override
                                    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                                        switch (index) {
                                            case 0:
                                                new MaterialDialog.Builder(Chat_RecyclerView_Activity.this)
                                                        .title("Confirm")
                                                        .content("Do You Want To Apply Robot Effect?")
                                                        .positiveText("Yes")
                                                        .negativeText("Cancel")
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                pitch = "name;3;0.9f";
                                                                chatMsg.setPreDefined(pitch);
                                                                Toast.makeText(getApplicationContext(), "Robot Effect Chosen", Toast.LENGTH_SHORT).show();
                                                                chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                                                                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                                                        counter = prefs.getInt("shared_send_counter", counter);
                                                                        counter = counter + 1;
                                                                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                                                                        editor.putInt("shared_send_counter", counter);
                                                                        editor.apply();
                                                                        Log.e("UCC Log", "code " + Utils.osType + "1602003 Audio Send success");
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
//                                                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

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
//                                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                                                            Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {
                                                                        Log.e("UCC Log", "code " + Utils.osType + "1602004 Audio Send error");
                                                                    }
                                                                }, new ResultCallBack<Integer, Integer, String>() {
                                                                    @Override
                                                                    public void onSuccess(Integer integer, Integer integer2, String s) {
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {
                                                                    }
                                                                });
                                                                //success, write intent to pass "audio path" to elsewhere
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                //Cancel
                                                            }
                                                        })
                                                        .show();
                                                break;


                                            case 1:
                                                new MaterialDialog.Builder(Chat_RecyclerView_Activity.this)
                                                        .title("Confirm")
                                                        .content("Do You Want To Apply Girl Effect?")
                                                        .positiveText("Yes")
                                                        .negativeText("Cancel")
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                pitch = "name;3;1.2f";
                                                                chatMsg.setPreDefined(pitch);
                                                                Toast.makeText(getApplicationContext(), "Girl Effect Chosen", Toast.LENGTH_SHORT).show();
                                                                chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                                                                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                                                        counter = prefs.getInt("shared_send_counter", counter);
                                                                        counter = counter + 1;
                                                                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                                                                        editor.putInt("shared_send_counter", counter);
                                                                        editor.apply();
                                                                        Log.e("UCC Log", "code " + Utils.osType + "1602003 Audio Send success");
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
//                                                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

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
//                                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                                                            Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {
                                                                        Log.e("UCC Log", "code " + Utils.osType + "1602004 Audio Send error");
                                                                    }
                                                                }, new ResultCallBack<Integer, Integer, String>() {
                                                                    @Override
                                                                    public void onSuccess(Integer integer, Integer integer2, String s) {
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {
                                                                    }
                                                                });
                                                                //success, write intent to pass "audio path" to elsewhere
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                //Cancel
                                                            }
                                                        })
                                                        .show();
                                                break;
                                            case 2:
                                                new MaterialDialog.Builder(Chat_RecyclerView_Activity.this)
                                                        .title("Confirm")
                                                        .content("No Effect Chosen")
                                                        .positiveText("Yes")
                                                        .negativeText("Cancel")
                                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                                                Toast.makeText(getApplicationContext(), "No Effect Chosen", Toast.LENGTH_SHORT).show();
                                                                chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                                                                        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                                                        counter = prefs.getInt("shared_send_counter", counter);
                                                                        counter = counter + 1;
                                                                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, getApplicationContext().MODE_PRIVATE).edit();
                                                                        editor.putInt("shared_send_counter", counter);
                                                                        editor.apply();
                                                                        Log.e("UCC Log", "code " + Utils.osType + "1602003 Audio Send success");
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
//                                                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

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
//                                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                                                            Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {
                                                                        Log.e("UCC Log", "code " + Utils.osType + "1602004 Audio Send error");
                                                                    }
                                                                }, new ResultCallBack<Integer, Integer, String>() {
                                                                    @Override
                                                                    public void onSuccess(Integer integer, Integer integer2, String s) {
                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {
                                                                    }
                                                                });
                                                                //success, write intent to pass "audio path" to elsewhere
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                //Cancel
                                                            }
                                                        })
                                                        .show();
                                                break;

                                        }
                                        dialog.dismiss();
                                    }
                                });
//                                    chatMsg.setPreDefined(pitch);

                                adapter.add(new MaterialSimpleListItem.Builder(Chat_RecyclerView_Activity.this)
                                        .content("Robot")
                                        .backgroundColor(Color.WHITE)
                                        .build());

                                adapter.add(new MaterialSimpleListItem.Builder(Chat_RecyclerView_Activity.this)
                                        .content("Girl")
                                        .backgroundColor(Color.WHITE)
                                        .build());

                                adapter.add(new MaterialSimpleListItem.Builder(Chat_RecyclerView_Activity.this)
                                        .content("Normal Sound")
                                        .backgroundColor(Color.WHITE)
                                        .build());

                                new MaterialDialog.Builder(Chat_RecyclerView_Activity.this)
                                        .adapter(adapter, null)
                                        .show();

                            }
                        }

                        break;
                }
                return false;
            }
        });
        //END OF AUDIO RECORDING PART




//
    } //TODO End of OnCreate

    /*
    * MULTIMEDIA FUNCTION SECTION
    * */
    public void onclickcamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = Uri.fromFile(new File("/sdcard/Camerafolder/" + "fname_" +
                String.valueOf(System.currentTimeMillis()) + ".jpg"));
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void onclickvideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        videoUri = Uri.fromFile(new File("/sdcard/Camerafolder/"
                + "/vname_" + String.valueOf(System.currentTimeMillis()) + ".mp4"));
        Log.e("Video URI ", videoUri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    public void onclickimage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_CAPTURED_IMAGE);
    }

    public void onclickattachment(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getApplicationContext(), "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (imageUri != null) {
                    Log.e("URI", imageUri.toString());
                    ChatMsgBuilder builder = new ChatMsgBuilder(othersideid);
                    String location = imageUri.toString();
                    location = location.replace("file:///", "");
                    final ChatMsg chatMsg = builder.createImageMsg(location, "", false);

                    if (burnChat == true && imageUri != null) {
                        chatMsg.setBurn(true);
                    } else {
//                    chatMsg.setBurn(false);
                        // Not necessary since nothing to set with
                    }
                    chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
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
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT);
                }
                break;

            case SELECT_CAPTURED_IMAGE:
                String selectedImagePath;
                Uri selectedImageUri = data.getData();
                ChatMsgBuilder builder = new ChatMsgBuilder(othersideid);
                selectedImagePath = ImageFilePath.getPath(this, selectedImageUri);
                Log.e("Image File Path", "" + selectedImagePath);

                final ChatMsg chatMsg = builder.createImageMsg(selectedImagePath, "Selected Image", false);
                if (burnChat == true) {
                    chatMsg.setBurn(true);
                }
                new MaterialDialog.Builder(this)
                        .title("Image Selected")
                        .content("Confirm to send selected photo?")
                        .positiveText("Yes")
                        .negativeText("Cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {

                                    @Override
                                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
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
//                                Toast.makeText(getApplicationContext(), "Image Upload Successful", Toast.LENGTH_SHORT).show();
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
//                                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

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
//                                            Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                            Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                                        }
                                        Log.e("UCC Log", "code " + Utils.osType + "1602007 Image send success");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("UCC Log", "code " + Utils.osType + "1602008 Image send error");
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
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                //do nothing
                            }
                        })
                        .show();
                break;

            case CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Log.e("URI", videoUri.toString());
                    builder = new ChatMsgBuilder(othersideid);
                    String location = videoUri.toString();
                    location = location.replace("file:///", "");
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    String times = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    time = 1000;
                    final ChatMsg chatMsgs = builder.createMiniVideoMsg(othersideid, flag, location, 200, 200, time);


                    if (burnChat == true) {
                        chatMsgs.setBurn(true);
                    } else {
                        chatMsgs.setBurn(false);
                    }

                    chatService.sendMsg(chatMsgs, new ResultCallBack<Void, Void, Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
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
//                                    Toast.makeText(getApplicationContext(), "Video Upload Successful", Toast.LENGTH_SHORT).show();
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
                                                String msg_id = String.valueOf(chatMsgs.getMsgID());
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
                                String msg_id = String.valueOf(chatMsgs.getMsgID());
                                String read_status = "PA";
                                String msg_type = "99";
//                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                            }
                            Log.e("UCC Log", "code " + Utils.osType + "1602005 Video send success");
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("UCC Log", "code " + Utils.osType + "1602006 Video Send error");
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
                break;
            case FILE_SELECT_CODE:

                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.e("UCC Log File URI: ", uri.toString());
                    String path = ImageFilePath.getPath(this, uri);

                    builder = new ChatMsgBuilder(othersideid);
                    final ChatMsg chatMsgFile = builder.createFileMsg(path);
                    if (burnChat == true) {

                        chatMsgFile.setBurn(true);

                    } else {
                    }
                    new MaterialDialog.Builder(this)
                            .title("File Selected")
                            .content("Confirm to send selected file?")
                            .positiveText("Yes")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    chatService.sendMsg(chatMsgFile, new ResultCallBack<Void, Void, Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
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
//                                    Toast.makeText(getApplicationContext(), "File Upload Successful", Toast.LENGTH_SHORT).show();
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
                                                                String msg_id = String.valueOf(chatMsgFile.getMsgID());
                                                                String read_status = "PR";
                                                                String msg_type = "99";
//                                                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

                                                                Log.e("UCC Log", "code " + Utils.osType + "1601013 Success to Push group msg data into db");
                                                            }
                                                        }

                                                    }
                                                });
                                            } else {
                                                Context contexts = getApplicationContext();
                                                user_id = String.valueOf(othersideid);
                                                String msg_id = String.valueOf(chatMsgFile.getMsgID());
                                                String read_status = "PA";
                                                String msg_type = "99";
//                                                Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                                Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                                            }
                                            Log.e("UCC Log", "code " + Utils.osType + "1602009 File send success");
                                        }

                                        @Override
                                        public void onError(int i, String s) {
                                            Log.e("UCC Log", "code " + Utils.osType + "1602010 File send failed");
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
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    //do nothing
                                }
                            })
                            .show();
                }
                break;
            case SELECT_GROUP_MEMBER:
                ImageView back_btn = (ImageView) findViewById(R.id.back_btn);

                    try{
                        handler = (ArrayList<Long>) data.getSerializableExtra("data");
                    }catch (Exception e){
                        Toast.makeText(Chat_RecyclerView_Activity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    }

                    Log.e("Set RelatedUser : ", handler + "");
                    inputwindow.setVisibility(GONE);
                    back_btn.setVisibility(GONE);
                    ImageButton btn = (ImageButton) findViewById(R.id.imageButton3);
                    btn.performClick();
                    break;





            case SELECT_GROUP_MEMBERS:
                hideSoftKeyboard();
                back_btn = (ImageView) findViewById(R.id.back_btn);
                inputwindow = (EditText) findViewById(R.id.inputmessage);

                try{
                    handler = (ArrayList<Long>) data.getSerializableExtra("data");
                }catch (Exception e){
                    Toast.makeText(Chat_RecyclerView_Activity.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
                Log.e("Set RelatedUser : ", handler + "");
                inputwindow.setVisibility(GONE);
                back_btn.setVisibility(GONE);
                TextView nameholder = (TextView) findViewById(R.id.temps);
                ArrayList<String> list = new ArrayList<String>();

                for (int i = 0; i < handler.size(); i++) {
                    String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + handler.get(i);
//                    Utils.setNickname(Chat_RecyclerView_Activity.this, url, handler.get(i) + "", nameholder);
                    list.add(i, "@" + nameholder.getText().toString());
                    String clear = list.toString().replace("[", "").replace("]", "");
                    inputwindow.setText(clear);
                }
        }
    }

    @Override
    public void onBackPressed() {
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        inputwindow = (EditText) findViewById(R.id.inputmessage);

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
//            Toast.makeText(getBaseContext(), "Tap Twice To Exit", Toast.LENGTH_SHORT).show();
            if (inputwindow.getVisibility() == View.VISIBLE) {
                inputwindow.setVisibility(View.INVISIBLE);
                back_btn.setVisibility(View.INVISIBLE);
            }
            super.onBackPressed();
            return;

        } else {
            if (inputwindow.getVisibility() == View.VISIBLE) {
                inputwindow.setVisibility(View.INVISIBLE);
                back_btn.setVisibility(View.INVISIBLE);
            } else {
                Intent intent = new Intent(Chat_RecyclerView_Activity.this, HomeActivity.class);
                startActivity(intent);
            }
        }
        mBackPressed = System.currentTimeMillis();
    }

    public void toggleBurnChat(View view) {
        if (burnChat == true) {
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);
            txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
            inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
            txtpopup.setHint("Type a message");
            inputwindow.setHint("Type a message");
            burnChat = false;
            stunt = false;
            blackout = false;
            tornado = false;
            popupWindow.dismiss();

//            Toast.makeText(this, "Burnchat", Toast.LENGTH_SHORT).show();
        } else {
            burnChat = true;
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);
            txtpopup.setBackground(getDrawable(R.drawable.layout_burn));
            inputwindow.setBackground(getDrawable(R.drawable.layout_burn));
            popupWindow.dismiss();


//            Intent intent = new Intent( Chat_RecyclerView_Activity.this, SelectGroupMemberTag.class);
//            intent.putExtra("user_group_id", othersideid);
//            startActivityForResult(intent, SELECT_GROUP_MEMBER);


            txtpopup.setHint("Burn Chat Enabled");
//            inputwindow.setHint("@Target");
//            inputwindow.setText("BurnChat Sent");
//            Toast.makeText(this, "Burnchat", Toast.LENGTH_SHORT).show();
        }
    }

    public void enable_freeze(View view) {

        if (stunt == true) {
            btnOpenPopup = (ImageButton) findViewById(R.id.openpopup);
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);

            burnChat = false;
            stunt = false;
            blackout = false;
            tornado = false;

            txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
            inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
            txtpopup.setHint("Ab");
            inputwindow.setHint("Ab");
            popupWindow.dismiss();
//            Toast.makeText(getApplicationContext(), "Freeze", Toast.LENGTH_SHORT).show();
        } else {
            btnOpenPopup = (ImageButton) findViewById(R.id.openpopup);
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);

            txtpopup.setBackground(getDrawable(R.drawable.layout_bg));
            inputwindow.setBackground(getDrawable(R.drawable.layout_bg));
            stunt = true;
            popupWindow.dismiss();
            useEdit = inputwindow;
            inputwindow.setText("");
            Intent intent = new Intent(Chat_RecyclerView_Activity.this, SelectGroupMemberTag.class);
            intent.putExtra("user_group_id", othersideid);
            startActivityForResult(intent, SELECT_GROUP_MEMBER);

            txtpopup.setHint("@Freeze Target");
            inputwindow.setText("[Freeze Effect Sent]");
        }


    }

    public void enable_blackout(View view) {
        if (blackout == true) {
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);
            burnChat = false;
            stunt = false;
            blackout = false;
            tornado = false;

            txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
            inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
            txtpopup.setHint("Type a message");
            inputwindow.setHint("Type a message");
            popupWindow.dismiss();
        } else {
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);
            txtpopup.setBackground(getDrawable(R.drawable.layout_blackout));
            inputwindow.setBackground(getDrawable(R.drawable.layout_blackout));
            blackout = true;
            popupWindow.dismiss();
            inputwindow.setText("");
            Intent intent = new Intent(Chat_RecyclerView_Activity.this, SelectGroupMemberTag.class);
            intent.putExtra("user_group_id", othersideid);
            startActivityForResult(intent, SELECT_GROUP_MEMBER);
            txtpopup.setHint("Type a message");
            inputwindow.setText("Type a message");

        }

    }

    public void go_eat_func(View view) {
        if (!eat_char_bool) {
            eat_char_bool = true;
            dialog = new Dialog(this);
//            dialog.setContentView(R.layout.eater_dialog);
            dialog.setTitle("Alphabet Eater");

//            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonClose);


//            dialogButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    eat_char_bool = false;
//                    dialog.dismiss();
//
//                }
//            });
//            dialog.show();

        } else {
            eat_char_bool = false;
            Toast.makeText(Chat_RecyclerView_Activity.this, "Alphabet Eater disabled.", Toast.LENGTH_SHORT).show();
        }
    }

    public void eatCharacter(View view) {
        String selectedChar = "";

        Button btn_a = (Button) dialog.findViewById(view.getId());
        selectedChar = btn_a.getText().toString();
        eat_char = selectedChar;

        Toast.makeText(getApplicationContext(), selectedChar + " is selected", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        popupWindow.dismiss();


    }

    public void enable_tornado(View view) {
        btnOpenPopup = (ImageButton) findViewById(R.id.openpopup);
        txtpopup = (EditText) findViewById(R.id.messageEditText);
        inputwindow = (EditText) findViewById(R.id.inputmessage);

        if (tornado == true) {
            tornado = false;
            txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
            inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
            txtpopup.setHint("Type a message");
            inputwindow.setHint("Type a message");
            popupWindow.dismiss();
        } else {
            txtpopup.setBackground(getDrawable(R.drawable.layout_bg));
            inputwindow.setBackground(getDrawable(R.drawable.layout_bg));
            tornado = true;
            popupWindow.dismiss();
            inputwindow.setText("");
            Intent intent = new Intent(Chat_RecyclerView_Activity.this, SelectGroupMemberTag.class);
            intent.putExtra("user_group_id", othersideid);
            startActivityForResult(intent, SELECT_GROUP_MEMBER);
            txtpopup.setHint("Type a message");
            inputwindow.setText("Type a message");

        }
    }

    @Override
    protected void onResume() {
//        /*
//        * Reply Click to redirect function
//        * */
        super.onResume();
//        long msgID = 0;
//        int count = 100;
//        int flags = 0;
//        othersideid = getIntent().getExtras().getLong("othersideid");
//        Log.e("othersideid",othersideid+"");
//        positions = getIntent().getExtras().getInt("positions");
//
//        msgid = getIntent().getExtras().getLong("msgid");
//        senduser = getIntent().getExtras().getLong("senduser");
//
//        ClientManager.getDefault().getChatService().getMessages(othersideid, msgID, count, flag, new RequestCallBack<Long, List<ChatMsg>, Void>() {
//                    @Override
//                    public void handleSuccess(Long aLong, List<ChatMsg> chatMsgs, Void aVoid) {
//                        if (chatMsgs.size() != 0) {
//                            chatMsgList = chatMsgs;
//                            recyclerView.setAdapter(mAdapter);
//
//                            if (positions != 0) {
//                                recyclerView.setAdapter(mAdapter);
//                                recyclerView.scrollToPosition(positions);
//
//                                Log.e("Index", positions + " This part of code did run");
//                            } else {
//                                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//
//                            }
//                        }
//
//                        chatMsgList = chatMsgs;
//                        recyclerView.setAdapter(new RecyclerViewMultiChat(chatMsgs));
//                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
//                    }
//                }
//
//        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (ChatMsgApi.isGroup(othersideid)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_group, menu);
        } else { // return personal chat setting}
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_personal, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (ChatMsgApi.isGroup(othersideid)) {

            if (id == R.id.group_setting) {
                hideSoftKeyboard();
                Intent intent = new Intent(this, Group_Chat_Setting.class);
                intent.putExtra("user_group_id", othersideid);
                startActivity(intent);

            }
        } else {

            if (id == R.id.chat_setting) {
                hideSoftKeyboard();
                Intent intent = new Intent(this, Personal_Chat_Setting.class);
                intent.putExtra("othersideid", othersideid);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Message send function
    * */
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            boolean special = false;
            ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(othersideid);
            final EditText message = (EditText) findViewById(R.id.inputmessage);
            final EditText message2 = (EditText) findViewById(R.id.messageEditText);
            final ChatMsg chatMsg = chatMsgBuilder.createTxtMsg(message.getText().toString());

            chatMsg.setRelatedUsers(handler);
            Log.e("Text Related User : ", handler + "");

            ArrayList<Long> relaterUsers = new ArrayList<>();
            if (atUserMap != null && !atUserMap.isEmpty()) {
                for (Map.Entry<Long, String> entry : atUserMap.entrySet()) {
                    relaterUsers.add(entry.getKey());
                }
            }
/*
        * Set Default PreDefined when no effect selected
        * MsgStatus Check Type = 10
        * Dummy Data Type = 98
        * Void Data Type = 99
        * */

            if (message.getText().length() != 0) {
                if (burnChat == true) {
                    special = true;
                    chatMsg.setBurn(true);
                    burnChat = false;
                    stunt = false;
                    blackout = false;
                    tornado = false;
                    txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
                    inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
                    //chatMsg.setPreDefined("name;" + "98" + ";5;" + ";unread;" + mMember);

                } else if (burnChat == false) {
                    chatMsg.setBurn(false);
                    special = false;

                }
                String name;
                String type = "";
                String sec;

                if (stunt == true) {
                    chatMsg.setBurn(false);
                    special = true;
                    type = "1";
                    chatMsg.setPreDefined("name;" + type + ";5;" + ";unread;" + mMember);
                    burnChat = false;
                    stunt = false;
                    blackout = false;
                    tornado = false;
                    txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
                    inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));

                } else {
                    type = "";
                    special = false;
                }

                if (blackout == true) {
                    chatMsg.setBurn(false);
                    special = true;
                    type = "2";
                    chatMsg.setPreDefined("name;" + type + ";5" + ";unread;" + mMember);
                    burnChat = false;
                    stunt = false;
                    blackout = false;
                    tornado = false;
                    txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
                    inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
                } else {
                    type = "";
                    special = false;
                }
                if (tornado == true) {
                    chatMsg.setBurn(false);
                    special = true;
                    type = "4";
                    chatMsg.setPreDefined("name;" + type + ";5" + ";unread;" + mMember);
                    burnChat = false;
                    stunt = false;
                    blackout = false;
                    tornado = false;
                    txtpopup.setBackground(getDrawable(R.drawable.layout_bgs));
                    inputwindow.setBackground(getDrawable(R.drawable.layout_bgs));
                } else {
                    type = "";
                    special = false;
                }

                if (eat_char_bool == true) {
                    chatMsg.setBurn(false);
                    special = true;
                    //execute eat char
                    type = "0";
                    burnChat = false;
                    stunt = false;
                    blackout = false;
                    tornado = false;
                    eat_char_bool = false;
                    chatMsg.setPreDefined("name;" + type + ";" + eat_char + ";unread;" + mMember);
                } else {
                    eat_char = "";
                    special = false;
                    type = "";
                }

                if (next_msg_eat == true) {
                    next_msg_eat = false;
                    special = true;
                    chatMsg.setBurn(false);
                    burnChat = false;
                    stunt = false;
                    blackout = false;
                    tornado = false;
                    eat_char_bool = false;
                    //to be change
                    //Toast.makeText(getApplicationContext(), next_msg_eat_char, Toast.LENGTH_SHORT).show();
                    String msg2eat = chatMsg.getBody();
                    msg2eat = msg2eat.replaceAll(next_msg_eat_char, "*");
                    msg2eat = msg2eat.replaceAll(next_msg_eat_char.toUpperCase(), "*");
                    chatMsg.setBody("");
                    chatMsg.setBody(msg2eat);
                }

                Log.e("Predefined = ", chatMsg.getPreDefined() + " 1601011 Success to send predefined");
                final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                final TextView dum = (TextView) findViewById(R.id.textView78);
                final TextView dum2 = (TextView) findViewById(R.id.textView79);
                String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
                String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
                String url_get_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
                Log.e("UCC Log", "code: : 1602015 Token URL" + url_get_token);
//                final Http_GetToken gt = new Http_GetToken(Chat_RecyclerView_Activity.this, 10, url_get_token, ids, dum, deviceId, "4", "", "");
                if (dum2.getText().toString().equals("")) {
                    String url_join_group = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//                    Http_GetToken cb = new Http_GetToken(Chat_RecyclerView_Activity.this, 9, url_join_group, ids, dum2, deviceId, "", "", "");
//                    cb.execute();
                }
                if (!chatMsg.getPreDefined().equals("") || burnChat == true) {
                    Log.e("Message", "Special");

                    new MaterialDialog.Builder(Chat_RecyclerView_Activity.this)
                            .title("Alert")
                            .content("By Using Special Effect, You'll spent 1 MP")
                            .positiveText("Agree")
                            .negativeText("Cancel")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                    gt.execute();
                            Log.e("ERROR","Energy Check "+ dum.getText().toString());
                                    if (dum.getText().toString().equals("SUCCESS")) {

                                        chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                                                final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//                                        Toast.makeText(getApplicationContext(), "Remaining : " + dum2.getText().toString(), Toast.LENGTH_SHORT).show();
                                                temp.clear();
                                                mMember.clear();
                                                relatedUsers.clear();
                                                message.setText("");
                                                eat_char = "";

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
//                                                                    Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);

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
//                                                    Utils.setNewMsg(getApplicationContext(), user_id, msg_id, read_status, msg_type);
                                                    Log.e("UCC Log", "code " + Utils.osType + "1601014 Success to Push personal msg data into db");

                                                }
                                                Log.e("UCC Log", "code " + Utils.osType + "1601010 Success to send message");
                                            }

                                            @Override
                                            public void onError(int i, String s) {
                                                message.setText("");
                                                eat_char = "";
                                                Log.e("UCC Log", "code " + Utils.osType + "1601011 Fail to send message");
                                            }
                                        }, new ResultCallBack<Integer, Integer, String>() {
                                            @Override
                                            public void onSuccess(Integer integer, Integer integer2, String s) {
                                                if (atUserMap != null) {
                                                    atUserMap.clear();
                                                }
                                            }

                                            @Override
                                            public void onError(int i, String s) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Insufficient Balance", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).show();

                } //Not Special Message
                else {
                    Log.e("Message", "Normal");
                    chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                            final SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);


                            mMember.clear();
                            handler.clear();
                            message.setText("");
                            eat_char = "";

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
                            Log.e("UCC Log", "code " + Utils.osType + "1601010 Success to send message");

                        }

                        @Override
                        public void onError(int i, String s) {
                            message.setText("");
                            eat_char = "";
                            Log.e("UCC Log", "code " + Utils.osType + "1601011 Fail to send message");
                        }
                    }, new ResultCallBack<Integer, Integer, String>() {
                        @Override
                        public void onSuccess(Integer integer, Integer integer2, String s) {
                            if (handler != null) {
                                handler.clear();
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }
            } else {
                final EditText inputwindow = (EditText) findViewById(R.id.inputmessage);

                inputwindow.setError("Please Input Something");
            }
            btnOpenPopup = (ImageButton) findViewById(R.id.openpopup);
            txtpopup = (EditText) findViewById(R.id.messageEditText);
            inputwindow = (EditText) findViewById(R.id.inputmessage);
            txtpopup.setHint("Type a message");
            txtpopup.setText("");
        }
    };


    /*
    * Back Button on Top left Conner
    * */
    @Override
    public boolean onSupportNavigateUp() {
        hideSoftKeyboard();
        Intent intent = new Intent(Chat_RecyclerView_Activity.this, HomeActivity.class);
        startActivity(intent);
        return true;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;


}
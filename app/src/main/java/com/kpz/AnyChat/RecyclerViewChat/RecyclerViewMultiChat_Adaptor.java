package com.kpz.AnyChat.RecyclerViewChat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kpz.AnyChat.Chat.Chat_Profile;
import com.kpz.AnyChat.Home_Activity.SelectChatActivity;
//import com.kpz.AnyChat.Network.Http_GetToken;
import com.kpz.AnyChat.Others.ChatMsgUtil;
import com.kpz.AnyChat.Others.DateTimeUtils;
import com.kpz.AnyChat.Others.DialogUtil;
import com.kpz.AnyChat.Others.FileUtils;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.ToastUtil;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.squareup.picasso.Picasso;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.chatbean.MsgAudio;
import com.vrv.imsdk.chatbean.MsgFile;
import com.vrv.imsdk.chatbean.MsgImg;
import com.vrv.imsdk.chatbean.MsgMiniVideo;
import com.vrv.imsdk.chatbean.MsgRevoke;
import com.vrv.imsdk.chatbean.MsgText;
import com.vrv.imsdk.chatbean.MsgTip;
import com.vrv.imsdk.listener.ReceiveMsgListener;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.ResultCallBack;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.kpz.AnyChat.RecyclerViewChat.Chat_RecyclerView_Activity.MY_PREFS_NAME;
import static com.vrv.imsdk.VIMClient.getContext;

/**
 * Created by Lenovo on 3/1/2018.
 */


public class RecyclerViewMultiChat_Adaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    private static final int TYPE_DEFAULT = 0;
    private static final int TYPE_TEXTING = 1;
    private static final int TYPE_IMAGES = 2;
    private static final int TYPE_MINI_VIDEO = 3;
    private static final int TYPE_AUDIO = 4;
    private static final int TYPE_FILE = 5;
    private static final int R_TYPE_TEXTING = 6;
    private static final int R_TYPE_IMAGES = 7;
    private static final int R_TYPE_MINI_VIDEO = 8;
    private static final int R_TYPE_AUDIO = 9;
    private static final int R_TYPE_FILE = 10;
    private static final int R_TYPE_TIPS = 11;
    private static final int R_TYPE_REVOKE = 12;


    private List<ChatMsg> chatMsgList;


    //Sender Audio
    SoundPool sp;
    int streamid;
    //

    //Avatar URL
    String avatar_Url;
    //

    //Text Tornado Position
    int pos;
    int chatpos;
    RecyclerView.ViewHolder temp_viewHolder;
    RecyclerView.ViewHolder temp_viewHolder_R;
    //

    //Text Reply Usage
    int viewTypes;
    //

    //For Experience ounter
    int counter;
    //


    public RecyclerViewMultiChat_Adaptor(Context context, List<ChatMsg> chatMsgList) {
        this.chatMsgList = chatMsgList;
        this.context = context;
    }

    /*
    * Message Item Count
    * */
    @Override
    public int getItemCount() {

        return chatMsgList == null ? 0 : chatMsgList.size();
    }

    /*
    * Type control
    * */
    @Override
    public int getItemViewType(int position) {
        if (ChatMsgApi.TYPE_REVOKE == chatMsgList.get(position).getMsgType()) {
            return R_TYPE_REVOKE;
        }

        if (ClientManager.getDefault().getAccountService().isMySelf(chatMsgList.get(position).getFromID())) {

            if (ChatMsgApi.TYPE_TEXT == chatMsgList.get(position).getMsgType()) {
                return TYPE_TEXTING;
            } else if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {
                return TYPE_IMAGES;
            } else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                return TYPE_MINI_VIDEO;
            } else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                return TYPE_FILE;
            } else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                return TYPE_AUDIO;
            } else if (ChatMsgApi.TYPE_WEAK_HINT == chatMsgList.get(position).getMsgType()) {

                return R_TYPE_TIPS;
            } else {
                return TYPE_DEFAULT;
            }


        } else { // Receiving side


            if (ChatMsgApi.TYPE_TEXT == chatMsgList.get(position).getMsgType()) {
                return R_TYPE_TEXTING;
            } else if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {
                return R_TYPE_IMAGES;
            } else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                return R_TYPE_MINI_VIDEO;
            } else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                return R_TYPE_FILE;
            } else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                return R_TYPE_AUDIO;
            } else if (ChatMsgApi.TYPE_WEAK_HINT == chatMsgList.get(position).getMsgType()) {

                return R_TYPE_TIPS;
            } else {
                return TYPE_DEFAULT;
            }


        }
//        return TYPE_DEFAULT;
    }

    /*
    * View control, function apply
    * */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int item_count = 0;
        Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
        if (chat != null) {
            item_count = chat.getUnreadCount();

            if (item_count > 0) {

                chatService.setMsgRead(chatMsgList.get(position).getTargetID(), chatMsgList.get(position).getMsgID());

                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "AR";
                String read = "";


//                Utils.ChangeMsgStatus(context, id, msgid, read, "");
                ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(chatMsgList.get(position).getTargetID());
                final ChatMsg chatMsgs = chatMsgBuilder.createPromptMsg(3, 5, "", chatMsgList.get(position).getMsgID() + "", RequestHelper.getAccountInfo().getID() + "", "");


                chatService.sendMsg(chatMsgs, new ResultCallBack<Void, Void, Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {
                        Log.e("Chg Status", "Success");
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

        if (ChatMsgApi.TYPE_REVOKE == chatMsgList.get(position).getMsgType()) {
            MsgRevoke revoke = (MsgRevoke) chatMsgList.get(position);

            String abc = Objects.toString(revoke.getFromID());
            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + revoke.getFromID();
//            Utils.setNickname(getContext(), url, abc, (((RecyclerViewMultiChat_Adaptor.RevokeViewHolder) holder)).temp);
            String name = (((RecyclerViewMultiChat_Adaptor.RevokeViewHolder) holder)).temp.getText().toString();
            (((RevokeViewHolder) holder)).tv_sysmsg.setText(name + " Had Recall a Message");
        }

        if (ClientManager.getDefault().getAccountService().isMySelf(chatMsgList.get(position).getFromID())) {

//TODO SENDER TEXT
            if (ChatMsgApi.TYPE_TEXT == chatMsgList.get(position).getMsgType()) {


                final MsgText msgText = (MsgText) chatMsgList.get(position);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).timestamp.setText(format.toString());
                ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).chl_tv2.setText(msgText.getBody());


                ((TextViewHolder) holder).send_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgText.getID(), position, msgText);
                        return false;

                    }
                });

                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
//                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", ((TextViewHolder) holder).dummy);
                    Log.e("Read Status Return", ((TextViewHolder) holder).dummy.getText().toString());

                    if (((TextViewHolder) holder).dummy.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
//                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.received);


                    } else if (((TextViewHolder) holder).dummy.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.read);

                    } else {
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.sent);

                    }

                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.sending);

                    }
                }
                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);


                //-----------------------------BEGINNING OF TORNADO FUNCTION && Reply Message Function--------------------------
                String pre = chatMsgList.get(position).getPreDefined();
                String[] split = pre.split(";");

                final Random rand = new Random();
                final int max = chatMsgList.size();

                Log.e("Related Received S", chatMsgList.get(position).getRelatedUsers() + "");
                Log.e("Pre Received Sending", pre);


                if (!pre.equals("") && !split[1].isEmpty()) {
                    Log.e("Pre Type Sending ", split[1]);
                    chatpos = position;

                    for (int i = 0; i < chatMsgList.get(position).getRelatedUsers().size(); i++) {

                        Long exp = chatMsgList.get(position).getRelatedUsers().get(i);
                        if (split[1].equals("4") && RequestHelper.isMyself(exp)) {
                            Toast.makeText(context, "Tordano !", Toast.LENGTH_SHORT).show();
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long time = millisUntilFinished / 1000;
                                    pos = rand.nextInt(max);
                                    ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).chl_tv2.setText(chatMsgList.get(pos).getBody());
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("Procesed Pre ", chatMsgList.get(chatpos).getPreDefined());

                                    chatMsgList.get(chatpos).setPreDefined("name;99;5");
                                    RequestHelper.updateMsg(chatMsgList.get(chatpos), new RequestCallBack() {
                                        @Override
                                        public void handleSuccess(Object o, Object o2, Object o3) {
                                            //Changed Successfully
                                            Log.e("Reset Pre ", chatMsgList.get(chatpos).getPreDefined());
                                            ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).chl_tv2.setText(chatMsgList.get(chatpos).getBody());
                                            Toast.makeText(context, "Looks like tornado had gone !", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }.start();
                        }
                    }

                    //-----------------------------END OF TORNADO FUNCTION--------------------------
                    //-----------------------------BEGINNING OF REPLY FUNCTION--------------------------


                    if (!pre.equals("") && split[1].equals("11")) {

                        final long reply_tgt_id = chatMsgList.get(position).getID();
                        final long reply_msg_id = Long.parseLong(split[0]);

                        ClientManager.getDefault().getChatService().getMessages(reply_tgt_id, reply_msg_id, 1, 0, new ResultCallBack<Long, List<ChatMsg>, Void>() {
                            @Override
                            public void onSuccess(Long aLong, final List<ChatMsg> chatMsgs, Void aVoid) {
                                ((TextViewHolder) holder).Origin_Timestamp.setVisibility(View.VISIBLE);
                                ((TextViewHolder) holder).reply_content.setVisibility(View.VISIBLE);
                                ((TextViewHolder) holder).background.setVisibility(View.VISIBLE);


                                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgs.get(0).getFromID();
//                                Utils.setNickname(context, url, chatMsgs.get(0).getFromID() + "", ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).dummy);
                                String name = ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).dummy.getText().toString();
                                Log.e("Test ID", "" + chatMsgs.get(0).getFromID());

                                ((TextViewHolder) holder).background.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Boolean match = false;
                                        for (int i = 0; i < chatMsgList.size(); i++) {

                                            if (chatMsgList.get(i).getMsgID() == reply_msg_id) {
                                /*
                                * Match Case
                                * */
                                                final int positions = chatMsgList.indexOf(chatMsgList.get(i));
                                                long othersideid = chatMsgList.get(position).getID();
                                                Intent intents = new Intent(context, Chat_RecyclerView_Activity.class);
                                                Bundle bund = new Bundle();
                                                bund.putInt("positions", positions);
                                                bund.putLong("othersideid", othersideid);
                                                bund.putLong("msgid", chatMsgList.get(position).getMsgID());
                                                bund.putLong("senduser", chatMsgList.get(position).getFromID());
                                                intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                                intents.putExtras(bund);

                                                context.startActivity(intents);
                                                Log.e("Redirect ", chatMsgList.indexOf(chatMsgList.get(i)) + "");


                                                match = true;
                                            }
                                        }


                                    }
                                });

                                switch (chatMsgs.get(0).getMsgType()) {
                                    case ChatMsgApi.TYPE_TEXT:
                                        String reply_body = chatMsgs.get(0).getBody();
                                        ((TextViewHolder) holder).reply_content.setText(name + " : " + reply_body);
                                        break;
                                    case ChatMsgApi.TYPE_IMAGE:
                                        ((TextViewHolder) holder).reply_content.setText(name + " : [IMAGE]");
                                        break;
                                    case ChatMsgApi.TYPE_MINI_VIDEO:
                                        ((TextViewHolder) holder).reply_content.setText(name + " : [VIDEO]");
                                        break;
                                    case ChatMsgApi.TYPE_AUDIO:
                                        ((TextViewHolder) holder).reply_content.setText(name + " : [AUDIO]");
                                        break;
                                    case ChatMsgApi.TYPE_FILE:
                                        ((TextViewHolder) holder).reply_content.setText(name + " : [FILE]");
                                        break;
                                }

                                ((TextViewHolder) holder).Origin_Timestamp.setText(DateTimeUtils.formatTime(context, chatMsgs.get(0).getTime(), true));

                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });


                    }
                    //-----------------------------END OF REPLY FUNCTION--------------------------


                    else {
                        simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                        format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                        ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).timestamp.setText(format.toString());
                        ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).chl_tv2.setText(msgText.getBody());

                    }


                }


//TODO SENDER IMAGE
            } else if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {

                final MsgImg msgImg = (MsgImg) chatMsgList.get(position);
                String key = msgImg.getEncryptKey();//key for decryption
                String localPath = msgImg.getMainLocalPath();
                String downPath = msgImg.getMainDownloadPath();
                String path;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).timestamp.setText(format.toString());

                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
//                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", ((TextViewHolder) holder).dummy);
                    Log.e("Read Status Return", ((TextViewHolder) holder).dummy.getText().toString());

                    if (((TextViewHolder) holder).dummy.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
//                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.received);


                    } else if (((TextViewHolder) holder).dummy.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.read);

                    } else {
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.sent);

                    }

                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.sending);

                    }
                }
                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            ((TextViewHolder) holder).text_msg_state.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);

                if (!TextUtils.isEmpty(localPath)) {
                    path = localPath;
                } else {
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                }
                final File files = new File(path);
                Picasso.with(context)
                        .load(("file:///" + path))
                        .resize(500, 500)
                        .centerCrop()
                        .into(((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).img);

                ((RecyclerViewMultiChat_Adaptor.TextViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = files.getName().substring(files.getName().indexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);
                            intent.setDataAndType(Uri.fromFile(files), type);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Image Load Fail", e + "");
                            Toast.makeText(context, "Image Loading Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ((TextViewHolder) holder).img.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgImg.getID(), position, msgImg);
                        return false;

                    }
                });

//TODO SENDER VIDEO
            } else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                final MsgMiniVideo msgVideo = (MsgMiniVideo) chatMsgList.get(position);
                String key = msgVideo.getEncryptKey();//key for decryption
                String localPath = msgVideo.getLocalVideoPath();
                String downPath = msgVideo.getVideoDownloadPath();
                String pre = msgVideo.getPreImgUrl();
                String videopath;


                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
//                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", ((VideoViewHolder) holder).dummy1);
                    Log.e("Read Status Return", (((VideoViewHolder) holder).dummy1.getText().toString()));

                    if (((VideoViewHolder) holder).dummy1.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
//                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        ((VideoViewHolder) holder).video_msg_state.setImageResource(R.drawable.received);


                    } else if (((VideoViewHolder) holder).dummy1.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        ((VideoViewHolder) holder).video_msg_state.setImageResource(R.drawable.read);

                    } else {
                        ((VideoViewHolder) holder).video_msg_state.setImageResource(R.drawable.sent);

                    }

                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        ((VideoViewHolder) holder).video_msg_state.setImageResource(R.drawable.sending);

                    }
                }
                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            ((VideoViewHolder) holder).video_msg_state.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);


                ((VideoViewHolder) holder).videos.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgVideo.getID(), position, msgVideo);
                        return false;
                    }
                });

                RequestHelper.downloadImage(msgVideo.getID(), pre, new RequestCallBack<String, Long, Void>() {
                    @Override
                    public void handleSuccess(String s, Long aLong, Void aVoid) {
                        Log.e("Video Pre Download ", "Success");
                    }
                }, new RequestCallBack<Integer, Integer, String>() {
                    @Override
                    public void handleSuccess(Integer integer, Integer integer2, String s) {
                        Log.e("Video Pre Download ", "Success");
                    }
                });

                File f = new File(localPath);
                if (!TextUtils.isEmpty(localPath) && f.exists()) {
                    videopath = localPath;
                    Log.e("Video Path Local", videopath);
                } else {
                    videopath = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                    Log.e("Video Path", videopath);
                }
                final File files = new File(videopath);

                String paths = msgVideo.getPreImgDownloadPath();
                String thumb = ClientManager.getDefault().getFileService().decryptFile(key, paths);
                Log.e("Video Thumb Image Path", thumb);
                if (((RecyclerViewMultiChat_Adaptor.VideoViewHolder) holder).videos != null) {
                    Picasso.with(context)
                            .load("file:///" + thumb)
                            .resize(500, 500)
                            .centerCrop()
                            .into(((RecyclerViewMultiChat_Adaptor.VideoViewHolder) holder).videos);

                    ((RecyclerViewMultiChat_Adaptor.VideoViewHolder) holder).videos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                notifyDataSetChanged();
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String ext = files.getName().substring(files.getName().indexOf(".") + 1);
                                String type = mime.getMimeTypeFromExtension(ext);
                                intent.setDataAndType(Uri.fromFile(files), type);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Log.e("Video Load Fail", e + "");
                                Toast.makeText(context, "Video Loading Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                (((RecyclerViewMultiChat_Adaptor.VideoViewHolder) holder).sender_timestamp).setText
                        (DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));


            }
//TODO SENDER FILE
            else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                final MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                final String localPath = msgFile.getLocalPath();
                final String downPath = msgFile.getDownloadPath();

                final String key = msgFile.getEncryptKey();//key for decryptio
                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
//                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", ((FileViewHolder) holder).dummy3);
                    Log.e("Read Status Return", ((FileViewHolder) holder).dummy3.getText().toString());

                    if (((FileViewHolder) holder).dummy3.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
//                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        ((FileViewHolder) holder).file_msg_state.setImageResource(R.drawable.received);


                    } else if (((FileViewHolder) holder).dummy3.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        ((FileViewHolder) holder).file_msg_state.setImageResource(R.drawable.read);

                    } else {
                        ((FileViewHolder) holder).file_msg_state.setImageResource(R.drawable.sent);

                    }

                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        ((FileViewHolder) holder).file_msg_state.setImageResource(R.drawable.sending);

                    }
                }
                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            ((FileViewHolder) holder).file_msg_state.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);


                (((RecyclerViewMultiChat_Adaptor.FileViewHolder) holder).sender_attachment_name).setText(msgFile.getFileName());

                (((RecyclerViewMultiChat_Adaptor.FileViewHolder) holder).sender_attachment_timestamp).setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                String size = String.valueOf(msgFile.getSize());

                int sizes = Integer.valueOf(size);
                int actual_size = (sizes / 1024);

                (((RecyclerViewMultiChat_Adaptor.FileViewHolder) holder).sender_attachment_size).setText(actual_size + " KB");

                (((RecyclerViewMultiChat_Adaptor.FileViewHolder) holder).sender_attachment_download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String actual;
                        File f = new File(localPath);
                        if (f.exists()) {
                            actual = localPath;
                            Log.e("File Path L ", actual);
                        } else {
                            actual = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                            Log.e("File Path ", actual);
                        }


                        File fileIn = new File(actual);
                        Uri u = Uri.fromFile(fileIn);
                        fileIn = new File(actual);
                        u = Uri.fromFile(fileIn);
                        try {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);
                            intent.setDataAndType(Uri.fromFile(fileIn), type);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Load File Fail", e + "");
                            Toast.makeText(context, "Load Attachment Fail", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                ((FileViewHolder) holder).send_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgFile.getID(), position, msgFile);
                        return false;

                    }
                });
//TODO SENDER AUDIO
            } else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                final MsgAudio msgAudio = (MsgAudio) chatMsgList.get(position);
                String audiopath = msgAudio.getLocalPath(); //localpath
                String dlaudiopath = msgAudio.getDownloadPath();
                final String key = msgAudio.getEncryptKey();//key for decryption
                final String localPath = msgAudio.getLocalPath();
                final String downPath = msgAudio.getDownloadPath();
                final String path;

                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
//                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", ((AudioViewHolder) holder).dummy2);
                    Log.e("Read Status Return", ((AudioViewHolder) holder).dummy2.getText().toString());

                    if (((AudioViewHolder) holder).dummy2.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
//                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        ((AudioViewHolder) holder).img_msg_state.setImageResource(R.drawable.received);


                    } else if (((AudioViewHolder) holder).dummy2.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        ((AudioViewHolder) holder).img_msg_state.setImageResource(R.drawable.read);

                    } else {
                        ((AudioViewHolder) holder).img_msg_state.setImageResource(R.drawable.sent);

                    }

                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        ((AudioViewHolder) holder).img_msg_state.setImageResource(R.drawable.sending);

                    }
                }


                (((RecyclerViewMultiChat_Adaptor.AudioViewHolder) holder).imgview).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgAudio.getID(), position, msgAudio);
                        return false;

                    }
                });

                (((RecyclerViewMultiChat_Adaptor.AudioViewHolder) holder).timestampaudio).setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));

                if (!FileUtils.isExist(localPath)) {

                    chatService.downloadFile(msgAudio, (byte) 2, new RequestCallBack<String, Long, Void>() {
                        @Override
                        public void handleSuccess(String s, Long aLong, Void aVoid) {

                        }
                    }, new RequestCallBack<Integer, Integer, String>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, String s) {

                        }
                    });
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                } else {

                    path = ClientManager.getDefault().getFileService().decryptFile(key, localPath);

                }


                sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                String pre = chatMsgList.get(position).getPreDefined();
                Log.e("PreCheck", pre);

                if (!pre.isEmpty() && pre != null && !pre.equals("0")) {
                    String[] split = pre.split(";");
                    if (!split[1].isEmpty() && split[1].equals("3")) { //Type = Pitch Changing Msg
                        String pitchs = split[2];
                        final float pitch_s = Float.parseFloat(pitchs);

                        (((RecyclerViewMultiChat_Adaptor.AudioViewHolder) holder).imgview).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                notifyDataSetChanged();

                                final int i = sp.load(path, 0);
                                sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                                    @Override
                                    public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                                        streamid = sp.play(i, 1, 1, 0, 0, pitch_s);
                                    }
                                });

                            }

                        });
                    }
                } else {//normal pitch
                    (((RecyclerViewMultiChat_Adaptor.AudioViewHolder) holder).imgview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notifyDataSetChanged();

                            final int i = sp.load(path, 0);
                            streamid = sp.play(i, 1, 1, 0, 0, 1);

                        }
                    });
                }


            } else if (ChatMsgApi.TYPE_WEAK_HINT == chatMsgList.get(position).getMsgType()) {
                final MsgTip msgTip = (MsgTip) chatMsgList.get(position);
                String userinfo;
                final String oprUser = msgTip.getOprUser();
                final String userInfo = msgTip.getUserInfo();
                String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
                String group_ID = String.valueOf(chatMsgList.get(position).getID());
                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.

                String abcs = Objects.toString(msgTip.getFromID());
                String urls = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgTip.getOprUser();
//                Utils.setNickname(getContext(), urls, abcs, ((TipsViewHolder) holder).temp);

                String abcd = Objects.toString(msgTip.getToID());
                String urlss = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgTip.getUserInfo();
//                Utils.setNickname(getContext(), urlss, abcd, ((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder).temps);

                String oprname = (((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder)).temps.getText().toString();

                if (!userInfo.equals("System")) {
                    userinfo = (((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder)).temp.getText().toString();
                } else {
                    userinfo = "System";
                }

                switch (msgTip.getTipType()) {
                    case MsgTip.TYPE_GROUP:
                        switch (msgTip.getOprType()) {
                            case 0:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(userinfo + " Had Join The Group");
                                //String url_join_group = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//                                Http_GetToken gt = new Http_GetToken(context, 8, url_join_group, ids, (((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder)).temp, deviceId, group_ID, "", "");
//                                gt.execute();
                                break;

                            case 1:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText("Group Created !");
                                break;

                            case 2:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(oprname + " Accepted " + userinfo + "'s Join Request ");
                                break;

                            case 3:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(userinfo + " Had Left The Group");
                                break;

                            case 4:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(userinfo + " Removed by " + oprname);
                                break;
                            case 5:
//                                (((TipsViewHolder) holder)).background.setVisibility(View.INVISIBLE);
//                                (((TipsViewHolder) holder)).tv_sysmsg.setVisibility(View.INVISIBLE);
                                break;
                        }
                }

            }

        }

        // END OF SENDER LAYOUT PART
        //======================================RECEIVER PART========================================

        else {
//TODO RECEIVER TEXT
            if (ChatMsgApi.TYPE_TEXT == chatMsgList.get(position).getMsgType()) {
                final MsgText msgText = (MsgText) chatMsgList.get(position);
//
//                ((RecyclerViewMultiChat.Receiver_TextViewHolder) holder).receiver_timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
//                ((RecyclerViewMultiChat.Receiver_TextViewHolder) holder).receiver_message_text.setText(msgText.getBody());

                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                    }
                });
                Utils.loadHead(context, avatar_Url, ((Receiver_TextViewHolder) holder).avatar_onclick, R.mipmap.vim_icon_default_user);
                ((Receiver_TextViewHolder) holder).avatar_onclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//                Utils.setNickname(context, url, chatMsgList.get(position).getFromID() + "", ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_nickname);

                //-----------------------------BEGINNING OF TORNADO FUNCTION && Reply Message Function--------------------------
                String pre = chatMsgList.get(position).getPreDefined();
                String[] split = pre.split(";");

                final Random rand = new Random();
                final int max = chatMsgList.size();

                Log.e("Related Received ", chatMsgList.get(position).getRelatedUsers() + "");
                Log.e("Pre Received ", pre);

                ((Receiver_TextViewHolder) holder).receiver_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgText.getID(), position, msgText);
                        return false;

                    }
                });

                if (!pre.equals("") && !split[1].isEmpty()) {
                    Log.e("Pre Type ", split[1]);
                    chatpos = position;
                    for (int i = 0; i < chatMsgList.get(position).getRelatedUsers().size(); i++) {

                        Long exp = chatMsgList.get(position).getRelatedUsers().get(i);


                        if (split[1].equals("4") && RequestHelper.isMyself(exp)) {
                            Toast.makeText(context, "Tordano !", Toast.LENGTH_SHORT).show();
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long time = millisUntilFinished / 1000;
                                    pos = rand.nextInt(max);
                                    ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) temp_viewHolder).receiver_message_text.setText(chatMsgList.get(pos).getBody());
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("Procesed Pre ", chatMsgList.get(chatpos).getPreDefined());

                                    chatMsgList.get(chatpos).setPreDefined("name;99;5");
                                    RequestHelper.updateMsg(chatMsgList.get(chatpos), new RequestCallBack() {
                                        @Override
                                        public void handleSuccess(Object o, Object o2, Object o3) {
                                            //Changed Successfully
                                            Log.e("Reset Pre ", chatMsgList.get(chatpos).getPreDefined());
                                            ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) temp_viewHolder).receiver_message_text.setText(chatMsgList.get(chatpos).getBody());
                                            Toast.makeText(context, "Looks like tornado had gone !", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }.start();
                        }
                        //-----------------------------END OF TORNADO FUNCTION--------------------------
                        //-----------------------------BEGINNING OF REPLY FUNCTION

                    }
                    if (!pre.equals("") && split[1].equals("11")) {

                        final long reply_tgt_id = chatMsgList.get(position).getID();
                        final long reply_msg_id = Long.parseLong(split[0]);
                        Log.e("Fetch Msg ID ", reply_tgt_id + "");
                        Log.e("Msg ID ", reply_msg_id + "");

                        ClientManager.getDefault().getChatService().getMessages(reply_tgt_id, reply_msg_id, 1, 0, new ResultCallBack<Long, List<ChatMsg>, Void>() {
                            @Override
                            public void onSuccess(Long aLong, final List<ChatMsg> chatMsgs, Void aVoid) {

                                ((Receiver_TextViewHolder) holder).background.setVisibility(View.VISIBLE);
                                ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).Origin_Timestamp.setVisibility(View.VISIBLE);
                                ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).reply_content.setVisibility(View.VISIBLE);
                                ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).Origin_Timestamp.setText(DateTimeUtils.formatTime(context, chatMsgs.get(0).getTime(), true));


                                Log.e("Msg", chatMsgs + "");
                                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgs.get(0).getFromID();
//                                Utils.setNickname(context, url, chatMsgs.get(0).getFromID() + "", ((Receiver_TextViewHolder) holder).temps);
                                String name = ((Receiver_TextViewHolder) holder).temps.getText().toString();

                                ((Receiver_TextViewHolder) holder).background.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Boolean match = false;
                                        for (int i = 0; i < chatMsgList.size(); i++) {

                                            if (chatMsgList.get(i).getMsgID() == reply_msg_id) {
                                /*
                                * Match Case
                                * */
                                                final int positions = chatMsgList.indexOf(chatMsgList.get(i));
                                                long othersideid = chatMsgList.get(position).getID();
                                                Intent intents = new Intent(context, Chat_RecyclerView_Activity.class);
                                                Bundle bund = new Bundle();
                                                bund.putInt("positions", positions);
                                                bund.putLong("othersideid", othersideid);
                                                bund.putLong("msgid", chatMsgList.get(position).getMsgID());
                                                bund.putLong("senduser", chatMsgList.get(position).getFromID());
                                                intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                                                intents.putExtras(bund);

                                                context.startActivity(intents);
                                                Log.e("Redirect ", chatMsgList.indexOf(chatMsgList.get(i)) + "");


                                                match = true;
                                            }
                                        }


                                    }
                                });


                                if (chatMsgs.size() > 0) {
                                    switch (chatMsgs.get(0).getMsgType()) {
                                        case ChatMsgApi.TYPE_TEXT:
                                            String reply_body = chatMsgs.get(0).getBody();
                                            ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).reply_content.setText(name + " : " + reply_body);
                                            break;
                                        case ChatMsgApi.TYPE_IMAGE:
                                            ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).reply_content.setText(name + " : [IMAGE]");
                                            break;
                                        case ChatMsgApi.TYPE_MINI_VIDEO:
                                            ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).reply_content.setText(name + " : [VIDEO]");
                                            break;
                                        case ChatMsgApi.TYPE_AUDIO:
                                            ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).reply_content.setText(name + " : [AUDIO]");
                                            break;
                                        case ChatMsgApi.TYPE_FILE:
                                            ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).reply_content.setText(name + " : [FILE]");
                                            break;
                                    }
                                } else {
                                    Toast.makeText(context, "Nothing TO fetch", Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                    } else {

                        ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                        ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_message_text.setText(msgText.getBody());

                    }

                }
                ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_message_text.setText(msgText.getBody());

//TODO RECEIVER IMAGE
            } else if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {

                final MsgImg msgImg = (MsgImg) chatMsgList.get(position);
                String key = msgImg.getEncryptKey();//key for decryption
                String localPath = msgImg.getMainLocalPath();
                String downPath = msgImg.getMainDownloadPath();
                String path;

                ((Receiver_TextViewHolder) holder).image_received.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgImg.getID(), position, msgImg);
                        return false;

                    }
                });

                ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                    }
                });
                Utils.loadHead(context, avatar_Url, ((Receiver_TextViewHolder) holder).avatar_onclick, R.mipmap.vim_icon_default_user);
                ((Receiver_TextViewHolder) holder).avatar_onclick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                ((Receiver_TextViewHolder) holder).receiver_message_text.setVisibility(View.GONE);
                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//                Utils.setNickname(context, url, chatMsgList.get(position).getFromID() + "", ((RecyclerViewMultiChat_Adaptor.Receiver_TextViewHolder) holder).receiver_nickname);


                if (!TextUtils.isEmpty(localPath)) {
                    path = localPath;
                } else {
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                }
                final File files = new File(path);

                Picasso.with(context)
                        .load(("file:///" + path))
                        .resize(500, 500)
                        .centerCrop()

                        .into(((Receiver_TextViewHolder) holder).image_received);
                ((Receiver_TextViewHolder) holder).image_received.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            notifyDataSetChanged();
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = files.getName().substring(files.getName().indexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);
                            intent.setDataAndType(Uri.fromFile(files), type);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Image Load Fail", e + "");
                            Toast.makeText(context, "Image Loading Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }

//TODO RECEIVER VIDEO
            else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                final MsgMiniVideo msgVideo = (MsgMiniVideo) chatMsgList.get(position);
                String key = msgVideo.getEncryptKey();//key for decryption
                String localPath = msgVideo.getLocalVideoPath();
                String downPath = msgVideo.getVideoDownloadPath();
                String pre = msgVideo.getPreImgUrl();
                String videopath;
                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//                Utils.setNickname(context, url, chatMsgList.get(position).getFromID() + "", ((Receiver_VideoViewHolder) holder).receiver_nickname);

                ((Receiver_VideoViewHolder) holder).videos.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgVideo.getID(), position, msgVideo);
                        return false;

                    }
                });
                ((RecyclerViewMultiChat_Adaptor.Receiver_VideoViewHolder) holder).sender_timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                    }
                });
                Utils.loadHead(context, avatar_Url, ((Receiver_VideoViewHolder) holder).avatar_onclick_video, R.mipmap.vim_icon_default_user);
                ((Receiver_VideoViewHolder) holder).avatar_onclick_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

                RequestHelper.downloadImage(msgVideo.getID(), pre, new RequestCallBack<String, Long, Void>() {
                    @Override
                    public void handleSuccess(String s, Long aLong, Void aVoid) {
                        Log.e("Video Pre Download ", "Success");
                    }
                }, new RequestCallBack<Integer, Integer, String>() {
                    @Override
                    public void handleSuccess(Integer integer, Integer integer2, String s) {
                        Log.e("Video Pre Download ", "Success");
                    }
                });

                File f = new File(localPath);
                if (!TextUtils.isEmpty(localPath) && f.exists()) {
                    videopath = localPath;
                    Log.e("Video Path Local", videopath);
                } else {
                    videopath = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                    Log.e("Video Path", videopath);
                }
                final File files = new File(videopath);

                String paths = msgVideo.getPreImgDownloadPath();
                String thumb = ClientManager.getDefault().getFileService().decryptFile(key, paths);
                Log.e("Video Thumb Image Path", thumb);
                if (((RecyclerViewMultiChat_Adaptor.Receiver_VideoViewHolder) holder).videos != null) {
                    Picasso.with(context)
                            .load("file:///" + thumb)
                            .resize(500, 500)
                            .centerCrop()
                            .into(((RecyclerViewMultiChat_Adaptor.Receiver_VideoViewHolder) holder).videos);

                    ((RecyclerViewMultiChat_Adaptor.Receiver_VideoViewHolder) holder).videos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                notifyDataSetChanged();
                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String ext = files.getName().substring(files.getName().indexOf(".") + 1);
                                String type = mime.getMimeTypeFromExtension(ext);
                                intent.setDataAndType(Uri.fromFile(files), type);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Log.e("Video Load Fail", e + "");
                                Toast.makeText(context, "Video Loading Fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                (((RecyclerViewMultiChat_Adaptor.Receiver_VideoViewHolder) holder).sender_timestamp).setText
                        (DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));


            }
//TODO RECEIVER FILE

            else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                final MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                final String localPath = msgFile.getLocalPath();
                final String downPath = msgFile.getDownloadPath();
                final String key = msgFile.getEncryptKey();//key for decryptio
                (((RecyclerViewMultiChat_Adaptor.Receiver_FileViewHolder) holder).sender_attachment_name).setText(msgFile.getFileName());
                ((Receiver_FileViewHolder) holder).receiver_box.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgFile.getID(), position, msgFile);
                        return false;

                    }
                });
                (((RecyclerViewMultiChat_Adaptor.Receiver_FileViewHolder) holder).sender_attachment_timestamp).setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                String size = String.valueOf(msgFile.getSize());

                int sizes = Integer.valueOf(size);
                int actual_size = (sizes / 1024);

                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                    }
                });
                Utils.loadHead(context, avatar_Url, ((Receiver_FileViewHolder) holder).avatar_onclick_file, R.mipmap.vim_icon_default_user);
                ((Receiver_FileViewHolder) holder).avatar_onclick_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//                Utils.setNickname(context, url, chatMsgList.get(position).getFromID() + "", ((Receiver_FileViewHolder) holder).receiver_nickname);

                (((RecyclerViewMultiChat_Adaptor.Receiver_FileViewHolder) holder).sender_attachment_size).setText(actual_size + " KB");
                (((RecyclerViewMultiChat_Adaptor.Receiver_FileViewHolder) holder).sender_attachment_download).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String actual;
                        File f = new File(localPath);
                        if (f.exists()) {
                            actual = localPath;
                            Log.e("File Path L ", actual);
                        } else {
                            actual = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                            Log.e("File Path ", actual);
                        }


                        File fileIn = new File(actual);
                        Uri u = Uri.fromFile(fileIn);
                        fileIn = new File(actual);
                        u = Uri.fromFile(fileIn);
                        try {
                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);
                            intent.setDataAndType(Uri.fromFile(fileIn), type);
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Log.e("Load File Fail", e + "");
                            Toast.makeText(context, "Load Attachment Fail", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
//TODO RECEIVER AUDIO
            else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                final MsgAudio msgAudio = (MsgAudio) chatMsgList.get(position);
                String audiopath = msgAudio.getLocalPath(); //localpath
                String dlaudiopath = msgAudio.getDownloadPath();
                final String key = msgAudio.getEncryptKey();//key for decryption
                final String localPath = msgAudio.getLocalPath();
                final String downPath = msgAudio.getDownloadPath();
                final String path;
                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//                Utils.setNickname(context, url, chatMsgList.get(position).getFromID() + "", ((Receiver_AudioViewHolder) holder).receiver_nickname);
                (((RecyclerViewMultiChat_Adaptor.Receiver_AudioViewHolder) holder).timestampaudio).setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                    }
                });


                ((Receiver_AudioViewHolder) holder).imgview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        OnLongClick(msgAudio.getID(), position, msgAudio);
                        return false;

                    }
                });

                Utils.loadHead(context, avatar_Url, ((Receiver_AudioViewHolder) holder).avatar_onclick_audio, R.mipmap.vim_icon_default_user);
                ((Receiver_AudioViewHolder) holder).avatar_onclick_audio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                if (!FileUtils.isExist(localPath)) {

                    chatService.downloadFile(msgAudio, (byte) 2, new RequestCallBack<String, Long, Void>() {
                        @Override
                        public void handleSuccess(String s, Long aLong, Void aVoid) {

                        }
                    }, new RequestCallBack<Integer, Integer, String>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, String s) {

                        }
                    });
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                } else {

                    path = ClientManager.getDefault().getFileService().decryptFile(key, localPath);

                }


                sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                String pre = chatMsgList.get(position).getPreDefined();
                Log.e("PreCheck", pre);

                if (!pre.isEmpty() && pre != null && !pre.equals("0")) {
                    String[] split = pre.split(";");
                    if (!split[1].isEmpty() && split[1].equals("3")) { //Type = Pitch Changing Msg
                        String pitchs = split[2];
                        final float pitch_s = Float.parseFloat(pitchs);

                        (((RecyclerViewMultiChat_Adaptor.Receiver_AudioViewHolder) holder).imgview).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                notifyDataSetChanged();

                                final int i = sp.load(path, 0);
                                sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                                    @Override
                                    public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                                        streamid = sp.play(i, 1, 1, 0, 0, pitch_s);
                                    }
                                });

                            }

                        });
                    }
                } else {//normal pitch
                    (((RecyclerViewMultiChat_Adaptor.Receiver_AudioViewHolder) holder).imgview).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notifyDataSetChanged();

                            final int i = sp.load(path, 0);
                            streamid = sp.play(i, 1, 1, 0, 0, 1);

                        }
                    });
                }


            } else if (ChatMsgApi.TYPE_WEAK_HINT == chatMsgList.get(position).getMsgType()) {
                final MsgTip msgTip = (MsgTip) chatMsgList.get(position);
                String userinfo;
                final String oprUser = msgTip.getOprUser();
                final String userInfo = msgTip.getUserInfo();
                String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
                TelephonyManager telephonyManager;
                telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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
                String group_ID = String.valueOf(chatMsgList.get(position).getID());
                SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.

                String abcs = Objects.toString(msgTip.getFromID());
                String urls = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgTip.getOprUser();
//                Utils.setNickname(getContext(), urls, abcs, ((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder).temp);

                String abcd = Objects.toString(msgTip.getToID());
                String urlss = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgTip.getUserInfo();
//                Utils.setNickname(getContext(), urlss, abcd, ((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder).temps);

                String oprname = (((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder)).temp.getText().toString();
                if (!userInfo.equals("System")) {
                    userinfo = (((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder)).temp.getText().toString();
                } else {
                    userinfo = "System";
                }

                switch (msgTip.getTipType()) {
                    case MsgTip.TYPE_GROUP:
                        switch (msgTip.getOprType()) {
                            case 0:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(userinfo + " Had Join The Group");
                                String url_join_group = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
//                                Http_GetToken gt = new Http_GetToken(context, 8, url_join_group, ids, (((RecyclerViewMultiChat_Adaptor.TipsViewHolder) holder)).temp, deviceId, group_ID, "", "");
//                                gt.execute();
                                break;

                            case 1:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText("Group Created !");
                                break;

                            case 2:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(oprname + " Accepted " + userinfo + "'s Join Request ");
                                break;

                            case 3:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(userinfo + " Had Left The Group");
                                break;

                            case 4:
                                (((TipsViewHolder) holder)).tv_sysmsg.setText(userinfo + " Removed by " + oprname);
                                break;
                            case 5:
                                (((TipsViewHolder) holder)).background.setVisibility(View.GONE);
                                (((TipsViewHolder) holder)).tv_sysmsg.setVisibility(View.GONE);
                                break;
                        }
                }

            }


        }

    }

    /*
    * Inflators
    */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("ViewType", viewType + "");
        View itemView;

        if (viewType == TYPE_TEXTING) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_chat_bubble, parent, false);
            viewTypes = TYPE_TEXTING;
            return new TextViewHolder(itemView);

        } else if (viewType == TYPE_IMAGES) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_chat_bubble, parent, false);
            viewTypes = TYPE_IMAGES;
            return new TextViewHolder(itemView);

        } else if (viewType == TYPE_MINI_VIDEO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_video_bubble, parent, false);
            viewTypes = TYPE_MINI_VIDEO;
            return new VideoViewHolder(itemView);

        } else if (viewType == TYPE_FILE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_attachment_bubble, parent, false);
            viewTypes = TYPE_FILE;
            return new FileViewHolder(itemView);

        } else if (viewType == TYPE_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_audio_bubble, parent, false);
            viewTypes = TYPE_AUDIO;
            return new AudioViewHolder(itemView);
        }

        //Receiving Side Inflators

        else if (viewType == R_TYPE_TEXTING) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_chat_bubble, parent, false);
            return new Receiver_TextViewHolder(itemView);

        } else if (viewType == R_TYPE_IMAGES) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_chat_bubble, parent, false);
            return new Receiver_TextViewHolder(itemView);

        } else if (viewType == R_TYPE_MINI_VIDEO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_video_bubble, parent, false);
            return new Receiver_VideoViewHolder(itemView);

        } else if (viewType == R_TYPE_FILE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_attachment_bubble, parent, false);
            return new Receiver_FileViewHolder(itemView);

        } else if (viewType == R_TYPE_AUDIO) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.receiver_audio_bubble, parent, false);
            return new Receiver_AudioViewHolder(itemView);
        } else if (viewType == R_TYPE_TIPS) {

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vim_sys_msg, parent, false);
            return new TipsViewHolder(itemView);

        } else if (viewType == R_TYPE_REVOKE) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.vim_sys_msg, parent, false);
            return new RevokeViewHolder(itemView);
        }
        // Not Recognize-able //Comment to return not to show not recognizeable
        else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.sender_chat_bubble, parent, false);
            return new TextViewHolder(itemView);
        }

    }


    /*
    * ViewHolders
    */
    public static class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView chl_tv2, timestamp, reply_content, Origin_Timestamp, background, dummy;
        public ImageView img, text_msg_state;

        public RelativeLayout send_box;

        public TextViewHolder(View itemView) {
            super(itemView);
            chl_tv2 = (TextView) itemView.findViewById(R.id.sender_message_text);
            timestamp = (TextView) itemView.findViewById(R.id.sender_timestamp);
            reply_content = (TextView) itemView.findViewById(R.id.reply_content);
            Origin_Timestamp = (TextView) itemView.findViewById(R.id.Origin_Timestamp);
            background = (TextView) itemView.findViewById(R.id.background);
            dummy = (TextView) itemView.findViewById(R.id.textView258);
            img = (ImageView) itemView.findViewById(R.id.image_send);
            text_msg_state = (ImageView) itemView.findViewById(R.id.text_msg_state);
            send_box = (RelativeLayout) itemView.findViewById(R.id.send_box);

        }
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView sender_timestamp, dummy1;
        ImageView videos, video_msg_state;
        RelativeLayout send_box;
        public VideoViewHolder(View itemView) {
            super(itemView);
            sender_timestamp = (TextView) itemView.findViewById(R.id.sender_timestamp);
            send_box = (RelativeLayout) itemView.findViewById(R.id.send_box);
            dummy1 = (TextView) itemView.findViewById(R.id.dummy1);
            videos = (ImageView) itemView.findViewById(R.id.video_player_sender);
            video_msg_state = (ImageView) itemView.findViewById(R.id.video_msg_state);
        }

    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView sender_attachment_name, sender_attachment_size, sender_attachment_timestamp, dummy3;
        ImageView sender_attachment_download, file_msg_state;
        RelativeLayout send_box;

        public FileViewHolder(View itemView) {
            super(itemView);
            sender_attachment_name = (TextView) itemView.findViewById(R.id.sender_attachment_name);
            sender_attachment_size = (TextView) itemView.findViewById(R.id.sender_attachment_size);
            dummy3 = (TextView) itemView.findViewById(R.id.dummy3);
            sender_attachment_timestamp = (TextView) itemView.findViewById(R.id.sender_timestamp);
            send_box = (RelativeLayout) itemView.findViewById(R.id.send_box);
            sender_attachment_download = (ImageView) itemView.findViewById(R.id.sender_attachment_download);
            file_msg_state = (ImageView) itemView.findViewById(R.id.file_msg_state);
        }

    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgview, img_msg_state;
        TextView timestampaudio,  dummy2;
        RelativeLayout send_box;
        public AudioViewHolder(View itemView) {
            super(itemView);
            timestampaudio = (TextView) itemView.findViewById(R.id.sender_timestamp);
            dummy2 = (TextView) itemView.findViewById(R.id.dummy2);
            send_box = (RelativeLayout) itemView.findViewById(R.id.send_box);
            imgview = (ImageView) itemView.findViewById(R.id.sender_play);
            img_msg_state = (ImageView) itemView.findViewById(R.id.img_msg_state);
        }
    }

    public static class Receiver_TextViewHolder extends RecyclerView.ViewHolder {
        public TextView receiver_message_text, receiver_timestamp, receiver_nickname, reply_content, Origin_Timestamp, background, temps;
        public ImageView avatar_onclick, image_received;
        public RelativeLayout receiver_box;

        public Receiver_TextViewHolder(View itemView) {
            super(itemView);
            receiver_message_text = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiver_timestamp = (TextView) itemView.findViewById(R.id.receiver_timestamp);
            receiver_nickname = (TextView) itemView.findViewById(R.id.receiver_nickname);
            reply_content = (TextView) itemView.findViewById(R.id.reply_content);
            Origin_Timestamp = (TextView) itemView.findViewById(R.id.Origin_Timestamp);
            background = (TextView) itemView.findViewById(R.id.background);
            temps = (TextView) itemView.findViewById(R.id.temps);
            receiver_box = (RelativeLayout) itemView.findViewById(R.id.receiver_box);
            avatar_onclick = (ImageView) itemView.findViewById(R.id.avatar_onclick);
            image_received = (ImageView) itemView.findViewById(R.id.image_received);
        }
    }

    public static class Receiver_VideoViewHolder extends RecyclerView.ViewHolder {
        TextView sender_timestamp, receiver_nickname;
        ImageView videos, avatar_onclick_video;
        public RelativeLayout receiver_box;

        public Receiver_VideoViewHolder(View itemView) {
            super(itemView);
            sender_timestamp = (TextView) itemView.findViewById(R.id.receiver_timestamp);
            receiver_nickname = (TextView) itemView.findViewById(R.id.receiver_nickname);
            receiver_box = (RelativeLayout) itemView.findViewById(R.id.receiver_box);
            videos = (ImageView) itemView.findViewById(R.id.video_player_receiver);
            avatar_onclick_video = (ImageView) itemView.findViewById(R.id.avatar_onclick_video);
        }

    }

    public static class Receiver_FileViewHolder extends RecyclerView.ViewHolder {
        TextView sender_attachment_name;
        TextView sender_attachment_size;
        TextView sender_attachment_timestamp;
        TextView receiver_nickname;
        ImageView sender_attachment_download, avatar_onclick_file;
        public RelativeLayout receiver_box;

        public Receiver_FileViewHolder(View itemView) {
            super(itemView);
            sender_attachment_name = (TextView) itemView.findViewById(R.id.receiver_attachment_name);
            sender_attachment_size = (TextView) itemView.findViewById(R.id.receiver_attachment_size);
            sender_attachment_timestamp = (TextView) itemView.findViewById(R.id.receiver_timestamp);
            receiver_nickname = (TextView) itemView.findViewById(R.id.receiver_nickname);
            receiver_box = (RelativeLayout) itemView.findViewById(R.id.receiver_box);
            sender_attachment_download = (ImageView) itemView.findViewById(R.id.receiver_attachment_download);
            avatar_onclick_file = (ImageView) itemView.findViewById(R.id.avatar_onclick_file);
        }

    }

    public static class Receiver_AudioViewHolder extends RecyclerView.ViewHolder {
        ImageView imgview, avatar_onclick_audio;
        TextView timestampaudio, receiver_nickname;
        public RelativeLayout receiver_box;

        public Receiver_AudioViewHolder(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.receiver_play);
            avatar_onclick_audio = (ImageView) itemView.findViewById(R.id.avatar_onclick_audio);
            timestampaudio = (TextView) itemView.findViewById(R.id.receiver_timestamp);
            receiver_nickname = (TextView) itemView.findViewById(R.id.receiver_nickname);
            receiver_box = (RelativeLayout) itemView.findViewById(R.id.receiver_box);
        }
    }

    public static class TipsViewHolder extends RecyclerView.ViewHolder {
        TextView temp, temps, tv_sysmsg;
        LinearLayout background;

        public TipsViewHolder(View itemView) {
            super(itemView);
            background = (LinearLayout) itemView.findViewById(R.id.background);
            temp = (TextView) itemView.findViewById(R.id.temp);
            temps = (TextView) itemView.findViewById(R.id.temp);
            tv_sysmsg = (TextView) itemView.findViewById(R.id.tv_sysmsg);
        }
    }

    public static class RevokeViewHolder extends RecyclerView.ViewHolder {
        TextView temp, temps, tv_sysmsg;


        public RevokeViewHolder(View itemView) {
            super(itemView);
            temp = (TextView) itemView.findViewById(R.id.temp);
            temps = (TextView) itemView.findViewById(R.id.temp);
            tv_sysmsg = (TextView) itemView.findViewById(R.id.tv_sysmsg);
        }
    }


    public void OnLongClick(long othersideid, final int position, ChatMsg chatMsg) {
        final long othersideids = othersideid;
        final ChatMsg chatMsg1 = chatMsg;
        ClientManager.getDefault().getChatService().getMessages(othersideid, 0, 100, 0, new RequestCallBack<Long, List<ChatMsg>, Void>() {

            @Override
            public void handleSuccess(Long aLong, List<ChatMsg> chatMsgs, Void aVoid) {
                int msgType = chatMsgList.get(position).getMsgType();
                boolean isMe = RequestHelper.isMyself(chatMsgList.get(position).getFromID());
                final String copy = VIMClient.getContext().getString(R.string.vim_action_copy);
                final String forward = VIMClient.getContext().getString(R.string.vim_action_forward);
                final String export = VIMClient.getContext().getString(R.string.vim_export);
                final String delete = VIMClient.getContext().getString(R.string.vim_delete);
                final String moreOptions = VIMClient.getContext().getString(R.string.vim_action_more);
                final String reply = "Reply";
                final String recall = context.getString(R.string.vim_recall_msg);
                final String share = "Share";
                boolean isPrivacyMsg = ChatMsgUtil.isPrivateMsg(chatMsgList.get(position));
                boolean isTaskMsg = ChatMsgUtil.isTaskMsg(msgType);
                boolean isDelayMsg = ChatMsgUtil.isDelayMsg(chatMsgList.get(position));
                boolean isReceiptMsg = ChatMsgUtil.isReceiptMsg(chatMsgList.get(position));

                boolean isBurnMsg = ChatMsgUtil.isBurnMsg(chatMsgList.get(position));
                final CharSequence[] items;

                switch (chatMsg1.getMsgType()) {
                    case ChatMsgApi.TYPE_TEXT:
                        if (!isMe || isTaskMsg || isDelayMsg || isReceiptMsg) {
                            items = new CharSequence[]{ copy, reply, forward, delete};
                        } else if (isPrivacyMsg) {

                            items = new CharSequence[]{ reply,delete};
                        } else {
                            items = new CharSequence[]{copy, forward,recall, delete};
                        }
                        break;
                    case ChatMsgApi.TYPE_FILE:
                        if (!isMe || isPrivacyMsg) {
                            items = new CharSequence[]{share,forward, reply, export, delete};
                        } else {
                            items = new CharSequence[]{share,forward, export, recall,delete};
                        }
                        break;
                    case ChatMsgApi.TYPE_IMAGE:
                    case ChatMsgApi.TYPE_MINI_VIDEO:
                        if (!isMe || isPrivacyMsg) {
                            items = new CharSequence[]{share,forward, reply, export, delete};
                        } else {
                            items = new CharSequence[]{share,forward, export, recall,delete};
                        }
                        break;
                    case ChatMsgApi.TYPE_AUDIO:
                        if (!isMe) {
                            items = new CharSequence[]{share,forward, export, reply, delete};
                        } else {
                            items = new CharSequence[]{share,forward, export,  recall, delete};
                        }
                        break;
                    case ChatMsgApi.TYPE_CARD:
                    case ChatMsgApi.TYPE_DYNAMIC:
                    case ChatMsgApi.TYPE_POSITION:
                    case ChatMsgApi.TYPE_WEB_LINK:
                    case ChatMsgApi.TYPE_CUSTOM_DYNAMIC:
                        if (!isMe || isPrivacyMsg || isTaskMsg || isDelayMsg || isReceiptMsg) {
                            items = new CharSequence[]{share,forward, reply, delete};
                        } else {
                            items = new CharSequence[]{share,forward, recall,delete};
                        }
                        break;
                    case ChatMsgApi.TYPE_NEWS:
                    case ChatMsgApi.TYPE_TEMPL:
                        if (!isMe || isPrivacyMsg || isTaskMsg || isDelayMsg || isReceiptMsg) {
                            items = new CharSequence[]{share,forward, reply, delete};
                        } else {
                            items = new CharSequence[]{share,forward, recall, delete};
                        }
                        break;
                    case ChatMsgApi.TYPE_MULTI:
                    case ChatMsgApi.TYPE_VIDEO:
                    case ChatMsgApi.TYPE_VOICE:
                    default:
                        items = new CharSequence[]{delete};
                        break;
                }
//                                    }

                final MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        String s = charSequence.toString();
                        if (s.equals(forward)) {
                            switch (chatMsgList.get(position).getMsgType()) {
                                case ChatMsgApi.TYPE_TEXT:
                                case ChatMsgApi.TYPE_IMAGE:
                                case ChatMsgApi.TYPE_FILE:
                                case ChatMsgApi.TYPE_MINI_VIDEO:
                                case ChatMsgApi.TYPE_AUDIO:
                                    Intent intent = new Intent(context, SelectChatActivity.class);
                                    Bundle extras = new Bundle();

                                    extras.putLong("fwd_msg_id", chatMsgList.get(position).getMsgID());
                                    extras.putLong("fwd_tgt_id", chatMsgList.get(position).getID());
                                    extras.putString("type", "TYPE_FORWARD");

                                    intent.putExtras(extras);
                                    context.startActivity(intent);
                                    break;

                            }
//                                                itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_FORWARD, chatMsgList.get(position));
                        } else if (s.equals(reply)) {
                            switch (chatMsgList.get(position).getMsgType()) {
                                case ChatMsgApi.TYPE_TEXT:
                                case ChatMsgApi.TYPE_IMAGE:
                                case ChatMsgApi.TYPE_FILE:
                                case ChatMsgApi.TYPE_MINI_VIDEO:
                                case ChatMsgApi.TYPE_AUDIO:
                                    MaterialDialog dialog = new MaterialDialog.Builder(context)
                                            .title("Reply")
                                            .content("")
                                            .inputType(InputType.TYPE_CLASS_TEXT)
                                            .input("Reply Content", "", new MaterialDialog.InputCallback() {
                                                        @Override
                                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                                            ChatMsgBuilder chatMsgBuilder = new ChatMsgBuilder(othersideids);
                                                            final ChatMsg chatMsg = chatMsgBuilder.createTxtMsg(input.toString());
                                                            final long msgid = chatMsgList.get(position).getMsgID();


                                                            String msgids = Objects.toString(msgid);
                                                            chatMsg.setPreDefined(msgids + ";11" + ";5;" + ";unread;" + "");


                                                            chatService.sendMsg(chatMsg, new ResultCallBack<Void, Void, Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {

                                                                    SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                                                                    counter = prefs.getInt("shared_send_counter", counter);
                                                                    counter = counter + 1;
                                                                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE).edit();
                                                                    editor.putInt("shared_send_counter", counter);
                                                                    editor.apply();
                                                                    Log.e("Reply Message", "Send Successful");
                                                                    Log.e("Reply Pre", chatMsg.getPreDefined());

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
                                                    }
                                            ).show();


                                    break;

                            }


                        } else if (s.equals(delete)) {
                            List<Long> listMsgID = new ArrayList<Long>();
                            listMsgID.add(chatMsgList.get(position).getMsgID());
                            RequestHelper.deleteMsgByID(chatMsgList.get(position).getID(), listMsgID, new RequestCallBack() {
                                @Override
                                public void handleSuccess(Object o, Object o2, Object o3) {
                                    Log.e("Msg Delete", "Successful");
                                    chatMsgList.remove(chatMsgList.get(position));
                                    notifyDataSetChanged();

                                }
                            });

                        } else if (s.equals(recall)) {
                            List<Long> listMsgID = new ArrayList<Long>();
                            listMsgID.add(chatMsgList.get(position).getMsgID());

                            long cur_time = System.currentTimeMillis();
                            long msg_time = chatMsgList.get(position).getTime();

                            if (cur_time > (msg_time + 120000)) {
                                Toast.makeText(context, "You can only recall a message that sent within 2 minutes.", Toast.LENGTH_SHORT).show();
                            } else {
                                ChatMsg chatmsg = chatMsgList.get(position);
//                                chatMsgList.remove(chatMsgList.get(position));
                                notifyDataSetChanged();
                                RequestHelper.deleteMsgByID(chatmsg.getID(), listMsgID, new RequestCallBack() {
                                    @Override
                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                        chatMsgList.remove(chatMsgList.get(position));
                                        notifyDataSetChanged();

                                        Log.e("Msg Revoke", " Successful");
                                        notifyDataSetChanged();

                                    }

                                });
                                notifyDataSetChanged();
                                ChatMsgUtil.sendWithdraw(chatmsg.getID(), chatmsg.getBody(), chatmsg.getMsgID(), new RequestCallBack() {
                                    @Override
                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                        notifyDataSetChanged();
                                        Intent intent = new Intent(context, Chat_RecyclerView_Activity.class);
                                        intent.putExtra("othersideid", chatMsgList.get(position).getID());
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        context.startActivity(intent);
                                        Log.e("Revoke Msg", "Sent");
                                        // TODO HAVE TO REFRESH THE LIST

                                    }
                                });
                                notifyDataSetChanged();


                            }

                        } else if (s.equals(copy)) {
                            Utils.copyTxt(chatMsgList.get(position).getBody());
                            ToastUtil.showShort("Copied ");
                        } else if (s.equals(export)) {
                            if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {
                                MsgImg msgImg = (MsgImg) chatMsgList.get(position);
                                String key = msgImg.getEncryptKey();//key for decryption
                                String localPath = msgImg.getMainLocalPath();
                                String downPath = msgImg.getMainDownloadPath();
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/img/" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                                RequestHelper.downloadImage(msgImg.getTargetID(), downPath, new RequestCallBack<String, Long, Void>() {
                                    @Override
                                    public void handleSuccess(String s, Long aLong, Void aVoid) {

                                    }
                                }, new RequestCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void handleSuccess(Integer integer, Integer integer2, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Toast.makeText(context, "Image Exported To " + destPath, Toast.LENGTH_SHORT).show();
                            } else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                                MsgAudio msgAud = (MsgAudio) chatMsgList.get(position);
                                String key = msgAud.getEncryptKey();//key for decryption
                                String localPath = msgAud.getLocalPath();
                                String downPath = msgAud.getDownloadPath();
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/audio/" + String.valueOf(System.currentTimeMillis()) + ".3gp";
                                chatService.downloadFile(msgAud, (byte) 2, new ResultCallBack<String, Long, Void>() {
                                    @Override
                                    public void onSuccess(String s, Long aLong, Void aVoid) {
                                        Log.e("AudioDownload", "Complete");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("AudioDownload", "Fail");
                                    }
                                }, new ResultCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void onSuccess(Integer integer, Integer integer2, String s) {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Toast.makeText(context, "Audio Exported To " + destPath, Toast.LENGTH_SHORT).show();

                            } else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                                MsgMiniVideo msgMiniVid = (MsgMiniVideo) chatMsgList.get(position);
                                String key = msgMiniVid.getEncryptKey();//key for decryption
                                String localPath = msgMiniVid.getLocalVideoPath();
                                String downPath = msgMiniVid.getVideoDownloadPath();
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/video/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
                                chatService.downloadFile(msgMiniVid, (byte) 2, new ResultCallBack<String, Long, Void>() {
                                    @Override
                                    public void onSuccess(String s, Long aLong, Void aVoid) {
                                        Log.e("MiniVideo Download", "Complete");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("Minivideo Download", "Fail");
                                    }
                                }, new ResultCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void onSuccess(Integer integer, Integer integer2, String s) {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Toast.makeText(context, "MiniVideo Exported To " + destPath, Toast.LENGTH_SHORT).show();

                            } else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                                MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                                String key = msgFile.getEncryptKey();//key for decryption
                                String localPath = msgFile.getLocalPath();
                                String downPath = msgFile.getDownloadPath();
                                String ext = msgFile.getFileName();
                                String extension = ext.substring(ext.lastIndexOf("."));
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/file/" + String.valueOf(System.currentTimeMillis()) + extension;
                                chatService.downloadFile(msgFile, (byte) 2, new ResultCallBack<String, Long, Void>() {
                                    @Override
                                    public void onSuccess(String s, Long aLong, Void aVoid) {
                                        Log.e("File Download", "Complete");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("File Download", "Fail");
                                    }
                                }, new ResultCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void onSuccess(Integer integer, Integer integer2, String s) {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Toast.makeText(context, "File Exported To " + destPath, Toast.LENGTH_SHORT).show();

                            }
                        } else if (s.equals(share)) {
                            if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {
                                MsgImg msgImg = (MsgImg) chatMsgList.get(position);
                                String key = msgImg.getEncryptKey();//key for decryption
                                String localPath = msgImg.getMainLocalPath();
                                String downPath = msgImg.getMainDownloadPath();
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/img/" + String.valueOf(System.currentTimeMillis()) + ".jpg";

                                RequestHelper.downloadImage(msgImg.getTargetID(), downPath, new RequestCallBack<String, Long, Void>() {
                                    @Override
                                    public void handleSuccess(String s, Long aLong, Void aVoid) {

                                    }
                                }, new RequestCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void handleSuccess(Integer integer, Integer integer2, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Uri finaluri = Uri.parse(destPath);
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, finaluri);
                                shareIntent.setType("image/jpeg");
                                context.startActivity(Intent.createChooser(shareIntent, "Sending Image Msg"));


                            } else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                                MsgAudio msgAud = (MsgAudio) chatMsgList.get(position);
                                String key = msgAud.getEncryptKey();//key for decryption
                                String localPath = msgAud.getLocalPath();
                                String downPath = msgAud.getDownloadPath();
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/audio/" + String.valueOf(System.currentTimeMillis()) + ".3gp";
                                chatService.downloadFile(msgAud, (byte) 2, new ResultCallBack<String, Long, Void>() {
                                    @Override
                                    public void onSuccess(String s, Long aLong, Void aVoid) {
                                        Log.e("AudioDownload", "Complete");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("AudioDownload", "Fail");
                                    }
                                }, new ResultCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void onSuccess(Integer integer, Integer integer2, String s) {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Uri finaluri = Uri.parse(destPath);

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, finaluri);
                                shareIntent.setType("audio/*");

                                context.startActivity(Intent.createChooser(shareIntent, "Sending Audio Msg"));

                            } else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                                MsgMiniVideo msgMiniVid = (MsgMiniVideo) chatMsgList.get(position);
                                String key = msgMiniVid.getEncryptKey();//key for decryption
                                String localPath = msgMiniVid.getLocalVideoPath();
                                String downPath = msgMiniVid.getVideoDownloadPath();
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/video/" + String.valueOf(System.currentTimeMillis()) + ".mp4";
                                chatService.downloadFile(msgMiniVid, (byte) 2, new ResultCallBack<String, Long, Void>() {
                                    @Override
                                    public void onSuccess(String s, Long aLong, Void aVoid) {
                                        Log.e("MiniVideo Download", "Complete");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("Minivideo Download", "Fail");
                                    }
                                }, new ResultCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void onSuccess(Integer integer, Integer integer2, String s) {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Uri finaluri = Uri.parse(destPath);

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, finaluri);
                                shareIntent.setType("video/*");
                                context.startActivity(Intent.createChooser(shareIntent, "Sending video Msg"));

                            } else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                                MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                                String key = msgFile.getEncryptKey();//key for decryption
                                String localPath = msgFile.getLocalPath();
                                String downPath = msgFile.getDownloadPath();
                                String ext = msgFile.getFileName();
                                String extension = ext.substring(ext.lastIndexOf("."));
                                String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/file/" + String.valueOf(System.currentTimeMillis()) + extension;
                                chatService.downloadFile(msgFile, (byte) 2, new ResultCallBack<String, Long, Void>() {
                                    @Override
                                    public void onSuccess(String s, Long aLong, Void aVoid) {
                                        Log.e("File Download", "Complete");
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        Log.e("File Download", "Fail");
                                    }
                                }, new ResultCallBack<Integer, Integer, String>() {
                                    @Override
                                    public void onSuccess(Integer integer, Integer integer2, String s) {

                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }
                                });
                                RequestHelper.decryptFile(key, downPath, destPath);
                                Uri finaluri = Uri.parse(destPath);

                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, finaluri);
                                shareIntent.setType("*/*");
                                context.startActivity(Intent.createChooser(shareIntent, "Sending Filesdrt Msg"));
                            }
                        } else {
                            Toast.makeText(context, "Message Type Not Support Yet", Toast.LENGTH_SHORT).show();

                        }
                    }
                };

                DialogUtil.buildOperateDialog(context, items, itemOperateCallBack).show();


            }
        });


    }

}
package com.kpz.AnyChat.Chat;

/*

                `-/osyhdddhyso/-`
            ./ydMMMMMMMMMMMMMMMMMdy/`
         `/hNNNNMMNNNNNNNNNNNNNNNNNmNh/`
       .omNNNNNNmmmdddddddmmmmmmdhhyhNMd`
      /mNNNNNmdyhyo+oosssyyyyyyyyyyysyMN:
    `smNNNNNNdy+:---:/+oo+oooossssyssoMN.`
   `hmNNNNNNdo:--.---:://+++ooooossyyyMd````
   /yhdmNNNm+--...-:+syhhhyso+++oshdmmm+``````
  `-.:+oymNy:------/+osyyyyys+osyddddhyo+-````
  -.:/::.+mo...--/oyhdmmhdhy++oydmNddds-/h-...`
  /-oo+:..y/``..-:+yssyyyhs///+ymmmdhys--+....`
  h//:/:-`:-```..-:/++oo++/::--oyhhhyys:-.....`
  sds:.--.``````...--:://+:--.-/oyysss+-......`
  :dds``.```````...----:+o:----:+syso+:---....
   sdy``````````....---://///++oyyyo+:------.`
   `yo``.````````...----:--::/soyss+/-------.
    `.....`````.`....--:+ooossyhhys/-------.
      ``..``........--::////+syhyyo----...`
         ```.---------::::/+ossso+-.---.`
          `.`.-::/::-----::/oosoo-....`
            ```.-:/oo+//++osssso:.``
                 `.-:osssso++:-`

█████████____█████____██████__██████
 ███__████_█████████_███__██_███__██
 ███__████_████_████_████____████
 ████████__███___███__█████___█████
 ███__████_████_████_____███_____███
 ███__████__███████__███████_██████
 ████████____█████____█████___█████
        SAVE SEND RECEIVE STORE PLAY
           MAY OUR BOSS PROTECT US
            BUG BUG FAR FAR AWAY


* split [0] = ExtraName
* split [1] = Func Type
*
* Type 0 - Eat
* Type 1 - Stunt
* Type 2 - Blackout
* Type 3 - VoiceChange
*
* split [2] = Func Param
* */

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.kpz.AnyChat.Others.DateTimeUtils;

import com.kpz.AnyChat.Others.MsgReceiver;

import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;

import com.kpz.AnyChat.R;

import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.chatbean.MsgAudio;
import com.vrv.imsdk.chatbean.MsgFile;
import com.vrv.imsdk.chatbean.MsgImg;
import com.vrv.imsdk.chatbean.MsgMiniVideo;
import com.vrv.imsdk.chatbean.MsgRevoke;
import com.vrv.imsdk.chatbean.MsgTip;
import com.vrv.imsdk.listener.ProgressListener;
import com.vrv.imsdk.listener.ReceiveMsgListener;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.ResultCallBack;

import org.w3c.dom.Text;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static com.kpz.AnyChat.Chat.ChatActivity.MY_PREFS_NAME;

/**
 * Created by wenrong on 14/7/2017.
 */

public class ConversationHistoryArrayAdapter extends ArrayAdapter<ChatMsg> {
    private final Context context;
    private final Context context2;
    Boolean onlyonce = false;
    private int tipType;
    public boolean canGetHistory = true;
    private final List<ChatMsg> chatMsgList;
    private long touchStart = 0l;
    protected ChatMsgItemView itemView;
    final ChatService chatService = ClientManager.getDefault().getChatService();
    String audiopath, dlaudiopath;
    int count;
    List<Long> relatedUsers = new ArrayList<>();
    protected OnReSendChatMsgListener reSendListener;
    int pos;
    long lastMsgID;
    ChatCallback chatCallback;
    static final int REDIRECT_MSG = 99;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    int streamid;
    boolean isplaying = false;
//    public void setReSendListener(OnReSendChatMsgListener listener) {
//        this.reSendListener = listener;
//    }

    public int getTipType() {
        return this.tipType;
    }

    public ConversationHistoryArrayAdapter(Context context, List<ChatMsg> chatMsgList, Context context2) {
        super(context, R.layout.chat, chatMsgList);
        this.context = context;
        this.context2 = context2;
        this.chatMsgList = chatMsgList;

    }

    static class ViewHolderText {
        TextView chl_tv1;
        TextView chl_tv2;
        ImageView img_read;
        TextView timestamp;
        ImageView text_msg_state;

        ViewHolderText(View view) {
        }
    }

    static class ViewHolderTips {
        TextView txts;
        TextView tv_sysmsg;

    }

    static class ViewHolderImg {
        TextView chl_tv1;
        TextView chl_tv2;
        TextView timestamp;
        ImageView img;
        ImageView text_msg_state;

        ViewHolderImg(View view) {
        }
    }

    static class ViewHolderMiniVideo {
        TextView sender_video_id;
        TextView timestamp;
        ImageView videoss;
        ImageView videos;

        ViewHolderMiniVideo(View view) {
        }
    }

    static class ViewHolderAudio {
        ImageButton imgview;
        TextView timestampaudio;
        TextView sender_audio_id;

        ViewHolderAudio(View view) {
        }
    }

    static class ViewHolderFile {
        TextView chl_tv1;
        TextView sender_attachment_name;
        TextView sender_attachment_size;
        TextView sender_timestamp;
        ImageButton sender_attachment_download;
        TextView receiver_attachment_name;
        TextView receiver_attachment_size;
        TextView receiver_timestamp;
        ImageButton receiver_attachment_download;

        ViewHolderFile(View view) {
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        long Sendername = chatMsgList.get(position).getFromID();
        long Receivername = chatMsgList.get(position).getTargetID();
        SimpleDateFormat Text = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
        String textformat = Text.format(chatMsgList.get(position).getTime());

        Date currentTextTime = Calendar.getInstance().getTime();
        long TextmillisIn1Days = 1 * 24 * 60 * 60 * 1000;

        if (ChatMsgApi.TYPE_REVOKE == chatMsgList.get(position).getMsgType()) {

            View rowView33 = inflater.inflate(R.layout.vim_sys_msg, parent, false);
            MsgRevoke revoke = (MsgRevoke) chatMsgList.get(position);

            TextView txts = (TextView) rowView33.findViewById(R.id.tv_sysmsg);
            TextView temp = (TextView) rowView33.findViewById(R.id.temp);

            String abc = Objects.toString(revoke.getFromID());
            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + revoke.getFromID();
            Utils.setNickname(getContext(), url, abc, temp);
            String name = temp.getText().toString();
            txts.setText(name + " Had Recall a Message");

            return rowView33;
        }

        else if (ChatMsgApi.TYPE_WEAK_HINT == chatMsgList.get(position).getMsgType()) {
            final MsgTip msgTip = (MsgTip) chatMsgList.get(position);
            View rowView34 = inflater.inflate(R.layout.null_activity, parent, false);
            View rowView33 = inflater.inflate(R.layout.vim_sys_msg, parent, false);
            TextView txts = (TextView) rowView33.findViewById(R.id.tv_sysmsg);

            int tipType = msgTip.getTipType();    //tip 类型
            int oprType = msgTip.getOprType();    //操作类型
            String tipTime = msgTip.getTipTime(); //时间
            final String oprUser = msgTip.getOprUser(); //操作userId
            final String userInfo = msgTip.getUserInfo();//用户信息
            String fileInfo = msgTip.getFileInfo();///<文件信息
            long id = msgTip.getID();

                /*
                * Retrieve Name From UCC API server
                * */
            TextView tempq = (TextView) rowView33.findViewById(R.id.temp);
            TextView temps = (TextView) rowView33.findViewById(R.id.temps);

            String abcs = Objects.toString(msgTip.getFromID());
            String urls = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgTip.getOprUser();
            Utils.setNickname(getContext(), urls, abcs, tempq);

            String abcd = Objects.toString(msgTip.getToID());
            String urlss = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgTip.getUserInfo();
            Utils.setNickname(getContext(), urlss, abcd, temps);

            Log.e("Opr URL = ", urls);
            Log.e("User URL = ", urlss);
            String userinfo;
            String oprname = tempq.getText().toString();
            if(!userInfo.equals("System")) {
                userinfo = temps.getText().toString();
            } else {
                userinfo = "System";
            }
//                Toast.makeText(context, oprname + " OprN", Toast.LENGTH_SHORT).show();
            TelephonyManager telephonyManager;
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();
            String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
            String group_ID = String.valueOf(chatMsgList.get(position).getID());
            SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
            ViewHolderTips holderTips = null;
//            if (convertView == null){
//                holderTips.tv_sysmsg = (TextView)rowView33.findViewById(R.id.tv_sysmsg);
//            } else {
//                holderTips = (ViewHolderTips) convertView.getTag();
//            }
            switch (tipType) {

                case MsgTip.TYPE_GROUP: //群弱提示
                    Log.e("Tip Msg ", "Received " + oprType);
                    if (oprType == 0) {
                        txts.setText(userinfo + " Had Join The Group");
                        String url_join_group = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
                        Http_GetToken gt = new Http_GetToken(context, 8, url_join_group, ids, tempq, deviceId,group_ID ,"", "");
                        gt.execute();

                    } else if (oprType == 1) {
//                        txts.setText(oprname + " Invite " + userinfo + " To Group Chat");
                        txts.setText("Group Created ! ");
                    } else if (oprType == 2) {
                        txts.setText(oprname + " Accepted " + userinfo + "'s Join Request ");
                    } else if (oprType == 3) {
                        txts.setText(userinfo + "Had Left The Group");
                    } else if (oprType == 4) {
                        txts.setText(userinfo + " Kicked by " + oprUser);
                    } else if (oprType == 5) {
                        View rowView38 = inflater.inflate(R.layout.null_activity, parent, false);
                        String ack_UID = ((MsgTip) chatMsgList.get(position)).getUserInfo();
                        String msgid = String.valueOf(((MsgTip) chatMsgList.get(position)).getOprUser());
                        Log.e("result", ack_UID + " " + msgid);
                        String read = "AR";
                        Utils.ChangeMsgStatus(context, ack_UID, msgid, read, "");
                        return rowView38;
                    }

                    break;
            }
            return rowView33;

        }


//================================================================SENDER================================================================

        if (ClientManager.getDefault().getAccountService().isMySelf(chatMsgList.get(position).getFromID())) {

            View rowView = inflater.inflate(R.layout.sender_chat_bubble, parent, false);
            final TextView dummys = (TextView) rowView.findViewById(R.id.textView258);
//            TextView chl_tv1 = (TextView) rowView.findViewById(R.id.sender_nickname);
            final TextView chl_tv2 = (TextView) rowView.findViewById(R.id.sender_message_text);
            final TextView timestamp = (TextView) rowView.findViewById(R.id.sender_timestamp);
            ImageView text_msg_state = (ImageView) rowView.findViewById(R.id.text_msg_state);
            final ProgressBar progressbar = (ProgressBar) rowView.findViewById(R.id.progress_chat_send);
            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//            Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", chl_tv1);
            timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
            final ImageView img_read = (ImageView) rowView.findViewById(R.id.text_msg_state);


            /*
            * Check if current Status = All Read, else two grey tick
            * If all read, show two green tick
            * */
            //If Text Message Detected
            if (ChatMsgApi.TYPE_TEXT == chatMsgList.get(position).getMsgType()) {

                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", dummys);
                    Log.e("Read Status Return", dummys.getText().toString());

                    if (dummys.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        img_read.setImageResource(R.drawable.received);


                    } else if (dummys.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        img_read.setImageResource(R.drawable.read);

                    } else {
                        img_read.setImageResource(R.drawable.sent);

                    }
                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        img_read.setImageResource(R.drawable.sending);
                    }
                }

                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {

                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            img_read.setImageResource(R.drawable.received);

                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);


                String pre = chatMsgList.get(position).getPreDefined();
                String[] split = pre.split(";");
                Log.e("Pre Type0 ", pre);


                /*
                * Reply Message
                */
                if (!pre.equals("") && split[1].equals("11")) {

                    Log.e("Pre Type1 ", split[1]);
                    View replyview = inflater.inflate(R.layout.sender_reply_layout, parent, false);
                    TextView timestaptss = (TextView) replyview.findViewById(R.id.sender_timestamp);
                    final TextView replycontent = (TextView) replyview.findViewById(R.id.reply_content);
                    TextView senderreply = (TextView) replyview.findViewById(R.id.sender_reply_body);
                    final TextView dummy = (TextView) replyview.findViewById(R.id.dummy);
                    TextView backgroundfield = (TextView) replyview.findViewById(R.id.background);
                    final ImageView img_read_reply = (ImageView) replyview.findViewById(R.id.read_icons);
                    final TextView Origin_Timestamp = (TextView) replyview.findViewById(R.id.Origin_Timestamp);


                    final long reply_tgt_id = chatMsgList.get(position).getID();
                    final long reply_msg_id = Long.parseLong(split[0]);
                    Log.e("Fetch Msg ID ",reply_tgt_id+"");
                    Log.e("Msg ID ",reply_msg_id+"");

//                    timestaptss.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                    senderreply.setText(chatMsgList.get(position).getBody());

                    msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                    msgstatus = "";
                    read = "";

                    if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                        Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", dummys);
                        Log.e("Read Status Return", dummys.getText().toString());

                        if (dummys.getText().toString().equals("unread")) {
                            //Unread, change into read status in SQLite DB
                            Utils.ChangeMsgStatus(context, id, msgid, read, "");
                            img_read_reply.setImageResource(R.drawable.received);


                        } else if (dummys.getText().toString().equals("read")) {
                            //set Two Tick (All read)
                            img_read_reply.setImageResource(R.drawable.read);

                        } else {
                            img_read_reply.setImageResource(R.drawable.sent);

                        }
                    } // Message successfully sent
                    else {
                        if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                            img_read_reply.setImageResource(R.drawable.sending);
                        }
                    }

                    chatService.observeMsgListener(new ReceiveMsgListener() {
                        @Override
                        public void receive(ChatMsg chatMsg) {
                        }

                        @Override
                        public void update(ChatMsg chatMsg) {

                            if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                                img_read_reply.setImageResource(R.drawable.received);

                            } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                            }
                        }
                    }, true);

                    backgroundfield.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            LayoutInflater inflater = (LayoutInflater) context
//                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                            View ah_list = inflater.inflate(R.layout.receiver_chat_bubble, null);

//                            ListView list = (ListView)ah_list.findViewById(R.id.ah_listView);

//                            list.smoothScrollToPosition(position);
                            Boolean match = false;
                            for (int i = 0; i < chatMsgList.size(); i++) {

                                if (chatMsgList.get(i).getMsgID() == reply_msg_id) {
                                /*
                                * Match Case
                                * */
                                    final int positions = chatMsgList.indexOf(chatMsgList.get(i));
                                    long othersideid = chatMsgList.get(position).getID();
                                    Intent intents = new Intent(context, ChatActivity.class);
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
                    ClientManager.getDefault().getChatService().getMessages(reply_tgt_id, reply_msg_id, 1, 0, new ResultCallBack<Long, List<ChatMsg>, Void>() {
                        @Override
                        public void onSuccess(Long aLong, final List<ChatMsg> chatMsgs, Void aVoid) {
                            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(0).getFromID();
                            Utils.setNickname(context2, url, chatMsgList.get(0).getFromID() + "", dummy);
                            String name = dummy.getText().toString();
                            switch (chatMsgs.get(0).getMsgType()) {
                                case ChatMsgApi.TYPE_TEXT:
                                    String reply_body = chatMsgs.get(0).getBody();
                                    replycontent.setText(name + " : " + reply_body);
                                    break;
                                case ChatMsgApi.TYPE_IMAGE:
                                    replycontent.setText(name + " : [IMAGE]");
                                    break;
                                case ChatMsgApi.TYPE_MINI_VIDEO:
                                    replycontent.setText(name + " : [VIDEO]");
                                    break;
                                case ChatMsgApi.TYPE_AUDIO:
                                    replycontent.setText(name + " : [AUDIO]");
                                    break;
                                case ChatMsgApi.TYPE_FILE:
                                    replycontent.setText(name + " : [FILE]");
                                    break;
                            }


                            Origin_Timestamp.setText(DateTimeUtils.formatTime(context, chatMsgs.get(0).getTime(), true));

                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                    return replyview;
                } else {

                    ViewHolderText holderText = null;
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                    holderText = new ViewHolderText(convertView);

                    boolean shown = false;
                    if (chatMsgList.get(position).isBurn()) {
                        if (shown == false) {
                            Toast.makeText(context, "Burn Chat !", Toast.LENGTH_SHORT).show();
                            shown = true;
                        }
                    }
//-----------------------------BEGINNING OF TORNADO FUNCTION && Reply Message Function--------------------------

                    final Random rand = new Random();
                    final int max = chatMsgList.size();

                    Log.e("Related Received ", chatMsgList.get(position).getRelatedUsers() + "");
                    Log.e("Pre Received ", pre);


                    if (!pre.equals("") && !split[1].isEmpty()) {
                        Log.e("Pre Type ", split[1]);

                        for (int i = 0; i < chatMsgList.get(position).getRelatedUsers().size(); i++) {

                            Long exp = chatMsgList.get(position).getRelatedUsers().get(i);
                            if (split[1].equals("4") && RequestHelper.isMyself(exp)) {
                                Toast.makeText(context, "Tordano !", Toast.LENGTH_SHORT).show();
                                new CountDownTimer(5000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        long time = millisUntilFinished / 1000;
                                        pos = rand.nextInt(max);
                                        chl_tv2.setText(chatMsgList.get(pos).getBody());
                                    }

                                    @Override
                                    public void onFinish() {
                                        Log.e("Procesed Pre ", chatMsgList.get(position).getPreDefined());

                                        chatMsgList.get(position).setPreDefined("name;99;5");
                                        RequestHelper.updateMsg(chatMsgList.get(position), new RequestCallBack() {
                                            @Override
                                            public void handleSuccess(Object o, Object o2, Object o3) {
                                                //Changed Successfully
                                                Log.e("Reset Pre ", chatMsgList.get(position).getPreDefined());
                                                chl_tv2.setText(chatMsgList.get(position).getBody());
                                                Toast.makeText(context, "Looks like tornado had gone !", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                }.start();
                            } else {
                                chl_tv2.setText(chatMsgList.get(position).getBody());
                            }
                        }
                    }
//-----------------------------END OF TORNADO FUNCTION--------------------------
                    chl_tv2.setText(chatMsgList.get(position).getBody());


                    if (convertView == null) {
                        holderText.text_msg_state = (ImageView) rowView.findViewById(R.id.text_msg_state);
                        holderText.chl_tv2 = (TextView) rowView.findViewById(R.id.sender_message_text);
                        holderText.timestamp = (TextView) rowView.findViewById(R.id.sender_timestamp);
                        holderText.chl_tv2.setText(chatMsgList.get(position).getBody());
                        holderText.timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                        convertView.setTag(holderText);

                    } else {
                        holderText = (ViewHolderText) convertView.getTag();
                    }

                }
            }

            //If Image Detected
            else if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {
                ViewHolderImg holderImg = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderImg = new ViewHolderImg(convertView);
                ImageView img = (ImageView) rowView.findViewById(R.id.image_send);
                MsgImg msgImg = (MsgImg) chatMsgList.get(position);

                String key = msgImg.getEncryptKey();//key for decryption
                String localPath = msgImg.getMainLocalPath();
                String downPath = msgImg.getMainDownloadPath();

                String img_id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String img_msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatuss = "";
                String read = "";
                chl_tv2.setText("Uploading..."); //UserID

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                    Utils.getMsgReadStatus(context, img_id, img_msgid, msgstatuss, "", dummys);
                    Log.e("Read Status Return", dummys.getText().toString());
                    if (dummys.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
                        Utils.ChangeMsgStatus(context, img_id, img_msgid, read, "");
                        img_read.setImageResource(R.drawable.received);


                    } else if (dummys.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        img_read.setImageResource(R.drawable.read);

                    } else {
                        img_read.setImageResource(R.drawable.sent);

                    }
                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        img_read.setImageResource(R.drawable.sending);
                    }
                }
                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            img_read.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);

                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown == false) {
                        Toast.makeText(context, "Burn Chat !", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }
                }
                if (convertView == null) {
                    holderImg.chl_tv2 = (TextView) rowView.findViewById(R.id.sender_message_text);
                    holderImg.timestamp = (TextView) rowView.findViewById(R.id.sender_timestamp);
                    holderImg.img = (ImageView) rowView.findViewById(R.id.image_send);
                    holderImg.timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                    holderImg.text_msg_state = (ImageView) rowView.findViewById(R.id.text_msg_state);
                    convertView.setTag(holderImg);
                } else {
                    holderImg = (ViewHolderImg) convertView.getTag();
                }
                String paths;
                if (!TextUtils.isEmpty(localPath)) {
                    paths = localPath;
                } else {
                    paths = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                }
                Picasso.with(getContext())
                        .load(("file:///" + paths))
                        .resize(500, 500)
                        .centerCrop()
                        .into(img);

                final File file = new File(paths);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(file), type);
                        context.startActivity(intent);
                    }
                });
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());

                timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));

            }


            //If Video Detected
            else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                ViewHolderMiniVideo holderMiniVideo = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderMiniVideo = new ViewHolderMiniVideo(convertView);

                MsgMiniVideo msgVideo = (MsgMiniVideo) chatMsgList.get(position);
                View rowView11 = inflater.inflate(R.layout.sender_video_bubble, parent, false);

                String key = msgVideo.getEncryptKey();//key for decryption
                String localPath = msgVideo.getLocalVideoPath();
                String downPath = msgVideo.getVideoDownloadPath();
                String pre = msgVideo.getPreImgUrl();

                String videopath;
                final ImageView img_send_video = (ImageView) rowView11.findViewById(R.id.video_msg_state);
                TextView dummys1 = (TextView) rowView11.findViewById(R.id.dummy1);
                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";
                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {

                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", dummys1);
                    Log.e("Vid Read Status Return", dummys.getText().toString());


                    if (dummys.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        img_send_video.setImageResource(R.drawable.received);

                    } else if (dummys.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        img_send_video.setImageResource(R.drawable.read);

                    } else {
                        img_send_video.setImageResource(R.drawable.sent);

                    }
                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        img_send_video.setImageResource(R.drawable.sending);
                    }
                }

                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            img_send_video.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);

                ImageView videoss = (ImageView) rowView11.findViewById(R.id.video_player_sender);
                final ProgressBar progressbar1 = (ProgressBar) rowView11.findViewById(R.id.progress_chat_send);

                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown == false) {
                        Toast.makeText(context, "Burn Chat !", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }
                }
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.sender_video_bubble, parent, false);
                } else {
                    holderMiniVideo = (ViewHolderMiniVideo) convertView.getTag();
                }
                TextView sender_timestamp = (TextView) rowView11.findViewById(R.id.sender_timestamp);
//                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", sender_video_id);
                sender_timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                File f = new File(localPath);
                if (!TextUtils.isEmpty(localPath) && f.exists()) {
                    videopath = localPath;
                    Log.e("Video Path L", videopath);
                } else {
                    videopath = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                    Log.e("Video Path", videopath);
                }
                final File files = new File(videopath);

                RequestHelper.downloadImage(msgVideo.getID(), pre, new RequestCallBack<String, Long, Void>() {
                    @Override
                    public void handleSuccess(String s, Long aLong, Void aVoid) {
                        chatService.observeProgressListener(new ProgressListener() {
                            @Override
                            public void progress(long l, int i, String s) {
                                if(i>100){
                                    progressbar.setVisibility(GONE);
                                }
                                else{
                                    progressbar.setVisibility(View.VISIBLE);
                                }
                            }
                        },true);
                    }
                }, new RequestCallBack<Integer, Integer, String>() {
                    @Override
                    public void handleSuccess(Integer integer, Integer integer2, String s) {

                    }
                });


                String paths = msgVideo.getPreImgDownloadPath();
                String thumb = ClientManager.getDefault().getFileService().decryptFile(key, paths);
                Picasso.with(getContext())
                        .load("file:///" + thumb)

                        .resize(500, 500)
                        .centerCrop()
                        .into(videoss);

                videoss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyDataSetChanged();
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = files.getName().substring(files.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(files), type);
                        context.startActivity(intent);
                    }
                });


                return rowView11;


            }


            //If Audio Detected
            else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {
                ViewHolderAudio holderAudio = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderAudio = new ViewHolderAudio(convertView);
                final View rowView2 = inflater.inflate(R.layout.sender_audio_bubble, parent, false);
                MsgAudio msgAudio = (MsgAudio) chatMsgList.get(position);
                audiopath = msgAudio.getLocalPath(); //localpath
                dlaudiopath = msgAudio.getDownloadPath();
                final ImageView img_send_audio = (ImageView) rowView2.findViewById(R.id.img_msg_state);
                TextView dummys2 = (TextView) rowView2.findViewById(R.id.dummy2);

                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";

                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", dummys2);
                    Log.e("Aud Read Status Return", dummys.getText().toString());

                    if (dummys.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        img_send_audio.setImageResource(R.drawable.received);


                    } else if (dummys.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        img_send_audio.setImageResource(R.drawable.read);

                    } else {
                        img_send_audio.setImageResource(R.drawable.received);

                    }
                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        img_read.setImageResource(R.drawable.sending);
                    }
                }

                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            img_send_audio.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);


                final long time = msgAudio.getMediaTime();
                touchStart = System.currentTimeMillis(); //Starting time
                count = (int) time;
                final String key = msgAudio.getEncryptKey();//key for decryption
                final String localPath = msgAudio.getLocalPath();
                final String downPath = msgAudio.getDownloadPath();
                final String path;
                final ProgressBar progressbar1 = (ProgressBar) rowView2.findViewById(R.id.progress_chat_send);
                final String pathss;


                if (!FileUtils.isExist(localPath)) {

                    chatService.downloadFile(msgAudio, (byte) 2, new RequestCallBack<String, Long, Void>() {
                        @Override
                        public void handleSuccess(String s, Long aLong, Void aVoid) {

                            chatService.observeProgressListener(new ProgressListener() {
                                @Override
                                public void progress(long l, int i, String s) {
                                    if (i < 100) {
                                        progressbar1.setVisibility(View.VISIBLE);
                                    } else {
                                        progressbar1.setVisibility(GONE);


                                    }


                                }
                            }, true);
                        }
                    }, new RequestCallBack<Integer, Integer, String>() {
                        @Override
                        public void handleSuccess(Integer integer, Integer integer2, String s) {

                        }
                    });
                    pathss = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                } else {


                    pathss = ClientManager.getDefault().getFileService().decryptFile(key, localPath);

                }


                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown == false) {
                        Toast.makeText(context, "Burn Chat !", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }
                }
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.sender_audio_bubble, parent, false);
                } else {
                    holderAudio = (ViewHolderAudio) convertView.getTag();
                }
                final ImageView imgview = (ImageView) rowView2.findViewById(R.id.sender_play);
                final TextView timestampaudio = (TextView) rowView2.findViewById(R.id.sender_timestamp);
//                TextView sender_audio_id = (TextView) rowView2.findViewById(R.id.sender_nickname);
//                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", sender_audio_id);

                timestampaudio.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));


                final SoundPool sp;
                sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
                String pre = chatMsgList.get(position).getPreDefined();

                Log.e("PreCheck", pre);
                if (!pre.isEmpty() && pre != null && !pre.equals("0")) {
                    String[] split = pre.split(";");
                    if (!split[1].isEmpty() && split[1].equals("3")) { //Type = Pitch Changing Msg
                        String pitchs = split[2];
                        final float pitch_s = Float.parseFloat(pitchs);


                        imgview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                notifyDataSetChanged();

                                if(!isplaying){
                                    imgview.setImageResource(R.drawable.play);
                                    final int i = sp.load(pathss, 0);
                                    sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                                        @Override
                                        public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                                            streamid = sp.play(i, 1, 1, 0, 0, pitch_s);
                                        }
                                    });
                                    isplaying = true;
                                } else {
                                    imgview.setImageResource(R.drawable.stop);
                                    sp.stop(streamid);
                                    final int i = sp.load(pathss, 0);
                                    isplaying = false;
                                }
                            }
                        });
                    }
                } else {//normal pitch
                    imgview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notifyDataSetChanged();

                            if(!isplaying){
//                                imgview.setPressed(true);
                                imgview.setImageResource(R.drawable.stop);
                                final int i = sp.load(pathss, 0);
                                streamid = sp.play(i, 1, 1, 0, 0, 1);

                                isplaying = true;
                            } else {
                                sp.stop(streamid);
                                sp.release();
                                imgview.setImageResource(R.drawable.play);
                                isplaying = false;
                            }
                        }
                    });
                }
                return rowView2;


            } else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                ViewHolderFile holderFile = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderFile = new ViewHolderFile(convertView);
                convertView.setTag(holderFile);


                MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                View rowView3 = inflater.inflate(R.layout.sender_attachment_bubble, parent, false);
//                chl_tv1 = (TextView) rowView3.findViewById(R.id.sender_nickname);
                final ImageView img_send_file = (ImageView) rowView3.findViewById(R.id.file_msg_state);
                TextView dummys3 = (TextView) rowView3.findViewById(R.id.dummy3);
                String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                String msgstatus = "";
                String read = "";
                if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                    Utils.getMsgReadStatus(context, id, msgid, msgstatus, "", dummys3);
                    Log.e("File Read Status Return", dummys.getText().toString());
                    if (dummys.getText().toString().equals("unread")) {
                        //Unread, change into read status in SQLite DB
                        Utils.ChangeMsgStatus(context, id, msgid, read, "");
                        img_send_file.setImageResource(R.drawable.received);


                    } else if (dummys.getText().toString().equals("read")) {
                        //set Two Tick (All read)
                        img_send_file.setImageResource(R.drawable.read);

                    } else {
                        img_send_file.setImageResource(R.drawable.received);

                    }
                } // Message successfully sent
                else {
                    if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SENDING) { //Sending
                        img_send_file.setImageResource(R.drawable.sending);
                    }
                }

                chatService.observeMsgListener(new ReceiveMsgListener() {
                    @Override
                    public void receive(ChatMsg chatMsg) {
                    }

                    @Override
                    public void update(ChatMsg chatMsg) {
                        if (chatMsgList.get(position).getMsgStatus() != chatMsgList.get(position).STATUS_SENDING) {
                            img_send_file.setImageResource(R.drawable.received);
                        } else if (chatMsgList.get(position).getMsgStatus() == chatMsgList.get(position).STATUS_SEND_FAILURE) {
                        }
                    }
                }, true);
//                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", chl_tv1);
                //chl_tv1.setText(Sendernames); //UserID;

                TextView sender_attachment_name = (TextView) rowView3.findViewById(R.id.sender_attachment_name);
                TextView sender_attachment_size = (TextView) rowView3.findViewById(R.id.sender_attachment_size);
                final TextView sender_timestamp = (TextView) rowView3.findViewById(R.id.sender_timestamp);
                ImageButton sender_attachment_download = (ImageButton) rowView3.findViewById(R.id.sender_attachment_download);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                sender_timestamp.setText(format.toString());

                String size = String.valueOf(msgFile.getSize());
                sender_attachment_name.setText(msgFile.getFileName());
                int sizes = Integer.valueOf(size);
                int actual_size = (sizes / 1024);
                sender_attachment_size.setText(actual_size + " KB");

                final ProgressBar progressbar2 = (ProgressBar) rowView3.findViewById(R.id.progress_chat_send);
                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown == false) {
                        Toast.makeText(context, "Burn Chat !", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }
                }
                if (convertView == null) {
                    sender_attachment_download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                            String localPath = msgFile.getLocalPath();
                            String downPath = msgFile.getDownloadPath();
                            String key = msgFile.getEncryptKey();//key for decryptio
                            String path;

                            if (!TextUtils.isEmpty(localPath)) {
                                path = localPath;

                                Log.e("Attachment Load Success", path);
                                File fileIn = new File(path);
                                Uri u = Uri.fromFile(fileIn);

                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                                String type = mime.getMimeTypeFromExtension(ext);

                                intent.setDataAndType(Uri.fromFile(fileIn), type);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } else {
                                path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);

                                ClientManager.getDefault().getFileService().observeProgressListener(new ProgressListener() {
                                    @Override
                                    public void progress(long l, int i, String s) {
                                        progressbar2.setVisibility(View.VISIBLE);

                                        boolean onetime = false;
                                        if (onetime = false) {
                                            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
                                            onetime = true;
                                        }
                                        int progress = i;
                                        if (progress == 100) {
                                            boolean onetimes = false;
                                            if (onetimes = false) {
                                                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show();
                                                progressbar2.setVisibility(GONE);
                                                onetimes = true;
                                            }
                                        }
                                    }
                                }, true);
                                File fileIn = new File(path);
                                Uri u = Uri.fromFile(fileIn);

                                Log.e("Attachment Load Success", path);
                                fileIn = new File(path);
                                u = Uri.fromFile(fileIn);

                                Intent intent = new Intent();
                                intent.setAction(android.content.Intent.ACTION_VIEW);
                                MimeTypeMap mime = MimeTypeMap.getSingleton();
                                String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                                String type = mime.getMimeTypeFromExtension(ext);

                                intent.setDataAndType(Uri.fromFile(fileIn), type);

                                context.startActivity(intent);
                                try {
                                    context.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                }//end of if
                else {
                    holderFile = (ViewHolderFile) convertView.getTag();
                }
                String downPath = msgFile.getDownloadPath();
                String localPath = msgFile.getLocalPath();
                final String actual;
                String key = msgFile.getEncryptKey();//key for decryptio
                holderFile.sender_attachment_name = (TextView) rowView3.findViewById(R.id.sender_attachment_name);
                holderFile.sender_attachment_size = (TextView) rowView3.findViewById(R.id.sender_attachment_size);
                holderFile.sender_timestamp = (TextView) rowView3.findViewById(R.id.sender_timestamp);
                holderFile.sender_attachment_download = (ImageButton) rowView3.findViewById(R.id.sender_attachment_download);
                final String filepath = ClientManager.getDefault().getFileService().decryptFile(key, downPath);

                File f = new File(localPath);
                if (f.exists()) {
                    actual = localPath;
                    Log.e("File Path L ", actual);
                } else {
                    actual = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                    Log.e("File Path ", actual);
                }
                sender_attachment_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File fileIn = new File(actual);
                        Uri u = Uri.fromFile(fileIn);
                        fileIn = new File(actual);
                        u = Uri.fromFile(fileIn);

                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(fileIn), type);
                        context.startActivity(intent);
                    }
                });

                return rowView3;
            }

            return rowView;

        }
//================================================================RECEIVER================================================================
        else {
            View rowView8 = inflater.inflate(R.layout.receiver_chat_bubble, parent, false);

            TextView timestamp = (TextView) rowView8.findViewById(R.id.receiver_timestamp);
            ImageView imgs = (ImageView) rowView8.findViewById(R.id.image_received);

            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();


            final ImageView avatar = (ImageView) rowView8.findViewById(R.id.avatar_onclick); //load avatar
//            String avatarURL = chatMsgList.get(position).getAvatar();
//            Log.e("Avatar URLs ",avatarURL);
//            Utils.loadHead(context, avatarURL, avatar, R.mipmap.vim_icon_default_user);

            //If Text Message Detected
            if (ChatMsgApi.TYPE_TEXT == chatMsgList.get(position).getMsgType()) {
                boolean shown = false;
                View rowView1 = inflater.inflate(R.layout.receiver_chat_bubble, parent, false);


                Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
                int item_count = chat.getUnreadCount();

                if(item_count>0){

                    chatService.setMsgRead(chatMsgList.get(position).getTargetID(), chatMsgList.get(position).getMsgID());

                    String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                    String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                    String msgstatus = "AR";
                    String read = "";


                    Utils.ChangeMsgStatus(context, id, msgid, read, "");
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
                } else {
                    //Read

                }


                if (chatMsgList.get(position).isBurn()) {
                    if (shown != false) {
//                        Toast.makeText(context, "Burn Chat Received", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }
                }
                String pre = chatMsgList.get(position).getPreDefined();
                String[] split = pre.split(";");
                Log.e("Pre Type0 ", pre);
                if (!pre.equals("") && split[1].equals("11")) {
                    Log.e("Pre Type1 ", split[1]);
                    View replyview = inflater.inflate(R.layout.receiver_reply_lyout, parent, false);
                    TextView timestaptss = (TextView) replyview.findViewById(R.id.receiver_timestamp);
                    final TextView replycontent = (TextView) replyview.findViewById(R.id.reply_content);
                    TextView senderreply = (TextView) replyview.findViewById(R.id.receiver_reply_body);
                    final TextView dummy = (TextView) replyview.findViewById(R.id.dummy);
                    TextView backgroundfield = (TextView) replyview.findViewById(R.id.background);
                    ImageView img_read = (ImageView) replyview.findViewById(R.id.text_msg_state);
                    final ImageView avatar_onclick_reply = (ImageView) replyview.findViewById(R.id.avatar_onclick_reply);
                    final TextView Origin_Timestamp = (TextView) replyview.findViewById(R.id.Origin_Timestamp);

                    final long reply_tgt_id = chatMsgList.get(position).getID();
                    final long reply_msg_id = Long.parseLong(split[0]);

//                    timestaptss.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                    senderreply.setText(chatMsgList.get(position).getBody());
                    String avatarURL = chatMsgList.get(position).getAvatar();

                    Utils.loadHead(context, avatarURL, avatar_onclick_reply, R.mipmap.vim_icon_default_user);


                    backgroundfield.setOnClickListener(new View.OnClickListener() {
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
                                    Intent intents = new Intent(context, ChatActivity.class);
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
                                Log.e("Redirect ", chatService.indexByID(chatMsgList.get(position).getFromID()) + "");
                            }

                        }
                    });
                    ClientManager.getDefault().getChatService().getMessages(reply_tgt_id, reply_msg_id, 1, 0, new ResultCallBack<Long, List<ChatMsg>, Void>() {
                        @Override
                        public void onSuccess(Long aLong, final List<ChatMsg> chatMsgs, Void aVoid) {
                            String reply_body = chatMsgs.get(0).getBody();
                            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
                            Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", dummy);
                            String name = dummy.getText().toString();

                            switch (chatMsgs.get(0).getMsgType()) {
                                case ChatMsgApi.TYPE_TEXT:
                                    reply_body = chatMsgs.get(0).getBody();
                                    replycontent.setText(name + " : " + reply_body);
                                    break;
                                case ChatMsgApi.TYPE_IMAGE:
                                    replycontent.setText(name + " : [IMAGE]");
                                    break;
                                case ChatMsgApi.TYPE_MINI_VIDEO:
                                    replycontent.setText(name + " : [VIDEO]");
                                    break;
                                case ChatMsgApi.TYPE_AUDIO:
                                    replycontent.setText(name + " : [AUDIO]");
                                    break;
                                case ChatMsgApi.TYPE_FILE:
                                    replycontent.setText(name + " : [FILE]");
                                    break;
                            }

                            RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                                @Override
                                public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                                    String avatar_Url = contact.getAvatar();
                                    Log.e("Avatar URL ", contact.getAvatar());
                                    Utils.loadHead(context, avatar_Url, avatar_onclick_reply, R.mipmap.vim_icon_default_user);
                                }
                            });
                            Origin_Timestamp.setText(DateTimeUtils.formatTime(context, chatMsgs.get(0).getTime(), true));

                            avatar_onclick_reply.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, Chat_Profile.class);
                                    intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);

                                }
                            });
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                    return replyview;
                }

                ViewHolderText holderText = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderText = new ViewHolderText(convertView);

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat, parent, false);
                } else {
                    holderText = (ViewHolderText) convertView.getTag();
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                TextView chl_tv1 = (TextView) rowView8.findViewById(R.id.receiver_nickname);
                final TextView chl_tv2 = (TextView) rowView8.findViewById(R.id.receiver_message_text);
                timestamp = (TextView) rowView8.findViewById(R.id.receiver_timestamp);
                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", chl_tv1);
//-----------------------------BEGINNING OF TORNADO FUNCTION--------------------------

                final Random rand = new Random();
                final int min = chatMsgList.size() - chatMsgList.size() + 1;
                final int max = chatMsgList.size();
                String pres = chatMsgList.get(position).getPreDefined();
                Log.e("Related Received ", chatMsgList.get(position).getRelatedUsers() + "");
                Log.e("Pre Received ", pres);
                String[] splits = pre.split(";");

                if (!pre.equals("") && !split[1].isEmpty()) {
                    Log.e("Pre Type ", split[1]);

                    for (int i = 0; i < chatMsgList.get(position).getRelatedUsers().size(); i++) {
                        Long exp = chatMsgList.get(position).getRelatedUsers().get(i);
                        if (split[1].equals("4") && RequestHelper.isMyself(exp)) {
                            Toast.makeText(context, "Tordano !", Toast.LENGTH_SHORT).show();
                            new CountDownTimer(5000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long time = millisUntilFinished / 1000;
                                    pos = rand.nextInt(max);
                                    chl_tv2.setText(chatMsgList.get(pos).getBody());
                                }

                                @Override
                                public void onFinish() {
                                    Log.e("Procesed Pre ", chatMsgList.get(position).getPreDefined());

                                    chatMsgList.get(position).setPreDefined("name;99;5");
                                    RequestHelper.updateMsg(chatMsgList.get(position), new RequestCallBack() {
                                        @Override
                                        public void handleSuccess(Object o, Object o2, Object o3) {
                                            //Changed Successfully
                                            Log.e("Reset Pre ", chatMsgList.get(position).getPreDefined());
                                            chl_tv2.setText(chatMsgList.get(position).getBody());
                                            Toast.makeText(context, "Looks like tornado had gone !", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            }.start();
                        } else {
                            chl_tv2.setText(chatMsgList.get(position).getBody());
                        }
                    }
                }
//-----------------------------END OF TORNADO FUNCTION--------------------------

                chl_tv2.setText(chatMsgList.get(position).getBody());
                timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));

                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        String avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                        Utils.loadHead(context, avatar_Url, avatar, R.mipmap.vim_icon_default_user);
                    }
                });


                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                    }
                });
            }

            //If Image Detected
            else if (ChatMsgApi.TYPE_IMAGE == chatMsgList.get(position).getMsgType()) {
                ViewHolderImg holderImgs = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderImgs = new ViewHolderImg(convertView);
                convertView.setTag(holderImgs);
                MsgImg msgImg = (MsgImg) chatMsgList.get(position);
                String key = msgImg.getEncryptKey();//key for decryption
                String localPath = msgImg.getMainLocalPath();
                String downPath = msgImg.getMainDownloadPath();
                final String path;

//                Toast.makeText(context, "Image Count " + chatService.getUnreadMsgCountByType(ChatMsgApi.TYPE_IMAGE) , Toast.LENGTH_SHORT).show();
                Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
                int item_count = chat.getUnreadCount();

                if(item_count>0){
                    chatService.setMsgRead(chatMsgList.get(position).getTargetID(), chatMsgList.get(position).getMsgID());

                    String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                    String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                    String msgstatus = "AR";
                    String read = "";


                    Utils.ChangeMsgStatus(context, id, msgid, read, "");
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
                } else {
                    //Read

                }
                View rowView1 = inflater.inflate(R.layout.receiver_chat_bubble, parent, false);
                final ImageView avatars = (ImageView) rowView1.findViewById(R.id.avatar_onclick); //load avatar
                final ProgressBar progressbar = (ProgressBar) rowView1.findViewById(R.id.progress_chat_receiver);
                final TextView ch2 = (TextView) rowView1.findViewById(R.id.receiver_message_text);
                ch2.setVisibility(View.INVISIBLE);

                String avatarURL = chatMsgList.get(position).getAvatar();
                Utils.loadHead(context, avatarURL, avatars, R.mipmap.vim_icon_default_user);


                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown != false) {
//                        Toast.makeText(context, "Burn Chat Received", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }
                }

                //download Image
                chatService.downloadFile(msgImg, (byte) 2, new ResultCallBack<String, Long, Void>() {
                    @Override
                    public void onSuccess(String s, Long aLong, Void aVoid) {
                        chatService.observeProgressListener(new ProgressListener() {
                            @Override
                            public void progress(long l, int i, String s) {
                                if (i < 100) {
                                    progressbar.setVisibility(View.VISIBLE);
                                    ch2.setText("Loading Image...");
                                } else {
                                    progressbar.setVisibility(GONE);
                                    ch2.setText("");
                                }
                            }
                        }, true);
                        Log.e("ImgDownload", "Complete");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("ImgDownload", "Fail");
                    }
                }, new ResultCallBack<Integer, Integer, String>() {
                    @Override
                    public void onSuccess(Integer integer, Integer integer2, String s) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.sender_video_bubble, parent, false);
                }//end of if
                else {
                    holderImgs = (ViewHolderImg) convertView.getTag();
                }

                holderImgs.chl_tv1 = (TextView) rowView1.findViewById(R.id.receiver_nickname);
                holderImgs.chl_tv2 = (TextView) rowView1.findViewById(R.id.receiver_message_text);
                holderImgs.timestamp = (TextView) rowView1.findViewById(R.id.receiver_timestamp);
                holderImgs.img = (ImageView) rowView1.findViewById(R.id.image_received);
                holderImgs.timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", holderImgs.chl_tv1);

                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        String avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                        Utils.loadHead(context, avatar_Url, avatars, R.mipmap.vim_icon_default_user);
                    }
                });

                avatars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });

                String paths;
                if (!TextUtils.isEmpty(localPath)) {
                    paths = localPath;

                } else {
                    paths = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                }

                Picasso.with(getContext())
                        .load(("file:///" + paths))
                        .resize(500, 500)
                        .centerCrop()
                        .into(holderImgs.img);
                final File file = new File(paths);

                holderImgs.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(file), type);
                        context.startActivity(intent);

                    }
                });
                return rowView1;
            }

            //If Video Detected
            else if (ChatMsgApi.TYPE_MINI_VIDEO == chatMsgList.get(position).getMsgType()) {
                ViewHolderMiniVideo holderMiniVideo = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderMiniVideo = new ViewHolderMiniVideo(convertView);
                convertView.setTag(holderMiniVideo);

                MsgMiniVideo msgVideos = (MsgMiniVideo) chatMsgList.get(position);
                View rowView1 = inflater.inflate(R.layout.receiver_video_bubble, parent, false);
                TextView receiver_video_id = (TextView) rowView1.findViewById(R.id.receiver_nickname);
                String urls = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
                Utils.setNickname(context2, urls, chatMsgList.get(position).getFromID() + "", receiver_video_id);
                TextView timestamps = (TextView) rowView1.findViewById(R.id.receiver_timestamp);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                timestamps.setText(format.toString());
                ImageView videos = (ImageView) rowView1.findViewById(R.id.video_player_receiver);

                String key = msgVideos.getEncryptKey();//key for decryption
                String localPath = msgVideos.getLocalVideoPath();
                String downPath = msgVideos.getVideoDownloadPath();
                String pre = msgVideos.getPreImgUrl();
                final String path;
                final ProgressBar progress_chat_receive = (ProgressBar) rowView1.findViewById(R.id.progress_chat_receive);
                final ImageView avatars = (ImageView) rowView1.findViewById(R.id.avatar_onclick_video); //load avatar
                String avatarURL = chatMsgList.get(position).getAvatar();
                Utils.loadHead(context, avatarURL, avatars, R.mipmap.vim_icon_default_user);
                avatars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });
//                Toast.makeText(context, "Video Count " + chatService.getUnreadMsgCountByType(ChatMsgApi.TYPE_IMAGE) , Toast.LENGTH_SHORT).show();
                Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
                int item_count = chat.getUnreadCount();

                if(item_count>0){
                    chatService.setMsgRead(chatMsgList.get(position).getTargetID(), chatMsgList.get(position).getMsgID());

                    String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                    String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                    String msgstatus = "AR";
                    String read = "";


                    Utils.ChangeMsgStatus(context, id, msgid, read, "");
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
                } else {
                    //Read

                }
                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown = false)
//                        Toast.makeText(context, "Burn Chat Received", Toast.LENGTH_SHORT).show();
                        shown = true;
                }
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.receiver_video_bubble, parent, false);

                } else {
                    holderMiniVideo = (ViewHolderMiniVideo) convertView.getTag();
                }

                RequestHelper.downloadImage(msgVideos.getID(), pre, new RequestCallBack<String, Long, Void>() {
                    @Override
                    public void handleSuccess(String s, Long aLong, Void aVoid) {
                        chatService.observeProgressListener(new ProgressListener() {
                            @Override
                            public void progress(long l, int i, String s) {
                                if (i < 100) {
                                    progress_chat_receive.setVisibility(View.VISIBLE);
                                } else {
                                    progress_chat_receive.setVisibility(GONE);
                                }
                            }
                        }, true);
                    }
                }, new RequestCallBack<Integer, Integer, String>() {
                    @Override
                    public void handleSuccess(Integer integer, Integer integer2, String s) {

                    }
                });


                chatService.downloadFile(msgVideos, (byte) 2, new ResultCallBack<String, Long, Void>() {
                    @Override
                    public void onSuccess(String s, Long aLong, Void aVoid) {
                        chatService.observeProgressListener(new ProgressListener() {
                            @Override
                            public void progress(long l, int i, String s) {
                                if (i < 100) {
                                    progress_chat_receive.setVisibility(View.VISIBLE);
                                } else {
                                    progress_chat_receive.setVisibility(GONE);
                                }
                            }
                        }, true);
                        Log.e("Video Download", "Successful");
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                }, new RequestCallBack<Integer, Integer, String>() {
                    @Override
                    public void handleSuccess(Integer integer, Integer integer2, String s) {

                    }
                });
                if (!TextUtils.isEmpty(localPath)) {
                    path = localPath;
                } else {
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                }
                TextView sender_video_id = (TextView) rowView1.findViewById(R.id.receiver_nickname);
                String pathsss = msgVideos.getPreImgDownloadPath();
                String thumb = ClientManager.getDefault().getFileService().decryptFile(key, pathsss);

                timestamp.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));


                final File file = new File(path);

                Picasso.with(getContext())
                        .load(("file:///" + thumb))
                        .resize(500, 500)
                        .centerCrop()
                        .into(videos);

                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        String avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                        Utils.loadHead(context, avatar_Url, avatars, R.mipmap.vim_icon_default_user);
                    }
                });

                avatars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });
                videos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyDataSetChanged();
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(file), type);
                        context.startActivity(intent);

                    }
                });
                return rowView1;


            }


            //If Audio Detected
            else if (ChatMsgApi.TYPE_AUDIO == chatMsgList.get(position).getMsgType()) {

                MsgAudio msgAudio = (MsgAudio) chatMsgList.get(position);
                audiopath = msgAudio.getLocalPath();

                Log.e("Audio Path", audiopath);
                final long time = msgAudio.getMediaTime();
                touchStart = System.currentTimeMillis(); //Starting time
                count = (int) time;
//                Toast.makeText(context, "Audio Count " + cf(ChatMsgApi.TYPE_IMAGE) , Toast.LENGTH_SHORT).show();
                Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
                int item_count = chat.getUnreadCount();

                if(item_count>0){
                    chatService.setMsgRead(chatMsgList.get(position).getTargetID(), chatMsgList.get(position).getMsgID());

                    String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                    String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                    String msgstatus = "AR";
                    String read = "";


                    Utils.ChangeMsgStatus(context, id, msgid, read, "");
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
                } else {
                    //Read

                }

                View rowView2 = inflater.inflate(R.layout.receiver_audio_bubble, parent, false);
                final ImageButton imgview = (ImageButton) rowView2.findViewById(R.id.receiver_play);
                final TextView timestampaudio = (TextView) rowView2.findViewById(R.id.receiver_timestamp);
                TextView receiver_audio_id = (TextView) rowView2.findViewById(R.id.receiver_nickname);
                final ImageView avatarss = (ImageView) rowView2.findViewById(R.id.avatar_onclick_audio);
                String avatarURL = chatMsgList.get(position).getAvatar();
                Utils.loadHead(context, avatarURL, avatarss, R.mipmap.vim_icon_default_user);

                timestampaudio.setText(DateTimeUtils.formatTime(context, chatMsgList.get(position).getTime(), true));
                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", receiver_audio_id);


                //  receiver_audio_id.setText(Receivernames); //UserID

                String key = msgAudio.getEncryptKey();//key for decryption
                final String localPath = msgAudio.getLocalPath();
                final String downPath = msgAudio.getDownloadPath();
                final String path;
                final ProgressBar progressbar1 = (ProgressBar) rowView2.findViewById(R.id.progress_chat_send);

                if (chatMsgList.get(position).isBurn()) {
//                    Toast.makeText(context, "Burn Chat Received", Toast.LENGTH_SHORT).show();
                }
                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        String avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                        Utils.loadHead(context, avatar_Url, avatarss, R.mipmap.vim_icon_default_user);
                    }
                });
                avatarss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });

                chatService.downloadFile(msgAudio, (byte) 2, new ResultCallBack<String, Long, Void>() {
                    @Override
                    public void onSuccess(String s, Long aLong, Void aVoid) {

                        chatService.observeProgressListener(new ProgressListener() {
                            @Override
                            public void progress(long l, int i, String s) {
                                if (i < 100) {
                                    progressbar1.setVisibility(View.VISIBLE);
                                } else {

                                    progressbar1.setVisibility(GONE);

                                }
                            }
                        }, true);
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
                imgview.setImageResource(R.drawable.play);
                File f = new File(localPath);
                boolean once = false;
                if (!TextUtils.isEmpty(localPath) && f.exists()) {
//                    path = localPath;
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                    if (!once) {

                        once = true;
                    } else {

                    }

                } else {
                    path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);

                    imgview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (path != null) ;
                            {
                                final SoundPool sp;
                                sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

                                String pre = chatMsgList.get(position).getPreDefined();

                                if (!pre.isEmpty() && pre != null) {
                                    String[] split = pre.split(";");

                                    if (!split[1].isEmpty() && split[1].equals("3")) { //Type = Pitch Changing Msg
                                        String pitchs = split[2];
                                        final float pitch_s = Float.parseFloat(pitchs);

                                        imgview.setImageResource(R.drawable.play);
                                        imgview.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                notifyDataSetChanged();
                                                if(!isplaying){
                                                    imgview.setImageResource(R.drawable.stop);
                                                    final int i = sp.load(path, 0);
                                                    sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                                                        @Override
                                                        public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                                                            streamid = sp.play(i, 1, 1, 0, 0, pitch_s);
                                                        }
                                                    });
                                                    isplaying = true;
                                                } else {
                                                    imgview.setImageResource(R.drawable.play);
                                                    sp.stop(streamid);
                                                    final int i = sp.load(path, 0);
                                                    streamid = sp.play(i, 1, 1, 0, 0, pitch_s);
                                                    isplaying = false;

                                                }

                                            }
                                        });
                                    }
                                } else {//normal pitch
                                    imgview.setImageResource(R.drawable.play);
                                    imgview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            notifyDataSetChanged();
                                            if(!isplaying){
                                                imgview.setImageResource(R.drawable.stop);
                                                final int i = sp.load(path, 0);
                                                sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                                                    @Override
                                                    public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                                                        streamid = sp.play(i, 1, 1, 0, 0, 1);
                                                    }
                                                });
                                                isplaying = true;
                                            } else {
                                                imgview.setImageResource(R.drawable.play);
                                                sp.stop(streamid);
                                                final int i = sp.load(path, 0);
                                                streamid = sp.play(i, 1, 1, 0, 0, 1);
                                                isplaying = false;

                                            }
                                        }
                                    });
                                }

                            }

                        }
                    });

                }
                return rowView2;


            } else if (ChatMsgApi.TYPE_FILE == chatMsgList.get(position).getMsgType()) {
                ViewHolderFile holderFile = null;
                convertView = LayoutInflater.from(context).inflate(R.layout.chat, null);
                holderFile = new ViewHolderFile(convertView);
                convertView.setTag(holderFile);

                MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                View rowView3 = inflater.inflate(R.layout.receiver_attachment_bubble, parent, false);
                TextView chl_tv1 = (TextView) rowView3.findViewById(R.id.receiver_nickname);
//                Toast.makeText(context, "File Count " + chatService.getUnreadMsgCountByType(ChatMsgApi.TYPE_IMAGE) , Toast.LENGTH_SHORT).show();
                Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatMsgList.get(position).getID());
                int item_count = chat.getUnreadCount();

                if(item_count>0){
                    chatService.setMsgRead(chatMsgList.get(position).getTargetID(), chatMsgList.get(position).getMsgID());

                    String id = String.valueOf(RequestHelper.getAccountInfo().getID());
                    String msgid = String.valueOf(chatMsgList.get(position).getMsgID());
                    String msgstatus = "AR";
                    String read = "";


                    Utils.ChangeMsgStatus(context, id, msgid, read, "");
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
                } else {
                    //Read

                }
                Utils.setNickname(context2, url, chatMsgList.get(position).getFromID() + "", chl_tv1);

                //chl_tv1.setText(Receivernames); //UserID;

                TextView receiver_attachment_name = (TextView) rowView3.findViewById(R.id.receiver_attachment_name);
                TextView receiver_attachment_size = (TextView) rowView3.findViewById(R.id.receiver_attachment_size);
                final TextView receiver_timestamp = (TextView) rowView3.findViewById(R.id.receiver_timestamp);
                ImageButton receiver_attachment_download = (ImageButton) rowView3.findViewById(R.id.receiver_attachment_download);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a dd-MM-yyyy");
                String format = simpleDateFormat.format(chatMsgList.get(position).getTime());
                receiver_timestamp.setText(format.toString());

                final ProgressBar progressbar = (ProgressBar) rowView3.findViewById(R.id.progress_chat_send);

                final ImageView avatarssss = (ImageView) rowView3.findViewById(R.id.avatar_onclick_file); //load avatar
                String avatarURL = chatMsgList.get(position).getAvatar();
                Utils.loadHead(context, avatarURL, avatarssss, R.mipmap.vim_icon_default_user);
                avatarssss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });
                String size = String.valueOf(msgFile.getSize());
                receiver_attachment_name.setText(msgFile.getFileName());
                int sizes = Integer.valueOf(size);
                int actual_size = (sizes / 1024);
                receiver_attachment_size.setText(actual_size + " KB");

                boolean shown = false;
                if (chatMsgList.get(position).isBurn()) {
                    if (shown == false) {
//                        Toast.makeText(context, "Burn Chat Received", Toast.LENGTH_SHORT).show();
                        shown = true;
                    }

                }
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.sender_audio_bubble, parent, false);

                } else {
                    holderFile = (ViewHolderFile) convertView.getTag();
                }
                String downPath = msgFile.getDownloadPath();
                String key = msgFile.getEncryptKey();//key for decryptio
                holderFile.receiver_attachment_name = (TextView) rowView3.findViewById(R.id.receiver_attachment_name);
                holderFile.receiver_attachment_size = (TextView) rowView3.findViewById(R.id.receiver_attachment_size);
                holderFile.receiver_timestamp = (TextView) rowView3.findViewById(R.id.receiver_timestamp);
                holderFile.receiver_attachment_download = (ImageButton) rowView3.findViewById(R.id.receiver_attachment_download);
                final String filepath = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });

                receiver_attachment_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File fileIn = new File(filepath);
                        Uri u = Uri.fromFile(fileIn);
                        fileIn = new File(filepath);
                        u = Uri.fromFile(fileIn);

                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                        String type = mime.getMimeTypeFromExtension(ext);
                        intent.setDataAndType(Uri.fromFile(fileIn), type);
                        context.startActivity(intent);
                    }
                });
                RequestHelper.getUserInfo(chatMsgList.get(position).getFromID(), new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        String avatar_Url = contact.getAvatar();
                        Log.e("Avatar URL ", contact.getAvatar());
                        Utils.loadHead(context, avatar_Url, avatarssss, R.mipmap.vim_icon_default_user);
                    }
                });

                avatarssss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, Chat_Profile.class);
                        intent.putExtra("othersideid", chatMsgList.get(position).getFromID());
                        context.startActivity(intent);
                    }
                });
                receiver_attachment_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MsgFile msgFile = (MsgFile) chatMsgList.get(position);
                        String localPath = msgFile.getLocalPath();
                        String downPath = msgFile.getDownloadPath();
                        String key = msgFile.getEncryptKey();//key for decryptio
                        String path;
                        chatService.downloadFile(msgFile, (byte) 2, new ResultCallBack<String, Long, Void>() {
                            @Override
                            public void onSuccess(String s, Long aLong, Void aVoid) {
                                chatService.observeProgressListener(new ProgressListener() {
                                    @Override
                                    public void progress(long l, int i, String s) {
                                        if (i < 100) {
                                            progressbar.setVisibility(View.VISIBLE);
                                        } else {
                                            progressbar.setVisibility(GONE);
                                        }
                                    }
                                }, true);
                                Log.e("FileDownload", "Complete");
                            }

                            @Override
                            public void onError(int i, String s) {
                                Log.e("FileDownload", "Fail");
                            }
                        }, new ResultCallBack<Integer, Integer, String>() {
                            @Override
                            public void onSuccess(Integer integer, Integer integer2, String s) {

                            }

                            @Override
                            public void onError(int i, String s) {

                            }
                        });
                        File f = new File(localPath);
                        if (!TextUtils.isEmpty(localPath) && f.exists()) {

                            path = localPath;
//                            path = ClientManager.getDefault().getFileService().decryptFile(key, localPath);


                            Log.e("Attachment Load Success", path);
                            File fileIn = new File(path);
                            Uri u = Uri.fromFile(fileIn);

                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);

                            intent.setDataAndType(Uri.fromFile(fileIn), type);

                            context.startActivity(intent);


                        } else {
                            path = ClientManager.getDefault().getFileService().decryptFile(key, downPath);
                            ClientManager.getDefault().getFileService().observeProgressListener(new ProgressListener() {
                                @Override
                                public void progress(long l, int i, String s) {
                                    progressbar.setVisibility(View.VISIBLE);

                                    progressbar.setVisibility(View.VISIBLE);
                                    int progress = i;
                                    if (progress == 100) {
                                        progressbar.setVisibility(GONE);
                                    }
                                }
                            }, true);
                            File fileIn = new File(path);
                            Uri u = Uri.fromFile(fileIn);

                            Log.e("Attachment Load Success", path);
                            fileIn = new File(path);
                            u = Uri.fromFile(fileIn);

                            Intent intent = new Intent();
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = fileIn.getName().substring(fileIn.getName().indexOf(".") + 1);
                            String type = mime.getMimeTypeFromExtension(ext);

                            intent.setDataAndType(Uri.fromFile(fileIn), type);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            try {
                                context.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                return rowView3;


            }

//            else
//                if(chatMsgList.get(position).getMsgType() == 10){
//                View rowView34 = inflater.inflate(R.layout.null_activity, parent, false);
//                return rowView34;
//            }


            return rowView8;

//            if(ChatMsgApi.TYPE_CUSTOM_DYNAMIC)
        }
    }
}





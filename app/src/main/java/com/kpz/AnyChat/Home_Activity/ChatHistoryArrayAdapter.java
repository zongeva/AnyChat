package com.kpz.AnyChat.Home_Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kpz.AnyChat.Others.DateTimeUtils;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ChatService;
import com.vrv.imsdk.model.Contact;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created by wenrong on 14/7/2017.
 */

public class ChatHistoryArrayAdapter extends ArrayAdapter<Chat> {
    private final Context context;
    private final List<Chat> chatHistorys;
    String Avatar_Url;
    String targetIDs;
    Chat chat;
    int item_count;
    final ChatService chatService = ClientManager.getDefault().getChatService();

    public ChatHistoryArrayAdapter(Context context, List<Chat> chatHistorys) {
        super(context, R.layout.chathistory_list, chatHistorys);
        this.context = context;
        this.chatHistorys = chatHistorys;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View rowView = inflater.inflate(R.layout.chathistory_list, parent, false);
        TextView chl_tv1 = (TextView) rowView.findViewById(R.id.chl_tv1);
        final TextView chl_tv2 = (TextView) rowView.findViewById(R.id.chl_tv2);
        ImageView avatar = (ImageView) rowView.findViewById(R.id.chatlist_avatar);
//        RelativeTimeTextView v = (RelativeTimeTextView)rowView.findViewById(R.id.timestamp);
        TextView chatlist_timestamp = (TextView)rowView.findViewById(R.id.chatlist_timestamp);
        TextView counts = (TextView)rowView.findViewById(R.id.textView76);

        //Identify Last Msg Type
        long targetID = chatHistorys.get(position).getID();
        long msgID = chatHistorys.get(position).getLastAtMsgID();
        int count = 1;
        int flag = 0;
        Contact entity = null;
        Chat chat = ClientManager.getDefault().getChatService().findItemByID(chatHistorys.get(position).getID());

//        v.setReferenceTime(chatHistorys.get(position).getMsgTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a yyyy-MM-dd");
            String msgtime = simpleDateFormat.format(chatHistorys.get(position).getMsgTime());
            chatlist_timestamp.setText(msgtime.toString());

            if (chat != null ){
                item_count = chat.getUnreadCount();
                if (item_count>0) {
                    counts.setVisibility(View.VISIBLE);
                    counts.setText(item_count + "");
                } else if (item_count>99){
                    counts.setVisibility(View.VISIBLE);
                    counts.setText("99+");
                }
            } else {
                chat = ClientManager.getDefault().getChatService().findItemByID(chatHistorys.get(position).getID());


            }


                if (ChatMsgApi.isGroup(targetID)) {

            String groupname = RequestHelper.getGroupInfo(targetID).getName();
            Avatar_Url = RequestHelper.getGroupInfo(targetID).getAvatar();
            chl_tv1.setText("[Group] " + groupname);

                    simpleDateFormat = new SimpleDateFormat("hh:mm a yyyy-MM-dd");
                    msgtime = simpleDateFormat.format(chatHistorys.get(position).getMsgTime());
//                    chatlist_timestamp.setText(msgtime.toString());
                    chatlist_timestamp.setText(DateTimeUtils.formatTime(context, chatHistorys.get(position).getMsgTime(), true).replace("am","AM").replace("pm","PM"));


            if (Avatar_Url != null) {
                Utils.loadHead(context, Avatar_Url, avatar, R.mipmap.vim_icon_default_group);
            }
        } else if (ChatMsgApi.isUser(targetID)) {

            Avatar_Url = chatHistorys.get(position).getAvatar();

            chl_tv1.setText(chatHistorys.get(position).getID() + "");
            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatHistorys.get(position).getID();
//            Utils.setNickname(context, url, chatHistorys.get(position).getID() + "", chl_tv1);
            Utils.loadHead(context, Avatar_Url, avatar, R.mipmap.vim_icon_default_user);
                    simpleDateFormat = new SimpleDateFormat("hh:mm a yyyy-MM-dd");
                    msgtime = simpleDateFormat.format(chatHistorys.get(position).getMsgTime());
                    chatlist_timestamp.setText(DateTimeUtils.formatTime(context, chatHistorys.get(position).getMsgTime(), true).replace("am","AM").replace("pm","PM"));
        }

        if (ChatMsgApi.TYPE_TEXT == chatHistorys.get(position).getMsgType()) {
            if (chatHistorys.get(position).getLastMsg() != null) {

                try {
                    chl_tv2.setText(new JSONObject(chatHistorys.get(position).getLastMsg()).get("body").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (ChatMsgApi.TYPE_IMAGE == chatHistorys.get(position).getMsgType()) {
            chl_tv2.setText("[Image Attachmet]");
        } else if (ChatMsgApi.TYPE_AUDIO == chatHistorys.get(position).getMsgType()) {
            chl_tv2.setText("[Audio Attachmet]");
        } else if (ChatMsgApi.TYPE_MINI_VIDEO == chatHistorys.get(position).getMsgType()) {
            chl_tv2.setText("[Video Attachmet]");
        } else if (ChatMsgApi.TYPE_FILE == chatHistorys.get(position).getMsgType()) {
            chl_tv2.setText("[Attachmet]");
        }else if (ChatMsgApi.TYPE_REVOKE == chatHistorys.get(position).getMsgType()) {
            chl_tv2.setText("[Revoke]");
        }

        else if (ChatMsgApi.TYPE_WEAK_HINT == chatHistorys.get(position).getMsgType()) {
            if (chatHistorys.get(position).getOprType() == 5) {

            } else

            {
                chl_tv2.setText("[System Message]");
        }
        }





        return rowView;


    }


}

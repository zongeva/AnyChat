package com.kpz.AnyChat.Home_Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.Utils;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.ResultCallBack;

import java.util.List;


/**
 * Created by wenrong on 14/7/2017.
 */

public class SelectChatAdaptor extends ArrayAdapter<Chat> {
    private final Context context;
    private final List<Chat> chatHistorys;
    String Avatar_Url;
    String targetIDs;

    public SelectChatAdaptor(Context context, List<Chat> chatHistorys) {
        super(context, R.layout.chathistory_lists, chatHistorys);
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


        //Identify Last Msg Type
        long targetID = chatHistorys.get(position).getID();
        long msgID = chatHistorys.get(position).getLastAtMsgID();
        int count = 1;
        int flag = 0;
        Contact entity = null;


        if (ChatMsgApi.isGroup(targetID)) {

            String groupname = RequestHelper.getGroupInfo(targetID).getName();
            Avatar_Url = RequestHelper.getGroupInfo(targetID).getAvatar();
            chl_tv1.setText("[Group] " + groupname);

            if (Avatar_Url != null) {
                Utils.loadHead(context, Avatar_Url, avatar, R.mipmap.vim_icon_default_group);
            }
        } else if (ChatMsgApi.isUser(targetID)) {

            Avatar_Url = chatHistorys.get(position).getAvatar();

            chl_tv1.setText(chatHistorys.get(position).getID() + "");
            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatHistorys.get(position).getID();
//            Utils.setNickname(context, url, chatHistorys.get(position).getID() + "", chl_tv1);
            Utils.loadHead(context, Avatar_Url, avatar, R.mipmap.vim_icon_default_user);
        }


        ClientManager.getDefault().getChatService().getMessages(targetID, msgID, count, flag, new ResultCallBack<Long, List<ChatMsg>, Void>() {
            @Override
            public void onSuccess(Long aLong, List<ChatMsg> chatMsgs, Void aVoid) {

            }

            @Override
            public void onError(int i, String s) {

            }
        });





        return rowView;
    }


}

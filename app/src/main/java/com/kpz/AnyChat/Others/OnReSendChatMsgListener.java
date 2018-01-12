package com.kpz.AnyChat.Others;


import com.vrv.imsdk.chatbean.ChatMsg;

public interface OnReSendChatMsgListener {

    //重发消息
    void onResend(ChatMsg chatMsg);
}

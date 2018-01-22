package com.kpz.AnyChat.Others;


import com.vrv.imsdk.chatbean.ChatMsg;

/**
 * 聊天界面消息点击监听
 */
public interface ItemDataChangeListener {
    //消息点击
    void onItemClick(ChatMsg chatMsg);

    //消息长按
    void onItemLongClick(ChatMsg chatMsg);

    //长按后操作
    void onItemOperation(int type, ChatMsg chatMsg);



}

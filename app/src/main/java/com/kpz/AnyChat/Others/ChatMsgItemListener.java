package com.kpz.AnyChat.Others;

/**
 * 聊天消息监听
 */
public interface ChatMsgItemListener {

    /**
     * 显示
     */
    public static final int SHOW = 1;

    /**
     * 聊天消息 状态
     * @param status
     */
    void status(int status);
}

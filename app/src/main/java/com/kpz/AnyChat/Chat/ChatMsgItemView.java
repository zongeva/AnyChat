package com.kpz.AnyChat.Chat;

import android.content.Context;
import android.widget.LinearLayout;

import com.kpz.AnyChat.Others.ChatMsgItemListener;
import com.kpz.AnyChat.Others.ItemDataChangeListener;
import com.kpz.AnyChat.Others.RequestHelper;
import com.vrv.imsdk.chatbean.ChatMsg;

/**
 * Created by Yang on 2015/11/3 003.
 */
public abstract class ChatMsgItemView extends LinearLayout {

    private final String TAG = ChatMsgItemView.class.getSimpleName();

    protected Context context;
    protected ChatMsg msgBean;
    protected int type;
    protected String encryptKey;
    protected boolean isMe = false;//是不是我发的消息
    private ChatMsgItemListener listener;
    private boolean isShow;

    public ChatMsgItemView(Context context, ChatMsg msgBean) {
        super(context);
        this.msgBean = msgBean;
        this.type = msgBean.getMsgType();
        this.isMe = RequestHelper.isMyself(msgBean.getFromID());
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        loadView();
        handleData();
        display();
    }

    protected abstract void loadView();

    protected abstract void handleData();

    protected abstract boolean display();

    protected abstract void onClick();

    protected abstract void onLongClick();

    protected void burn() {

    }

    protected void notifyFinish() {
        isShow = true;
        if (this.listener != null) {
            this.listener.status(ChatMsgItemListener.SHOW);
        }
    }

    // 子类根据需要重写该方法
    public void setProgress(long localID, int progress) {
        if (msgBean == null || localID != msgBean.getLocalID()) {
            return;
        }
    }

    public void setListener(ChatMsgItemListener listener) {
        this.listener = listener;
    }

    public boolean isShow() {
        return isShow;
    }


    //监听itemview的长按事件，将操作结果返回上去，控制adapter，notifyDataSetChanged；isShowCheckbox，多选控制显示checkbox
    protected ItemDataChangeListener itemDataChangeListener;


    public void setItemDataChangeListener(ItemDataChangeListener listener) {
        if (listener != null) {
            this.itemDataChangeListener = listener;
        }
    }
}

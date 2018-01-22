package com.kpz.AnyChat.Others;


import android.os.Parcel;
import android.os.Parcelable;

import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.Account;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.ItemModel;
import com.vrv.imsdk.util.SDKFileUtils;

public class BaseInfoBean implements Parcelable {

    public static final byte TYPE_USER = 1;
    public static final byte TYPE_GROUP = 2;
    public static final byte TYPE_ENT = 3;
    public static final byte TYPE_ORG_GROUP = 4;
    public static final byte TYPE_ORG_USER = 5;
    public static final byte TYPE_ENT_APP = 6;

    private byte type;

    private long ID = 0;
    private String name = "";
    private String text;

    private String button;
    private String content = "";
    private String icon = "";
    private byte gender = 0;
    private String background = "";
    private String backgroundByMe = "";

    private long msgId;

    public BaseInfoBean(String name) {
        this.name = name;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public byte getGender() {
        return gender;
    }

    public void setGender(byte gender) {
        this.gender = gender;
    }

    public void setBackgroundByMe(String background) {
        this.backgroundByMe = background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getBackground() {
        if (!ChatMsgApi.isGroup(this.ID)) {
            return background;
        } else {
            if (SDKFileUtils.isExist(backgroundByMe)) {
                return backgroundByMe;
            } else {
                return background;
            }
        }
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "BaseInfoBean{" +
                "type=" + type +
                ", ID=" + ID +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", button='" + button + '\'' +
                ", content='" + content + '\'' +
                ", icon='" + icon + '\'' +
                ", gender=" + gender +
                ", background='" + background + '\'' +
                ", backgroundByMe='" + backgroundByMe + '\'' +
                ", msgId=" + msgId +
                '}';
    }

    public static BaseInfoBean chat2BaseInfo(Chat chat) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(chat.getID());
        baseInfoBean.setName(chat.getName());
        baseInfoBean.setIcon(chat.getAvatar());
        baseInfoBean.setGender((byte) chat.getGender());
        return baseInfoBean;
    }

    public static BaseInfoBean chatMsg2BaseInfo(ChatMsg chatMsg) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(chatMsg.getTargetID());
        baseInfoBean.setName(chatMsg.getName());
        baseInfoBean.setIcon(chatMsg.getAvatar());
        return baseInfoBean;
    }

    public static BaseInfoBean contact2BaseInfo(Contact contact) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(contact.getID());
        baseInfoBean.setName(contact.getName());
        baseInfoBean.setIcon(contact.getAvatar());
        baseInfoBean.setGender((byte) contact.getGender());
        baseInfoBean.setBackground(contact.getChatImg());
        return baseInfoBean;
    }

    public static BaseInfoBean account2BaseInfo(Account account) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(account.getID());
        baseInfoBean.setName(account.getName());
        baseInfoBean.setIcon(account.getAvatar());
        baseInfoBean.setGender((byte) account.getGender());
        baseInfoBean.setText(account.getAccount());
        return baseInfoBean;
    }

    public static BaseInfoBean group2BaseInfo(Group group) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(group.getID());
        baseInfoBean.setName(group.getName());
        baseInfoBean.setIcon(group.getAvatar());
        baseInfoBean.setBackground(group.getChatImg());
        return baseInfoBean;
    }

    public static BaseInfoBean itemModel2BaseInfo(ItemModel itemModel) {
        BaseInfoBean baseInfoBean = new BaseInfoBean();
        baseInfoBean.setID(itemModel.getID());
        baseInfoBean.setName(itemModel.getName());
        baseInfoBean.setIcon(itemModel.getAvatar());
        return baseInfoBean;
    }

    public BaseInfoBean() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.type);
        dest.writeLong(this.ID);
        dest.writeString(this.name);
        dest.writeString(this.text);
        dest.writeString(this.button);
        dest.writeString(this.content);
        dest.writeString(this.icon);
        dest.writeByte(this.gender);
        dest.writeString(this.background);
        dest.writeString(this.backgroundByMe);
        dest.writeLong(this.msgId);
    }

    protected BaseInfoBean(Parcel in) {
        this.type = in.readByte();
        this.ID = in.readLong();
        this.name = in.readString();
        this.text = in.readString();
        this.button = in.readString();
        this.content = in.readString();
        this.icon = in.readString();
        this.gender = in.readByte();
        this.background = in.readString();
        this.backgroundByMe = in.readString();
        this.msgId = in.readLong();
    }

    public static final Creator<BaseInfoBean> CREATOR = new Creator<BaseInfoBean>() {
        @Override
        public BaseInfoBean createFromParcel(Parcel source) {
            return new BaseInfoBean(source);
        }

        @Override
        public BaseInfoBean[] newArray(int size) {
            return new BaseInfoBean[size];
        }
    };
}

package com.kpz.AnyChat.Others;

public class OptionBean {

    //消息操作
    public static final int TYPE_OPTION_MSG_FORWARD = 0x30;// 转发
    public static final int TYPE_OPTION_MSG_COLLECTION = 0x31;// 收藏
    public static final int TYPE_OPTION_MSG_DELETE = 0x32;// 删除
    public static final int TYPE_OPTION_MSG_WITHDRAW = 0x33;// 撤回
    public static final int TYPE_OPTION_MSG_MORE = 0x34;//更多
    public static final int TYPE_OPTION_MSG_EXPORT = 0x35;//导出


    public OptionBean() {
    }

    public OptionBean(int icon, String name) {
        this.name = name;
        this.icon = icon;
    }

    private int type;
    private int icon;
    private String name;
    private String intent;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }


    @Override
    public String toString() {
        return "OptionBean{" +
                "type=" + type +
                ", icon=" + icon +
                ", name='" + name + '\'' +
                ", intent='" + intent + '\'' +
                '}';

    }

}

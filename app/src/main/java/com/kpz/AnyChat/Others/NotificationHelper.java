//package com.kpz.AnyChat.Others;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.support.v7.app.NotificationCompat;
//import android.text.TextUtils;
//import android.widget.RemoteViews;
//
//import com.kpz.AnyChat.Others.BaseInfoBean;
//import com.kpz.AnyChat.Others.ChatMsgUtil;
//import com.kpz.AnyChat.Others.ImageUtil;
//import com.kpz.AnyChat.R;
//import com.vrv.imsdk.chatbean.ChatMsgApi;
//import com.vrv.imsdk.extbean.ChannelEvent;
//import com.vrv.imsdk.listener.NotificationListener;
//import com.vrv.imsdk.model.Chat;
//import com.vrv.imsdk.model.ItemModel;
//import com.vrv.imsdk.model.SDKClient;
//
//
//
///**
// * 通知栏信息管理
// */
//public class MsgReceiver {
//    private static Context context;
//    private NotificationManager notificationManager;
//    private static MsgReceiver helper ;
//
//    public static MsgReceiver getInstance(Context context) {
//        MsgReceiver.context = context;
//        if (helper == null) {
//            helper = new MsgReceiver();
//        }
//        return helper;
//    }
//
//    private MsgReceiver() {
//        if (context != null)
//            this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//    }
//
//    private void notificationShow(final Chat chat, long targetID) {
//
//        final Notification notification = buildNotification(chat, targetID);
//
//
//        setNotification(notification, chat);
//
//        if (notificationManager != null && notification != null) {
//            notificationManager.notify(Long.valueOf(chat.getID()).intValue(), notification);
//        }
//    }
//
//
//    /**
//     * 清除通知
//     */
//    public void clearAll() {
//        if (notificationManager != null) {
//            notificationManager.cancelAll();
//        }
//    }
//
//    //清除单个聊天
//    public void clear(long targetID) {
//        if (notificationManager != null) {
//            notificationManager.cancel(Long.valueOf(targetID).intValue());
//        }
//    }
//
//    /**
//     * 构建通知显示内容
//     *
//     * @param chat
//     * @return
//     */
//    private Notification buildNotification(Chat chat, long targetID) {
//        int unReadNum = chat.getUnreadCount();
//        if (unReadNum <= 0) {
//            return null;
//        }
//        String countStr = unReadNum > 99 ? "99+" : String.valueOf(unReadNum);
//        countStr = " [" + countStr + " 条] ";
//        ItemModel bean = null;
//        String message;
//        String title; // 通知标题 --- 系统消息需要单独设置
//        String ticker; // 通知第一次显示的内容
//        Bitmap largeIcon = getLargeIcon(chat); // 通知图标
//        long time = chat.getMsgTime(); // 通知时间
//        title = chat.getName();
//        String lastMsg = ChatMsgUtil.lastMsgBrief(context, chat);
//        // 群显示谁发的，单人聊天不显示
//        String whereFrom = ChatMsgApi.isGroup(chat.getID()) ? chat.getWhereFrom() : "";
//        whereFrom = TextUtils.isEmpty(whereFrom) ? "" : (whereFrom + ": ");
//        message = countStr + whereFrom + lastMsg;
//        ticker = title + ":" + lastMsg;
//
//
//        if (ChatMsgApi.isSysMsg(chat.getID())) { // 如果是系统消息，以上代码获取的为“”
//            title = "系统消息";
//            ticker = "收到系统消息";
//
//            message = countStr + lastMsg;
//        }
//        PendingIntent intent = buildNotifyReceiver(chat, targetID);
//
//        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification);
//        remoteViews.setImageViewBitmap(R.id.icon, largeIcon);
//        remoteViews.setTextViewText(R.id.title, title);
//        remoteViews.setLong(R.id.time, "setTime", time);
//        remoteViews.setTextViewText(R.id.text, message);
//
//
//        return initBuilder(ticker, time, largeIcon, title, message, intent, remoteViews).build();
//    }
//
//    private Bitmap getLargeIcon(Chat chat) {
//        Bitmap bm = null;
//        String icon = chat.getAvatar();
//        if (!TextUtils.isEmpty(icon)) {
//            bm = BitmapFactory.decodeFile(icon);
//        }
//        if (bm == null) {
//            return BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
//        } else {
//            return ImageUtil.getRoundedCornerBitmap(bm);
//        }
//    }
//
//    private NotificationCompat.Builder initBuilder(String ticker, long time, Bitmap largeIcon, String title, String message, PendingIntent intent, RemoteViews remoteViews) {
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setTicker(ticker);
//        builder.setWhen(time);
//        builder.setLargeIcon(largeIcon);
//        builder.setContentTitle(title);
//        builder.setContentText(message);
//        builder.setContentIntent(intent);
//        builder.setContent(remoteViews);
//        return builder;
//    }
//
//    //点击跳转广播处理，NotificationReceiver 接收处理
//    private PendingIntent buildNotifyReceiver(Chat chat, long targetID) {
//        if (chat.getID() == 0) {
//            return null;
//        }
//        BaseInfoBean baseInfoBean = new BaseInfoBean();
//        baseInfoBean.setIcon(chat.getAvatar());
//        baseInfoBean.setID(chat.getID());
//        baseInfoBean.setName(chat.getName());
//        baseInfoBean.setGender((byte) chat.getGender());
//        return PendingIntent.getBroadcast(context, Long.valueOf(chat.getID()).intValue(), new Intent(),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//    }
//
//    // Notification额外提示属性设置
//    private void setNotification(Notification notification, Chat chat) {
//        if (notification != null) {
//            notification.flags |= Notification.FLAG_SHOW_LIGHTS;//呼吸灯
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;//自动取消
//            notification.ledARGB = Color.GREEN;
//            notification.ledOnMS = 1000; // 亮起的时间
//            notification.ledOffMS = 1000;// 暗去的时
//
//        }
//    }
//
//    public void setNotifyListener(SDKClient client) {
//        if (client == null) return;
//        client.observeNotificationListener(new NotificationListener() {
//            @Override
//            public void onEvent(long targetID, final ChannelEvent channelEvent) {
//                // TODO: 处理音视频消息
//            }
//
//            @Override
//            public void onNotification(long targetID, Chat chat) {
//                // TODO: 处理聊天消息
//                if (chat == null) {
//                    return;
//                }
//                notificationShow(chat, targetID);
//
//            }
//        }, true);
//    }
//
//
//}
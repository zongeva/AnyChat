package com.kpz.AnyChat.Others;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.kpz.AnyChat.Chat.ChatActivity;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.extbean.ChannelEvent;
import com.vrv.imsdk.listener.NotificationListener;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.ItemModel;
import com.vrv.imsdk.model.SDKClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lenovo on 8/4/2017.
 */

public class MsgReceiver extends Service {
    SDKClient defaultClient = null;
    private NotificationManager notificationManager;
    private boolean alreadyDisplayedNotification = false;
    private static Context context;
    String lastMsg;
    String lastMsgs;
    boolean onlyonce = false;

    @Override
    public void onCreate() {
        super.onCreate();


        if (defaultClient != null) {
            defaultClient = ClientManager.getDefault();
            this.notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        } else {
            defaultClient = ClientManager.getDefault();
            this.notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            try {
                startService(new Intent(this, MsgReceiver.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (defaultClient != null) {
            defaultClient.observeNotificationListener(new NotificationListener() {
                @Override
                public void onEvent(long l, ChannelEvent channelEvent) {
                }

                @Override
                public void onNotification(long l, Chat chat) {
                    notificationShow(chat, l);
                    String lastMsg = ChatMsgUtil.lastMsgBrief(getApplicationContext(), chat);
//                    Toast.makeText(getApplicationContext(), "From Service: " + chat.getLastMsg(), Toast.LENGTH_LONG).show();
                }
            }, true);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void notificationShow(final Chat chat, long targetID) {
        if (!alreadyDisplayedNotification) {
            Notification notification = buildNotification(chat, targetID);
            setNotification(notification, chat);

            if (notificationManager != null && notification != null) {
                Intent notificationIntent = new Intent(MsgReceiver.this, ChatActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(MsgReceiver.this, 0, notificationIntent, 0);

                if(!onlyonce) {
                    notificationManager.notify(Long.valueOf(chat.getID()).intValue(), notification);
                    onlyonce = true;
                } else{
                    //
                }
                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        alreadyDisplayedNotification = true;
                    }

                    public void onFinish() {
                        alreadyDisplayedNotification = false;
                        onlyonce = false;
                    }

                }.start();

            }
        }
    }

    // Notification额外提示属性设置
    private void setNotification(Notification notification, Chat chat) {
        if (notification != null) {
            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;//呼吸灯
            notification.flags |= Notification.FLAG_AUTO_CANCEL;//自动取消
            notification.ledARGB = Color.GREEN;
            notification.ledOnMS = 1000; // 亮起的时间
            notification.ledOffMS = 1000;// 暗去的时
        }
    }

    private Notification buildNotification(Chat chat, long targetID) {
        lastMsg = "";
        lastMsgs = "";
//        String lastMsg = null;
        int unReadNum = chat.getUnreadCount();
        if (unReadNum <= 0) {
            return null;
        }
        String countStr = unReadNum > 99 ? "99+" : String.valueOf(unReadNum);
        countStr = " [" + countStr + " unread] ";
        ItemModel bean = null;
        String message;
        String title; // 通知标题 --- 系统消息需要单独设置
        String titles = "";
        String ticker; // 通知第一次显示的内容
        // Bitmap largeIcon = getLargeIcon(chat); // 通知图标qqqqqdsaghfhuid;fhui
        long time = chat.getMsgTime(); // 通知时间
        title = chat.getName();


        try {
            JSONObject jsonObject = new JSONObject(chat.getLastMsg());
            lastMsg = ChatMsgUtil.lastMsgBrief(getApplicationContext(), chat);
//            lastMsgs = lastMsg.replace("邀请", " Invite ").replace("加入该群", " Join The Group ");

            //Special Notification (Include with Chinese)

            if (lastMsg.contains("9151")) {

                lastMsgs = "Group Created ! ";


            } else {

                lastMsgs = ChatMsgUtil.lastMsgBrief(getApplicationContext(), chat);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 群显示谁发的，单人聊天不显示

//        String whereFrom = ChatMsgApi.isGroup(chat.getID()) ? chat.getWhereFrom() : "";
        String whereFrom = "";

        message = countStr + whereFrom + lastMsgs;
        ticker = title + ":" + lastMsgs;

        if (ChatMsgApi.isSysMsg(chat.getID())) { // 如果是系统消息，以上代码获取的为“”
            title = "System";
            ticker = "Received a system message";
            message = countStr + lastMsgs;
        }
        Intent notificationIntent = new Intent(MsgReceiver.this, ChatActivity.class);
        notificationIntent.putExtra("othersideid", chat.getID());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(MsgReceiver.this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        return initBuilder(ticker, time, title, message, pendingIntent).build();
    }


    private NotificationCompat.Builder initBuilder(String ticker, long time, String title, String message, PendingIntent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.drawable.profile);
        builder.setLargeIcon(BitmapFactory.decodeResource(MsgReceiver.this.getResources(),
                R.drawable.ucclogolarge));
        builder.setTicker(ticker);
        builder.setWhen(time);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setContentIntent(intent);
//        builder.setFullScreenIntent(intent,false);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setVibrate(new long[0]);
        builder.setAutoCancel(true);
        return builder;
    }

}

package com.kpz.AnyChat.Others;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.kpz.AnyChat.R;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.SysMsgService;
import com.vrv.imsdk.model.SystemMsg;
import com.vrv.imsdk.util.JsonToolHelper;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import static com.vrv.imsdk.VIMClient.getContext;


/**
 * Created by Wen Rong on 15/8/2017.
 */

public class Utils {
    public static String osType = "1";
    public static String serverAddress = " http://ucc2u.com:8080/UCC/";

    public static String urlencode(String value) {
        String result = "";
        try {
            result = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static Drawable getDrawable(Context context, int drawRes) {
        return ContextCompat.getDrawable(context, drawRes);
    }

    /**
     * 加载头像，
     *
     * @param context
     * @param path
     * @param headView
     */
    public static void loadHead(Context context, String path, ImageView headView, int def) {
        if (TextUtils.isEmpty(path) || !path.contains("/")) {
            ImageUtil.loadDefaultHead(context.getApplicationContext(), headView, def);
            return;
        }
        ImageUtil.loadViewLocalHead(context.getApplicationContext(), path, headView, def);
    }

    /**
     * recyclerView divider
     *
     * @param context
     * @return
     */
    public static RecyclerView.ItemDecoration buildDividerItemDecoration(Context context) {
        return buildDividerItemDecoration(context, 0);
    }

    public static RecyclerView.ItemDecoration buildDividerItemDecoration(Context context, int colorID) {
        HorizontalDividerItemDecoration.Builder builder = new HorizontalDividerItemDecoration.Builder(context);
        if (colorID != 0) {
            builder.drawable(new ColorDrawable(Color.TRANSPARENT));
        } else {
            builder.drawable(new ColorDrawable(context.getResources().getColor(R.color.vim_divider)));
        }
        builder.size(1);
        return builder.build();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //    String urls = Utils.serverAddress + "retrieveusername?LinkdoodID=" + chatMsgList.get(position).getFromID();
//    public static void setNickname(Context context, String url, String linkdoodid, TextView textView) {
//        Log.e("UCC Log", url + " - " + linkdoodid);
//        SQLiteDatabase sqLiteDatabase;
//        UCCDBHelper uccdbHelper;
//        Cursor cursor;
//        uccdbHelper = new UCCDBHelper(context);
//        sqLiteDatabase = uccdbHelper.getReadableDatabase();
//        cursor = uccdbHelper.getUserInfo(linkdoodid, sqLiteDatabase);
//        try {
//            String nickname = "";
//
//            if (cursor.moveToFirst()) {
//                do {
//                    nickname = cursor.getString(1);
//                    Log.e("UCC Log", "From db " + url + " - " + linkdoodid + " - " + cursor.getString(1));
//                } while (cursor.moveToNext());
//            }
//
//            cursor.close();
//
//            if (!nickname.equals("")) {
//                textView.setText(nickname);
//            } else {
//                Http_RetrieveUserName httpRetrieveUserName = new Http_RetrieveUserName(context, url, textView, linkdoodid);
//                httpRetrieveUserName.execute();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            sqLiteDatabase.close();
//            cursor.close();
//        }
//    }

//    public static void setNicknameString(Context context, String url, String linkdoodid, String content) {
//        Log.e("UCC Log", url + " - " + linkdoodid);
//        SQLiteDatabase sqLiteDatabase;
//        UCCDBHelper uccdbHelper;
//        Cursor cursor;
//        uccdbHelper = new UCCDBHelper(context);
//        sqLiteDatabase = uccdbHelper.getReadableDatabase();
//        cursor = uccdbHelper.getUserInfo(linkdoodid, sqLiteDatabase);
//        try {
//            String nickname = "";
//
//            if (cursor.moveToFirst()) {
//                do {
//                    nickname = cursor.getString(1);
//                    Log.e("UCC Log", "From db " + url + " - " + linkdoodid + " - " + cursor.getString(1));
//                } while (cursor.moveToNext());
//            }
//
//            cursor.close();
//
//            if (!nickname.equals("")) {
//                content = nickname;
//            } else {
//                Http_RetrieveUserNameString httpRetrieveUserNameString = new Http_RetrieveUserNameString(context, url, content, linkdoodid);
//                httpRetrieveUserNameString.execute();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            sqLiteDatabase.close();
//            cursor.close();
//        }
//    }
    /*
    * Trigger when a new message had been sent
    * */
//    public static void setNewMsg(Context context, String mri_ui_id, String mri_msg_id, String mri_read_status, String mri_msg_type) {
//        Log.e("Get Msg Detail", "ID-" + mri_ui_id + " MsgID" + " -" + mri_msg_id + " ReadStatus-" + mri_read_status + " MsgType-" + mri_msg_type);
//
//        try {
//            SQLiteDatabase sqLiteDatabase;
//            UCCDBHelper uccdbHelper;
//            Cursor cursor;
//            uccdbHelper = new UCCDBHelper(context);
//            sqLiteDatabase = uccdbHelper.getReadableDatabase();
//            uccdbHelper.addNewMsg(mri_ui_id, mri_msg_id, mri_read_status, mri_msg_type, sqLiteDatabase);
//            Log.e("NewMsgList", mri_ui_id);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("UCC Log", "Code: " + Utils.osType + "1701003 " + e.toString());
//            Toast.makeText(context, "Connection fail with server(NewMsg).", Toast.LENGTH_LONG).show();
//
//        }
//    }

    /*
* Get message read status with userid and msgid
* */
//    public static void getMsgReadStatus(Context context, String mri_ui_id, String mri_msg_id, String mri_read_status, String mri_msg_type, TextView textView) {
//        Log.e("Set Msg Detail", "ID-" + mri_ui_id + " MsgID" + " -" + mri_msg_id + " ReadStatus-" + mri_read_status + " MsgType-" + mri_msg_type);
//
//        try {
//
//            SQLiteDatabase sqLiteDatabase;
//            UCCDBHelper uccdbHelper;
//            uccdbHelper = new UCCDBHelper(context);
//            sqLiteDatabase = uccdbHelper.getReadableDatabase();
//
//            uccdbHelper.getMsgInfo(mri_ui_id, mri_msg_id,mri_read_status, sqLiteDatabase);
//            Cursor cursors = uccdbHelper.getMsgInfo(mri_ui_id, mri_msg_id,mri_read_status, sqLiteDatabase);
//
//            if(cursors!=null && cursors.getCount()>0 && cursors.moveToFirst())
//            {
//                /*
//                * There are still someone haven't read (PR Available)
//                * */
//                cursors.getString(0);
//                Log.e("Result",cursors.getString(2));
//                cursors.close();
//                textView.setText("unread");
//            }
//            else{
//                /*
//                * All read (PR Not Available, return not found message)
//                * */
//                textView.setText("read");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//
//            Log.e("UCC Log", "Code: " + Utils.osType + "1701004 " + e.toString());
//
//        } return;
//
//
//
//    }


    /*
    * Modify State once no PR found
    * */
//    public static void  ChangeMsgStatus(Context context, String mri_ui_id, String mri_msg_id, String mri_read_status, String mri_msg_type) {
//        Log.e("UCC Log New Msg", "ID-" + mri_ui_id + " MsgID" + " -" + mri_msg_id + " ReadStatus-" + mri_read_status + " MsgType-" + mri_msg_type);
//
//        try {
//
//            SQLiteDatabase sqLiteDatabase;
//            UCCDBHelper uccdbHelper;
//            Cursor cursor;
//            uccdbHelper = new UCCDBHelper(context);
//            sqLiteDatabase = uccdbHelper.getReadableDatabase();
//
//            uccdbHelper.ChangeMsgInfo(mri_ui_id,mri_msg_id,"AR",sqLiteDatabase);
//            Log.e("ReadStatus DB", "success" );
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("UCC Log", "Code: " + Utils.osType + "1701003 " + e.toString());
//            Toast.makeText(context, "Connection fail with server(NewMsg).", Toast.LENGTH_LONG).show();
//
//        }
//
//    }

    //复制文本
    public static void copyTxt(String srcTxt) {
        ClipboardManager clipboardManager = (ClipboardManager) VIMClient.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", srcTxt);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static Chat system2Chat(Context mContext , SystemMsg systemMsg, int unReadNum) {
        Chat chat = new Chat();
        chat.setMsgType(systemMsg.getMsgType());
        chat.setID(ChatMsgApi.getSysMsgID());
        chat.setName("System Message");
        chat.setLastMsgID(systemMsg.getMsgID());
        chat.setLastMsg(JsonToolHelper.buildTxtJson(Utils.sysMsgBrief(mContext, systemMsg, true)));
        chat.setMsgType(ChatMsgApi.TYPE_TEXT);
        chat.setMsgTime(systemMsg.getTime());
        chat.setUnreadCount(unReadNum);
        chat.setRealUnreadCount(unReadNum);
        return chat;
    }

    public static String sysMsgBrief(Context mContext, final SystemMsg msgBean, boolean isChat) {
        final String nickname = "";

        new CountDownTimer(500, 1000) {

            public void onTick(long millisUntilFinished) {
                SystemMsg tempmsg = msgBean;
                String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + tempmsg.getUserName();
//                Utils.setNicknameString(getContext(), url,tempmsg.getUserName() , nickname);
            }

            public void onFinish() {

                start();
            }

        }.start();


        if (msgBean == null) return "";
        String msg = "";
        if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ) {//收到的别人向自己发的请求，自己需要处理，同意拒绝
            if (isChat) {
                switch (msgBean.getOprType()) {
                    case 1:
                        msg = nickname + " Sent You A Friend Request";//请求加好友
                        break;
                    case 2:
                        msg = nickname + "Ask you to follow his space";//请求关注
                        break;
                    default:
                        msg = "";
                }
            } else {
                msg = msgBean.getInfo();//消息盒子内部
            }
        } else if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_RSP) {
            switch (msgBean.getOprType()) {
                case SysMsgService.OPT_BUDDY_PASS://同意
                    msg = nickname + " Agree Your Friend Request";
                    break;
                case SysMsgService.OPT_BUDDY_REFUSE://拒绝
                    msg = nickname + " Refuse Your Friend Request";
                    break;
                case SysMsgService.OPT_BUDDY_REFUSE_FOREVER://永久拒绝
                    msg = nickname + " Refuse Your Friend Request Permanantly";
                    break;
            }
        } else if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_REQ) {//收到别人发的群请求，自己需要同意或者拒绝
            switch (msgBean.getOprType()) {
                case 1:
                    msg = nickname + " Join " + msgBean.getGroupName();//邀请入群
                    break;
                case 2:
                    msg = nickname + " Request to join " + msgBean.getGroupName();//申请入群
                    break;
                default:
                    msg = nickname + ":" + msgBean.getInfo();
            }
        } else if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_RSP) {
//            if(TextUtils.isEmpty(msgBean.getRefuseReason())){缺少字段，稳定版暂时也没有赋值
//                msg=msgBean.getRefuseReason();
//            return nickname + msg;
//            }
            switch (msgBean.getOprType()) {
                case SysMsgService.OPT_GROUP_IGNORE:
                    break;
                case SysMsgService.OPT_GROUP_PASS://同意
                    msg = String.format(" Agree By ", nickname);
                    break;
                case SysMsgService.OPT_GROUP_REFUSE://拒绝
                    msg = String.format(" Refuse By ", nickname);
                    break;
                case SysMsgService.OPT_GROUP_REFUSE_FOREVER://永久拒绝
                    msg = String.format(" Refuse Permanantly By ", nickname);
                    break;
                case SysMsgService.OPT_GROUP_DELETE://解散
//                    if (!Utils.isZhLanguage(mContext)) {
//                        msg = " Group Deleted " + " by " + nickname;
//                    } else {
                        msg =" Group Deleted ";
//                    }
                    break;
                case SysMsgService.OPT_GROUP_EXIT://退出
                    msg = nickname + "Someone" + " Left The Group";

                    break;
                case SysMsgService.OPT_GROUP_REMOVE://移除群成员
                    msg = nickname + " " + " Removed from " + " " + msgBean.getGroupName();
                    break;
            }
        }
        return msg;
    }

    public static boolean isZhLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        if (locale.getLanguage().equals(new Locale("zh", "", "").getLanguage()))
            return true;
        else
            return false;
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String destPath = Environment.getExternalStorageDirectory() + "/ucc/export/img/" + String.valueOf(System.currentTimeMillis()) + "." + mime.getExtensionFromMimeType(cR.getType(contentUri));;
            File copyFile = new File( destPath+ "");
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }



    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package com.kpz.AnyChat.Others;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kpz.AnyChat.Database.UCCDBHelper;
import com.kpz.AnyChat.Database.UCCDBInfo;
import com.kpz.AnyChat.Network.Http_RetrieveReadStatus;
import com.kpz.AnyChat.Network.Http_RetrieveUserName;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.VIMClient;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.kpz.AnyChat.Database.UCCDBInfo.MsgReadInfo.msgreadinfo_tbl;


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
    public static void setNickname(Context context, String url, String linkdoodid, TextView textView) {
        Log.e("UCC Log", url + " - " + linkdoodid);
        SQLiteDatabase sqLiteDatabase;
        UCCDBHelper uccdbHelper;
        Cursor cursor;
        uccdbHelper = new UCCDBHelper(context);
        sqLiteDatabase = uccdbHelper.getReadableDatabase();
        cursor = uccdbHelper.getUserInfo(linkdoodid, sqLiteDatabase);
        try {
            String nickname = "";

            if (cursor.moveToFirst()) {
                do {
                    nickname = cursor.getString(1);
                    Log.e("UCC Log", "From db " + url + " - " + linkdoodid + " - " + cursor.getString(1));
                } while (cursor.moveToNext());
            }

            cursor.close();

            if (!nickname.equals("")) {
                textView.setText(nickname);
            } else {
                Http_RetrieveUserName httpRetrieveUserName = new Http_RetrieveUserName(context, url, textView, linkdoodid);
                httpRetrieveUserName.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.close();
            cursor.close();
        }
    }

    /*
    * Trigger when a new message had been sent
    * */
    public static void setNewMsg(Context context, String mri_ui_id, String mri_msg_id, String mri_read_status, String mri_msg_type) {
        Log.e("Get Msg Detail", "ID-" + mri_ui_id + " MsgID" + " -" + mri_msg_id + " ReadStatus-" + mri_read_status + " MsgType-" + mri_msg_type);

        try {
            SQLiteDatabase sqLiteDatabase;
            UCCDBHelper uccdbHelper;
            Cursor cursor;
            uccdbHelper = new UCCDBHelper(context);
            sqLiteDatabase = uccdbHelper.getReadableDatabase();
            uccdbHelper.addNewMsg(mri_ui_id, mri_msg_id, mri_read_status, mri_msg_type, sqLiteDatabase);
            Log.e("NewMsgList", mri_ui_id);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UCC Log", "Code: " + Utils.osType + "1701003 " + e.toString());
            Toast.makeText(context, "Connection fail with server(NewMsg).", Toast.LENGTH_LONG).show();

        }
    }

    /*
* Get message read status with userid and msgid
* */
    public static void getMsgReadStatus(Context context, String mri_ui_id, String mri_msg_id, String mri_read_status, String mri_msg_type,TextView textView) {
        Log.e("Set Msg Detail", "ID-" + mri_ui_id + " MsgID" + " -" + mri_msg_id + " ReadStatus-" + mri_read_status + " MsgType-" + mri_msg_type);

        try {

            SQLiteDatabase sqLiteDatabase;
            UCCDBHelper uccdbHelper;
            uccdbHelper = new UCCDBHelper(context);
            sqLiteDatabase = uccdbHelper.getReadableDatabase();

            uccdbHelper.getMsgInfo(mri_ui_id, mri_msg_id,mri_read_status, sqLiteDatabase);
            Cursor cursors = uccdbHelper.getMsgInfo(mri_ui_id, mri_msg_id,mri_read_status, sqLiteDatabase);

            if(cursors!=null && cursors.getCount()>0 && cursors.moveToFirst())
            {
                /*
                * There are still someone havent read (PR Available)
                * */
                cursors.getString(0);
                Log.e("Result",cursors.getString(2));
                cursors.close();
                textView.setText("unread");
            }
            else{
                /*
                * All read (PR Not Available, return not found message)
                * */
                textView.setText("read");
            }

        } catch (Exception e) {
            e.printStackTrace();

            Log.e("UCC Log", "Code: " + Utils.osType + "1701004 " + e.toString());

        } return;



    }


    /*
    * Modify State once no PR found
    * */
    public static void  ChangeMsgStatus(Context context, String mri_ui_id, String mri_msg_id, String mri_read_status, String mri_msg_type) {
        Log.e("UCC Log New Msg", "ID-" + mri_ui_id + " MsgID" + " -" + mri_msg_id + " ReadStatus-" + mri_read_status + " MsgType-" + mri_msg_type);

        try {

            SQLiteDatabase sqLiteDatabase;
            UCCDBHelper uccdbHelper;
            Cursor cursor;
            uccdbHelper = new UCCDBHelper(context);
            sqLiteDatabase = uccdbHelper.getReadableDatabase();

            uccdbHelper.ChangeMsgInfo(mri_ui_id,mri_msg_id,"AR",sqLiteDatabase);
            Log.e("ReadStatus DB", "success" );


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UCC Log", "Code: " + Utils.osType + "1701003 " + e.toString());
            Toast.makeText(context, "Connection fail with server(NewMsg).", Toast.LENGTH_LONG).show();

        }

    }

    //复制文本
    public static void copyTxt(String srcTxt) {
        ClipboardManager clipboardManager = (ClipboardManager) VIMClient.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", srcTxt);
        clipboardManager.setPrimaryClip(clipData);
    }

}

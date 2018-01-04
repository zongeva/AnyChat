package com.kpz.AnyChat.Others;

import android.content.Context;
import android.widget.Toast;

import com.vrv.imsdk.VIMClient;

public class ToastUtil {

    public static void showShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int resID) {
        String msg = context.getString(resID);
        showShort(context, msg);
    }

    public static void showShort(String msg) {
        Toast.makeText(VIMClient.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(int resID, String defMsg) {
        String msg = VIMClient.getContext().getString(resID, defMsg);
        Toast.makeText(VIMClient.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(int resID) {
        String msg = VIMClient.getContext().getString(resID);
        Toast.makeText(VIMClient.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
package com.kpz.AnyChat.Others;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2017-11-29.
 */

public class App {
    public static  void finishApp (AppCompatActivity appCompatActivity) {
        appCompatActivity.finish();
    }

    public static  void refersh (AppCompatActivity appCompatActivity) {
        appCompatActivity.recreate();
    }
}

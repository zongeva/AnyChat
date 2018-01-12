package com.kpz.AnyChat.Others;

import android.view.View;

/**
 * RecyclerView 选项点击长按监听
 */
public interface OnItemClickListener {

    void OnItemClick(int position, View view);

    boolean OnItemLongClick(int position, View view);
}

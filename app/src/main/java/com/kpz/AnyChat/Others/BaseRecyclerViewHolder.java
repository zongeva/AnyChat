package com.kpz.AnyChat.Others;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerViewHolder
 */
public abstract class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {
    public View itemView;

    public BaseRecyclerViewHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }
}

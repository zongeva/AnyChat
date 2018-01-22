package com.kpz.AnyChat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kpz.AnyChat.Home_Activity.HomeActivity;
import com.kpz.AnyChat.Others.BaseActivity;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.SysMsgAdapter;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.listener.ReceiveSysMsgListener;
import com.vrv.imsdk.model.SysMsgService;
import com.vrv.imsdk.model.SystemMsg;

import java.util.ArrayList;
import java.util.List;

public class SystemBoxActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private long time;
    private final int OFFSET = 30;//偏移量
    private RecyclerView recyclerView;
    private SysMsgAdapter adapter;
    private List<SystemMsg> list = new ArrayList<>();

    public static void start(Context context, long time) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(context, SystemBoxActivity.class);
        intent.putExtra("time", time);
        context.startActivity(intent);

    }


    @Override
    protected void setToolBar() {
        toolbar.setTitle("System Request");
    }

    @Override
    protected void loadContentLayout() {
        setContentView(R.layout.vim_activity_system_box);
    }

    @Override
    protected void findViews() {
        recyclerView = (RecyclerView) findViewById(R.id.rc_system_list);
    }

    @Override
    protected void setViews() {
        adapter = new SysMsgAdapter(context, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        requestSysMsg();

    }


    @Override
    public void onClick(View v) {

    }

    private void requestSysMsg() {
        if (time < 0) {
            return;
        }
        /**
         * 获取系统消息消息
         *
         * @param type     传入响应消息类型 0 全部 1 加好友请求 2 加好友响应 3 加群请求 4 加群
         * @param count    传入数量
         * @param msgID    传入消息id
         * @param flag     传入偏移标志 0 向上偏移 1 向下偏移
         * @param callBack 传入接收结果回调  _1错误信息  _2系统消息集合
         */
        RequestHelper.getSysMessages(0, OFFSET, time, Constants.FLAG_UP, new RequestCallBack<List<SystemMsg>, Void, Void>() {
            @Override
            public void handleSuccess(List<SystemMsg> result, Void aVoid, Void aVoid2) {
                ClientManager.getDefault().getSysMsgService().setMsgRead(result);
                if (result == null || result.isEmpty()) {
                    return;
                }
                int addCount = result.size();
                list.addAll(result);
                SysMsgService sysMsgService =  ClientManager.getDefault().getSysMsgService();
                sysMsgService.setMsgRead(result);
                adapter.notifyItemRangeInserted(list.size() - addCount, addCount);
                if (addCount < OFFSET) {
                    time = -1;
                } else {
                    time = list.get(adapter.getItemCount() - 1).getTime() - 1;
                }
            }
        });


    }

    @Override
    protected void setListener() {
        setNotifyListener();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem == adapter.getItemCount() - 1) {
                    requestSysMsg();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        RequestHelper.setSysMsgRead(list);
    }

    @Override
    public void onBackPressed() {

        Intent data = new Intent(SystemBoxActivity.this,HomeActivity.class);
        startActivity(data);

    }



    @Override
    public void onRefresh() {
        requestSysMsg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientManager.getDefault().getSysMsgService().observeReceiveListener(sysMsgListener, false);
    }

    private void setNotifyListener() {
        ClientManager.getDefault().getSysMsgService().observeReceiveListener(sysMsgListener, true);
    }

    ReceiveSysMsgListener sysMsgListener = new ReceiveSysMsgListener() {
        @Override
        public void onReceive(SystemMsg systemMsg, int i) {
            list.add(0, systemMsg);
            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
        }
    };
}

package com.kpz.AnyChat.Group_Chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.kpz.AnyChat.Chat.ChatActivity;
import com.kpz.AnyChat.Chat.Chat_Profile;
import com.kpz.AnyChat.Others.BaseActivity;
import com.kpz.AnyChat.Others.GroupMemberAdapterNormal;
import com.kpz.AnyChat.Others.IndexSideBar;
import com.kpz.AnyChat.Others.MaterialSimpleListAdapter;
import com.kpz.AnyChat.Others.MaterialSimpleListItem;
import com.kpz.AnyChat.Others.OnItemClickListener;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Member;

import java.util.List;


/**
 * 选择群成员，
 */
public class SelectGroupMemberActivity extends BaseActivity {

    private RecyclerView listView;
    private IndexSideBar indexBar;
    private GroupMemberAdapterNormal adapter;
    long othersideid;
    private Member creator;

    public static void startForResult(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, ChatActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("Choose Contact");
    }

    @Override
    protected void loadContentLayout() {
        setContentView(R.layout.vim_fragment_contacts2);
    }

    @Override
    protected void findViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (RecyclerView)findViewById(R.id.z_contact);
        indexBar = (IndexSideBar)findViewById(R.id.side_bar);
    }

    @Override
    protected void setViews() {
        listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerViewHeader headerView = (RecyclerViewHeader) findViewById(R.id.header);

        //全体成员
        ((TextView) headerView.findViewById(R.id.name_tv)).setText("All Members");
        headerView.findViewById(R.id.ll_contacts_group).setOnClickListener(this);
        headerView.attachTo(listView);
        adapter = new GroupMemberAdapterNormal(context);
        listView.setAdapter(adapter);
        listView.smoothScrollToPosition(0);
        indexBar.setListView(listView);
    }

    @Override
    protected void onStart() {
        othersideid = getIntent().getExtras().getLong("user_group_id");
        super.onStart();
        RequestHelper.getMemberList(othersideid, new RequestCallBack<List<Member>, Void, Void>() {
            @Override
            public void handleSuccess(List<Member> members, Void aVoid, Void aVoid2) {
                adapter.update();
            }
        });


    }

    @Override
    protected void onStop() {
        indexBar.windowRemoveView();
        super.onStop();
    }

    @Override
    protected void setListener() {
        adapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void OnItemClick(int position, View view) {
                Intent intent = new Intent(context, Chat_Profile.class);
                intent.putExtra("othersideid", adapter.getItemObject(position).getID());
                context.startActivity(intent);
                resultFinish(adapter.getItemObject(position));
            }

            @Override
            public boolean OnItemLongClick(int position, View view) {

//                if(ChatMsgApi.isAdminUser(RequestHelper.getAccountInfo().getID())){

                    final int pos = position;
                final MaterialSimpleListAdapter adapters = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
                    @Override
                    public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                        switch (index) {
                            case 0:

                                new MaterialDialog.Builder(SelectGroupMemberActivity.this)
                                        .title("Remove Member")
                                        .content("Do You Want To Remove This Member From Group?")
                                        .positiveText("Yes")
                                        .negativeText("Cancel")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                RequestHelper.removeMember(othersideid, adapter.getItemObject(pos).getID(), new RequestCallBack() {
                                                    @Override
                                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                                        Log.e("UCC Log MemberDel", "Succesfully");
                                                    }

                                                    @Override
                                                    public void handleFailure(int code, String message) {
                                                        super.handleFailure(code, message);
                                                        Toast.makeText(SelectGroupMemberActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                //Cancel
                                            }
                                        })
                                        .show();
                                break;
                            case 1:

                                new MaterialDialog.Builder(SelectGroupMemberActivity.this)
                                        .title("Transfer Group Ownership")
                                        .content("Do You Want Transfer Your Ownership To Selected Member?")
                                        .positiveText("Yes")
                                        .negativeText("Cancel")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                RequestHelper.transferGroup(othersideid, adapter.getItemObject(pos).getID(), new RequestCallBack() {
                                                    @Override
                                                    public void handleSuccess(Object o, Object o2, Object o3) {
                                                        Log.e("UCC Log OwnTrans", "Success");
                                                    }
                                                    @Override
                                                    public void handleFailure(int code, String message) {
                                                        super.handleFailure(code, message);
                                                        Toast.makeText(SelectGroupMemberActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                //Cancel
                                            }
                                        })
                                        .show();

                                break;
                        }
                    }
                });

                adapters.add(
                        new MaterialSimpleListItem.Builder(getApplicationContext())
                                .content("Remove Member From Group")
                                .backgroundColor(Color.WHITE)
                                .build());
                adapters.add(
                        new MaterialSimpleListItem.Builder(getApplicationContext())
                                .content("Transfer Group Ownership")
                                .backgroundColor(Color.WHITE)
                                .build());


                new MaterialDialog.Builder(SelectGroupMemberActivity.this)
                        .adapter(adapters, null)
                        .show();

//            }



            return true;
            }

        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_contacts_group) {
            Member member = new Member();
            member.setID(othersideid);
            member.setName("All Members");
            resultFinish(member);
        }
    }

    private void resultFinish(Member contact) {
        Intent data = new Intent();
        data.putExtra("data", contact);
        setResult(RESULT_OK, data);
        finish();
    }
}

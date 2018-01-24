package com.kpz.AnyChat.Group_Chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
import com.kpz.AnyChat.Chat.ChatActivity;
import com.kpz.AnyChat.Others.BaseActivity;
import com.kpz.AnyChat.Others.GroupMemberAdapter;
import com.kpz.AnyChat.Others.IndexSideBar;
import com.kpz.AnyChat.Others.OnItemClickListener;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.R;
import com.vrv.imsdk.model.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 选择群成员，
 */
public class SelectGroupMemberTag extends BaseActivity {
    boolean selected;
    private RecyclerView listView;
    private IndexSideBar indexBar;
    private GroupMemberAdapter adapter;
    long othersideid;
    private HashMap<Long, Integer> choiceMap = new HashMap<>();
    private ArrayList<Long> choiceList = new ArrayList<Long>();



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
        setContentView(R.layout.vim_fragment_contacts);
    }

    @Override
    protected void findViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (RecyclerView) findViewById(R.id.z_contact);
        indexBar = (IndexSideBar) findViewById(R.id.side_bar);

    }

    @Override
    protected void setViews() {
        listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerViewHeader headerView = (RecyclerViewHeader) findViewById(R.id.header);

        //全体成员
        ((TextView) headerView.findViewById(R.id.name_tv)).setText("All Members");
        headerView.findViewById(R.id.ll_contacts_group).setOnClickListener(this);
        headerView.attachTo(listView);
        adapter = new GroupMemberAdapter(context);
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
        LayoutInflater inflater = (LayoutInflater) SelectGroupMemberTag.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView38 = inflater.inflate(R.layout.vim_item_contact_has_index, null);
        Button confirm = (Button) findViewById(R.id.confirm);
        LinearLayout layout = (LinearLayout) rowView38.findViewById(R.id.ll_item_contact);
//        final CheckBox checkBox = (CheckBox) rowView38.findViewById(R.id.ch_item_select);

        adapter.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void OnItemClick(int position, View view) {

                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.ch_item_select);

                Member member = new Member();
                member.setID(adapter.getItemObject(position).getID());


                if (checkBox.isChecked() == true) {
                    choiceList.remove(member.getID());
                    checkBox.setChecked(false);
                } else if (checkBox.isChecked() != true) {
                    if (!choiceList.contains(member.getID())) {
                        choiceList.add(member.getID());
                    }
                    checkBox.setChecked(true);
                }
            }


            @Override
            public boolean OnItemLongClick(int position, View view) {
                return false;
            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("Related User", choiceList + "");
                resultFinish(choiceList);
            }
        });


    }


    /*
    * Select one by one
    * */
    private void resultFinish(ArrayList<Long> contact) {
        Intent data = new Intent();
        data.putExtra("data", contact);
        setResult(RESULT_OK, data);
        finish();
    }

    /*
* Select All Members (Group ID)
* */
    @Override
    public void onClick(View v) {
        RequestHelper.getMemberList(othersideid, new RequestCallBack<List<Member>, Void, Void>() {
            @Override
            public void handleSuccess(List<Member> members, Void aVoid, Void aVoid2) {
                for (int i = 0 ; i < members.size() ; i++){
                    choiceList.add(members.get(i).getID());
                }
                Log.e("Error", "Ids"+ choiceList);
                resultFinish(choiceList);
            }
        });

    }


}

package com.kpz.AnyChat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kpz.AnyChat.Chat.ChatActivity;
import com.kpz.AnyChat.Group_Chat.ResultModel;
import com.kpz.AnyChat.Network.ChatCallback;
//import com.kpz.AnyChat.Network.Http_TokenGroupAPI;
import com.kpz.AnyChat.Others.BaseActivity;
import com.kpz.AnyChat.Others.BaseInfoBean;
import com.kpz.AnyChat.Others.RequestCallBack;
import com.kpz.AnyChat.Others.RequestHelper;
import com.kpz.AnyChat.Others.SearchListAdapter;
import com.kpz.AnyChat.Others.ToastUtil;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.bean.ContactVerifyType;
import com.vrv.imsdk.bean.EntAppInfo;
import com.vrv.imsdk.bean.EntInfo;
import com.vrv.imsdk.bean.OrgGroupInfo;
import com.vrv.imsdk.bean.OrgUserInfo;
import com.vrv.imsdk.bean.SearchResult;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.TinyGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchActivity extends BaseActivity implements ChatCallback {


    private List<BaseInfoBean> list = new ArrayList<BaseInfoBean>();
    private SearchListAdapter adapter = new SearchListAdapter(context, list);
    private ArrayList<BaseInfoBean> searchlist = new ArrayList<BaseInfoBean>();

    private EditText editText;
    private Button button;
    private ListView listView;
    private long userID;
    private long groupID;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
//    ChatCallback chatCallback = this;


    public static void start(Activity activity) {
        Intent intent = new Intent();
        intent.setClass(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void setToolBar() {
        toolbar.setTitle("Group Search");
    }

    @Override
    protected void loadContentLayout() {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void findViews() {
        editText = (EditText) findViewById(R.id.et_search);
        button = (Button) findViewById(R.id.bt_search);
        listView = (ListView) findViewById(R.id.lv_search);

    }

    @Override
    protected void setViews() {
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Group Search");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void setListener() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!editText.getText().toString().equals("") && !editText.getText().toString().equals("")) {

                    searchNetContact(editText.getText().toString());


                } else {
                    Toast.makeText(context, "Please insert search item", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void searchNetContact(String key) {

//        TelephonyManager telephonyManager;
//        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        @SuppressLint("MissingPermission") String deviceId = telephonyManager.getDeviceId();
//        String ids = String.valueOf(RequestHelper.getAccountInfo().getID());
//        SharedPreferences prefs = this.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
//        String shared_login_password = prefs.getString("shared_login_password", "");//"No name defined" is the default value.
//        String url_get_token = Utils.serverAddress + "getauthorizationtoken?LinkdoodID=" + RequestHelper.getAccountInfo().getID() + "&UserSecret=" + shared_login_password + "&AppRandomKey=" + deviceId;
////           public Http_TokenGroupAPI(Context context, int type, String url_token, String linkdoodid, List<String> datas, String deviceId, String param1, String param2, String param3) {
//        Log.e("URL Token ", url_get_token);
////        Http_TokenGroupAPI tga = new Http_TokenGroupAPI(context, 1, url_get_token, ids, searchlist, deviceId, Utils.urlencode(key), "", "", new ChatCallback() {
////            @Override
////            public void handleReturnData(ArrayList<ResultModel> list) {
////                Log.e("Error", "Check list size: " + list.size());
////                Log.e("Error", "Check Slist size: " + searchlist.size());
////
////                adapter = new SearchListAdapter(SearchActivity.this, list);
////                listView.setAdapter(adapter);
////                adapter.notifyDataSetChanged();
////                searchlist = list;
////
////                listView.setOnItemClickListener(new OnItemClickListener() {
////                    @Override
////                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////
////                        if (!searchlist.get(position).groupid.equals("")) {
////                            adds(searchlist.get(position));
////
////                        } else{
////                            Toast.makeText(context, "Empty ID " + searchlist.get(position).groupid, Toast.LENGTH_SHORT).show();
////
////                        }
////                    }
////                });
////
////            }
////        });
////        tga.execute();
//
//        SearchService searchService = ClientManager.getDefault().getSearchService();
////        RequestHelper.searchFromNet(key, new RequestCallBack<SearchResult, Void, Void>() {
////            @Override
////            public void handleSuccess(SearchResult searchResult, Void aVoid, Void aVoid2) {
////                adapter = new SearchListAdapter(SearchActivity.this, list);
////                listView.setAdapter(adapter);
////                adapter.notifyDataSetChanged();
////                searchlist = list;
////
////                listView.setOnItemClickListener(new OnItemClickListener() {
////                    @Override
////                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////
////                        if (!searchlist.get(position).groupid.equals("")) {
////                            adds(searchlist.get(position));
////
////                        } else{
////                            Toast.makeText(context, "Empty ID " + searchlist.get(position).groupid, Toast.LENGTH_SHORT).show();
////
////                        }
////                    }
////                });
////            }
////        });

        RequestHelper.searchFromNet(key, new RequestCallBack<SearchResult, Void, Void>(true, context) {
            @Override
            public void handleSuccess(SearchResult searchResult, Void aVoid, Void aVoid2) {

                List<BaseInfoBean> searchList = SearchResult2List(searchResult);

                if (searchList != null && searchList.size() > 0) {
                    list = searchList;

                    adapter = new SearchListAdapter(context, list);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            BaseInfoBean bean = list.get(position);
                            userID = bean.getID();
                            if (ChatMsgApi.isUser(userID)) {
                                if (RequestHelper.isBuddy(userID)) {

                                    Toast.makeText(context, "ID" + userID, Toast.LENGTH_SHORT).show();

                                } else {
                                    adds(bean);
                                }
                            } else if (ChatMsgApi.isGroup(userID)) {
                                if (ClientManager.getDefault().getContactService().findItemByID(userID) != null) {
                                    Toast.makeText(context, "ID" + userID, Toast.LENGTH_SHORT).show();
                                } else {
                                    adds(bean);
                                }
                            }
                        }
                    });

                } else {
                    listView.setVisibility(View.GONE);

                }
            }
        });
    }





    @Override
    public void handleReturnData(ArrayList<BaseInfoBean> list) {
        Log.e("Error", "Check list size: " + list.size());
        searchlist = list;

        adapter = new SearchListAdapter(SearchActivity.this, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void onClick(View v) {

    }


    private void updateData() {

        Log.e("Lists", searchlist + "");
        if (adapter == null) {
            adapter = new SearchListAdapter(context, searchlist);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    ResultModel bean = searchlist.get(position);
                    userID = Long.parseLong(bean.groupid);

                    if (ClientManager.getDefault().getContactService().findItemByID(userID) != null) {
                        Toast.makeText(context, "ID" + userID, Toast.LENGTH_SHORT).show();
                    } else {
                        adds(bean);
                    }
                }
            });
        }
        adapter.notifyDataSetChanged();
    }


    private void adds(final ResultModel bean) {


        AlertDialog.Builder builder = new Builder(context);
        builder.setMessage("Request To Join " + bean.name + "?");
        builder.setTitle("Confirmation");
        builder.setPositiveButton("Yes", new OnClickListener() {
            @Override
            //isInGroup ( GroupID, MemberID)
            public void onClick(DialogInterface dialog, int which) {
                long groupid = Long.parseLong(bean.groupid);
                if (RequestHelper.isInGroup(groupid, RequestHelper.getUserID())) {
                    ToastUtil.showShort(context, "You Already In The Group !");
                    Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                    intent.putExtra("othersideid",groupid);
                    startActivity(intent);

                } else {
                    if (ChatMsgApi.isUser(groupid)) {
//                        addGroup();

                    } else if (ChatMsgApi.isGroup(groupid)) {
                        groupID = groupid;
                        addGroup();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //先拉取验证方式，然后请求添加好友
    private void getBuddyVerify() {

        ClientManager.getDefault().getContactService().getVerifyType(userID, new ResultCallBack<ContactVerifyType, Void, Void>() {
            @Override
            public void onSuccess(ContactVerifyType contactVerifyType, Void aVoid, Void aVoid2) {
                if (contactVerifyType.getType() == Constants.TYPE_NOT_ALLOW) {
                    ToastUtil.showShort(context, "Friend Request Denied");
                } else {
                    addBuddy();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    /**
     * 添加好友
     */
    private void addBuddy() {
        String verifyInfo = ClientManager.getDefault().getUserService().getAccountInfo().getName();
        ClientManager.getDefault().getContactService().add(userID, verifyInfo, "", new ResultCallBack() {
            @Override
            public void onSuccess(Object o, Object o2, Object o3) {
                ToastUtil.showShort(context, "Friend Request Had Been Sent");
            }

            @Override
            public void onError(int i, String s) {

            }
        });


    }

    private void getGroupVerifyType() {
        RequestHelper.getGroupSet(groupID, new RequestCallBack<Byte, Byte, Void>() {
            @Override
            public void handleSuccess(Byte aByte, Byte aByte2, Void aVoid) {
                if (aByte != 1) {
                    addGroup();
                } else {
                    ToastUtil.showShort(context, "Group Admin Deny Your Request");
                }
            }
        });
    }

    private void addGroup() {
        final String verifyInfo = "";
//        final String verifyInfo = RequestHelper.getAccountInfo().getName();

        RequestHelper.addGroup(groupID, verifyInfo, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {
                ToastUtil.showShort(context, "Join Group Request Had Been Sent");
            }
        });
    }


    private List<BaseInfoBean> SearchResult2List(SearchResult list) {
        List<BaseInfoBean> result = new ArrayList<>();
        if (list == null) {
            Toast.makeText(this, "No Result Found", Toast.LENGTH_SHORT).show();
            return result;
        }
//        List<User> users = list.getUsers();
//        if (users != null && users.size() > 0) {
//            for (User userBean : users) {
//                BaseInfoBean bean = new BaseInfoBean();
//                bean.setType(BaseInfoBean.TYPE_USER);
//                bean.setID(userBean.getID());
//                bean.setName(userBean.getName());
//                bean.setIcon(userBean.getAvatar());
//                result.add(bean);
//            }
//        }
        //private List<TinyGroup> groups;
        List<TinyGroup> groups = list.getGroups();
        if (groups != null && groups.size() > 0) {
            for (TinyGroup groupBean : groups) {
                BaseInfoBean bean = new BaseInfoBean();
                bean.setType(BaseInfoBean.TYPE_GROUP);
                bean.setID(groupBean.getID());
                bean.setName(groupBean.getName());
                bean.setIcon(groupBean.getAvatar());
                result.add(bean);
            }
        }
        // private List<EntInfo> enterprises;                    ///< 企业列表 vtent.
        List<EntInfo> entInfos = list.getEnterprises();
        if (entInfos != null && entInfos.size() > 0) {
            for (EntInfo entInfo : entInfos) {
                BaseInfoBean bean = new BaseInfoBean();
                bean.setType(BaseInfoBean.TYPE_ENT);
                bean.setID(entInfo.getEntID());
                bean.setName(entInfo.getFullName());
                bean.setIcon("");
                result.add(bean);
            }
        }
        // private List<OrgGroupInfo> orgGroups;            ///< 企业组织列表 vtOrgroup.
        List<OrgGroupInfo> orgGroups = list.getOrgGroups();
        if (orgGroups != null && orgGroups.size() > 0) {
            for (OrgGroupInfo orgGroupInfo : orgGroups) {
                BaseInfoBean bean = new BaseInfoBean();
                bean.setType(BaseInfoBean.TYPE_ORG_GROUP);
                bean.setID(orgGroupInfo.getOrgID());
                bean.setName(orgGroupInfo.getOrgName());
                bean.setIcon("");
                result.add(bean);
            }
        }
        //private Map<String, List<OrgUserInfo>> orgUserMap;///< 组织用户列表 vtorgUser
        Map<String, List<OrgUserInfo>> orgUserMap = list.getOrgUserMap();
        if (orgUserMap != null && orgUserMap.size() > 0) {
            for (Map.Entry<String, List<OrgUserInfo>> entry : orgUserMap.entrySet()) {
                String key = entry.getKey();
                List<OrgUserInfo> orgUserInfos = entry.getValue();
                for (OrgUserInfo orgUser : orgUserInfos) {
                    BaseInfoBean bean = new BaseInfoBean();
                    bean.setType(BaseInfoBean.TYPE_ORG_USER);
                    bean.setID(orgUser.getUserID());
                    bean.setName(orgUser.getName());
                    bean.setIcon(orgUser.getUserHead());
                    result.add(bean);
                }
            }
        }
        // private List<EntAppInfo> entApps;            ///< 企业应用列表 vtEntApp
        List<EntAppInfo> apps = list.getEntApps();
        if (apps != null && apps.size() > 0) {
            for (EntAppInfo entApp : apps) {
                BaseInfoBean bean = new BaseInfoBean();
                bean.setType(BaseInfoBean.TYPE_ENT_APP);
                bean.setID(entApp.getAppID());
                bean.setName(entApp.getAppName());
                bean.setIcon(entApp.getAppIcon());
                result.add(bean);
            }
        }
        return result;
    }
}

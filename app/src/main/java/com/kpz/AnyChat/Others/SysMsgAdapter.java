package com.kpz.AnyChat.Others;

import android.content.Context;
import android.os.Handler;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kpz.AnyChat.R;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.SysMsgService;
import com.vrv.imsdk.model.SystemMsg;


import org.w3c.dom.Text;

import java.util.List;

/**
 * 验证消息
 */
public class SysMsgAdapter extends BaseRecyclerAdapter<SysMsgAdapter.SysMsgViewHolder> {

    private Context context;
    private List<SystemMsg> list;//= new ArrayList<>();
    private LongSparseArray<String> avatarMap;
    public SysMsgAdapter(Context context, List<SystemMsg> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SysMsgViewHolder(View.inflate(context, R.layout.vim_view_sysmsg_item, null));
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {

        SysMsgViewHolder viewHolder = (SysMsgViewHolder) holder;
        SystemMsg bean = list.get(position);
        setView(viewHolder, position, bean);

        if (bean.getIsResponse() == SysMsgService.RESPONSE_NEED) {
            viewHolder.llOptBtn.setVisibility(View.VISIBLE);
            viewHolder.tvStatus.setVisibility(View.GONE);
        } else {
            viewHolder.tvStatus.setVisibility(View.VISIBLE);
            viewHolder.llOptBtn.setVisibility(View.GONE);
        }
        msgOperate(position, viewHolder, bean);
    }

    private void setView(SysMsgViewHolder holder, int position, SystemMsg msgBean) {
        int imgDefaultID;
        int titleDefaultID;
        String name;
        long targetID;


        if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_REQ || msgBean.getMsgType() == SysMsgService.TYPE_GROUP_RSP) {
            imgDefaultID = R.mipmap.vim_icon_default_group;
            titleDefaultID = R.string.vim_verifyGroup;
            targetID = msgBean.getGroupID();
//            String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + targetID;
            //            Utils.setNickname(context, url, targetID + "",holder.dummy );
//            if(holder.dummy != null){
//                name = holder.dummy.getText().toString();
//            }
            name = msgBean.getGroupName();
        } else {
            if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ) {
                titleDefaultID = R.string.vim_verifyBuddy;
            } else {
                titleDefaultID = R.string.vim_checkingmsg_item_0_list;
            }
            imgDefaultID = R.mipmap.vim_icon_default_user;
            targetID = msgBean.getUserID();

            name = msgBean.getUserName();



        }
        UserInfoConfig.loadHead(context,getAvatar(targetID, position), holder.imgIcon, imgDefaultID);

        holder.tvCatalog.setText(titleDefaultID);
        holder.tvTime.setText(DateTimeUtils.formatFullTime(context, msgBean.getTime()));
        holder.tvName.setText(TextUtils.isEmpty(name) ? targetID + "" : name);

        setMsg(holder, msgBean);
        setStatus(holder.tvStatus, msgBean);
        if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ | msgBean.getMsgType() == SysMsgService.TYPE_GROUP_REQ) {
            holder.flOpt.setVisibility(View.VISIBLE);
        } else {
            holder.flOpt.setVisibility(View.GONE);
        }
    }

    private void setStatus(TextView tvStatus, SystemMsg msgBean) {
        String status = "";
        String agree = context.getString(R.string.vim_agree_set);
        String deny = context.getString(R.string.vim_deny_set);
        String ignore = context.getString(R.string.vim_ignore_set);
        if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ || msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_RSP) {
            switch (msgBean.getOprType()) {
                case SysMsgService.OPT_BUDDY_PASS:
                    status = agree;
                    break;
                case SysMsgService.OPT_BUDDY_REFUSE_FOREVER:
                case SysMsgService.OPT_BUDDY_REFUSE:
                    status = deny;
                    break;
            }
        } else {
            switch (msgBean.getOprType()) {
                case SysMsgService.OPT_GROUP_PASS:
                    status = agree;
                    break;
                case SysMsgService.OPT_GROUP_IGNORE:
                    status = ignore;
                    break;
                case SysMsgService.OPT_GROUP_REFUSE_FOREVER:
                case SysMsgService.OPT_GROUP_REFUSE:
                    status = deny;
                    break;
            }
        }
        tvStatus.setText(status);
    }

    //设置消息状态
    private void setMsg(SysMsgViewHolder holder, SystemMsg msgBean) {
        String msg = ChatMsgUtil.sysMsgBrief(msgBean);
        String url = Utils.serverAddress + "retrieveusername?LinkdoodID=" + msgBean.getUserName();
        Utils.setNickname(context, url, msgBean.getUserID()+"", holder.dummy);
        String names = "";
        if (holder.dummy != null) {
            names = holder.dummy.getText().toString();
        }
        holder.tvMsg.setText(names + msg);
    }

    //请求消息操作
    private void msgOperate(final int position, SysMsgViewHolder viewHolder, final SystemMsg msgBean) {
        viewHolder.btAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseSysMsg(1, msgBean, position, "", "");
            }
        });
        viewHolder.btDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                responseSysMsg(2, msgBean, position, "", "");
            }
        });
    }

    private String getAvatar(final long targetID, final int position) {
        if (avatarMap == null) {
            avatarMap = new LongSparseArray<>();
        }
        if (avatarMap.indexOfKey(targetID) < 0) {
//            avatarMap.put(targetID, "");
            if (ChatMsgApi.isGroup(targetID)) {
                RequestHelper.getGroupInfo(targetID, new RequestCallBack<Group, Void, Void>() {
                    @Override
                    public void handleSuccess(Group group, Void aVoid, Void aVoid2) {
                        updateAvatar(targetID, position, group.getAvatar());
                    }
                });
            } else if (ChatMsgApi.isUser(targetID)) {
                RequestHelper.getContactInfo(targetID, new RequestCallBack<Contact, Void, Void>() {
                    @Override
                    public void handleSuccess(Contact contact, Void aVoid, Void aVoid2) {
                        updateAvatar(targetID, position, contact.getAvatar());
                    }
                });
            }
            return "";
        } else {
            return avatarMap.get(targetID);
        }
    }

    private void updateAvatar(long targetID, final int position, String avatar) {
        if (TextUtils.isEmpty(avatar)) {
            return;
        }
        avatarMap.put(targetID, avatar);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(position);
            }
        });
    }

    /**
     * 好友/群请求处理
     *
     * @param type     1 同意 2拒绝
     * @param msgBean
     * @param position
     */
    private void responseSysMsg(int type, SystemMsg msgBean, int position, String remark, String refuseReason) {
        if (type == 1) {//允许
            if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ) {
                list.get(position).setOprType(SysMsgService.OPT_BUDDY_PASS);
                RequestHelper.respToAddBuddy(msgBean.getUserID(), msgBean.getMsgID(), Constants.SYS_AGREE, remark, refuseReason, null);
            } else if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_REQ) {
                list.get(position).setOprType(SysMsgService.OPT_GROUP_PASS);
                RequestHelper.respToEnterGroup(msgBean.getGroupID(), msgBean.getMsgID(), Constants.SYS_AGREE, refuseReason, null);
            }
        } else if (type == 2) {//拒绝
            if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ) {
                list.get(position).setOprType(SysMsgService.OPT_BUDDY_REFUSE);
                RequestHelper.respToAddBuddy(msgBean.getUserID(), msgBean.getMsgID(), Constants.SYS_REFUSE, remark, refuseReason, null);
            } else if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_REQ) {
                list.get(position).setOprType(SysMsgService.OPT_GROUP_REFUSE);
                RequestHelper.respToEnterGroup(msgBean.getGroupID(), msgBean.getMsgID(), Constants.SYS_REFUSE, refuseReason, null);
            }
        }
        list.get(position).setIsResponse(SysMsgService.RESPONSE_OPT);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class SysMsgViewHolder extends BaseRecyclerViewHolder {

        private TextView tvCatalog;
        private TextView tvTime;
        private TextView tvName;
        private TextView tvMsg;
        private TextView dummy;
        private FrameLayout flOpt;
        private Button btAgree, btDeny;
        private TextView tvStatus;
        private ImageView imgIcon;
        private LinearLayout llOptBtn;

        public SysMsgViewHolder(View itemView) {
            super(itemView);
            tvCatalog = (TextView) itemView.findViewById(R.id.tv_item_catalog);
            tvTime = (TextView) itemView.findViewById(R.id.tv_item_time);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_nick);
            tvMsg = (TextView) itemView.findViewById(R.id.tv_item_sign);
            btAgree = (Button) itemView.findViewById(R.id.bt_item_agree);
            btDeny = (Button) itemView.findViewById(R.id.bt_item_deny);
            imgIcon = (ImageView) itemView.findViewById(R.id.img_item_icon);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_item_status);
            flOpt = (FrameLayout) itemView.findViewById(R.id.fl_item_opt);
            llOptBtn = (LinearLayout) itemView.findViewById(R.id.ll_item_opt_btn);
            dummy = (TextView)itemView.findViewById(R.id.dummy);
        }
    }
}

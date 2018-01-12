//package com.kpz.AnyChat.Chat;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.os.CountDownTimer;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.kpz.AnyChat.Others.ChatMsgUtil;
//import com.kpz.AnyChat.Others.DialogUtil;
//import com.kpz.AnyChat.Others.ItemDataChangeListener;
//import com.kpz.AnyChat.Others.OnReSendChatMsgListener;
//import com.kpz.AnyChat.Others.OptionBean;
//import com.kpz.AnyChat.Others.RequestHelper;
//import com.kpz.AnyChat.Others.ToastUtil;
//import com.kpz.AnyChat.Others.Utils;
//import com.kpz.AnyChat.R;
//import com.vrv.imsdk.VIMClient;
//import com.vrv.imsdk.chatbean.ChatMsg;
//import com.vrv.imsdk.chatbean.ChatMsgApi;
//import com.vrv.imsdk.chatbean.MsgAudio;
//import com.vrv.imsdk.chatbean.MsgCard;
//
//
//
///**
// * Created by Yang on 2015/8/15 015.
// */
//public abstract class ChatMessageView extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {
//
//    private final String TAG = ChatMessageView.class.getSimpleName();
//
//    protected Context context;
//    protected ChatMsg messageBean;
//    protected boolean showName = false;//显示名称
////    private boolean encrypt = false;//消息是否加密
////    private boolean readBurn = false;//是否为阅后即焚
//
//    protected OnReSendChatMsgListener reSendListener;
//
//    public void setReSendListener(OnReSendChatMsgListener listener) {
//        this.reSendListener = listener;
//    }
//
//    private MsgStatus status = MsgStatus.NORMAL;
//
//    public enum MsgStatus {
//        NORMAL,//正常消息
//        REMIND,//提醒消息
//        DELAY//延时消息
//    }
//
//    protected TextView tvFromName;//发消息人
//    protected LinearLayout llMsgStatusTime;//延时提醒消息时间
//    protected TextView tvMsgStatus;//延迟或提醒
//    protected TextView tvMsgStatusTime;//延时提醒消息时间
//    protected FrameLayout flMsg;//
//    protected ChatMsgItemView itemView;
//    protected ProgressBar proSend;//发送消息
//
//    protected boolean isBurn; //阅后即焚消息
//    protected boolean isPrivate;//私信消息
//
//    public ChatMessageView(Context context) {
//        super(context);
//        initView(context);
//    }
//
//    public ChatMessageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initView(context);
//    }
//
//    public ChatMessageView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView(context);
//    }
//
//    protected abstract View loadContentView();
//
//    private void initView(Context context) {
//        this.context = context;
//        View view = loadContentView();
//        findViews(view);
//
//    }
////
////    private void findViews(View view) {
////        //点击消息控件（包括所有的消息类型）
////        flMsg = (FrameLayout) view.findViewById(R.id.fl_chat_content);
////        //显示名称/昵称
////        tvFromName = (TextView) view.findViewById(R.id.tv_chat_fromName);
////        //消息活动类型（延迟。提醒）
////        llMsgStatusTime = (LinearLayout) view.findViewById(R.id.ll_chat_remindTime);
////        tvMsgStatusTime = (TextView) view.findViewById(R.id.tv_chat_delayOrRemind_time);
////        tvMsgStatus = (TextView) view.findViewById(R.id.tv_chat_delayOrRemind);
////        proSend = (ProgressBar) view.findViewById(R.id.progress_chat_send);
////    }
//
//    /**
//     * 设置消息展示
//     */
////    public void setViews(ChatMsg messageBean, boolean showName) {
////        this.messageBean = messageBean;
////        this.showName = showName;
////        boolean isMe = RequestHelper.isMyself(messageBean.getFromID());
////        this.isBurn = !isMe && ChatMsgUtil.isBurnMsg(messageBean);
////        this.isPrivate = !isMe && ChatMsgUtil.isPrivateMsg(messageBean);
////        setProSend();//设置未发送成功消息，只有自己发送显示
////        setName();
////        setBackground();
////        setMsgDisplay();
////        setListeners();
////    }
////
////    protected void setProSend() {
////        //在ChatMessageToView中设置
////    }
////
////    /**
////     * 显示聊天人名称
////     */
////    protected abstract void setName();
////
////    protected abstract void setBackground();
////
////    /**
////     * 设置消息延迟或提醒时间
////     */
////    private void setMsgStatus() {
////        if (status == MsgStatus.NORMAL) {
////            llMsgStatusTime.setVisibility(View.GONE);
////        } else {
////            llMsgStatusTime.setVisibility(View.VISIBLE);
////            tvMsgStatusTime.setText(DateTimeUtils.formatDateWeek(context, System.currentTimeMillis()));
////            if (status == MsgStatus.DELAY) {
////                tvMsgStatus.setText(R.string.vim_chat_delay_time);
////            } else if (status == MsgStatus.REMIND) {
////                tvMsgStatus.setText(R.string.vim_chat_remind_time);
////            }
////        }
////    }
////
////    /**
////     * 设置消息展示
////     */
////    private void setMsgDisplay() {
////        if (isPrivate && messageBean.getPrivateMsg() == 1) {//未解密
////            displayPrivate();
////        } else if (isBurn) {//私信消息解密后 判断是否是阅后即焚
////            displayBurn();
////        } else {
////            displayNormalMsg();
////        }
////    }
//
//    /**
//     * 显示正常消息
//     */
////    protected void displayNormalMsg() {
////        flMsg.removeAllViews();
////        itemView = ChatMsgItemFactory.createItemView(context, messageBean, this);
////        flMsg.addView(itemView);
////    }
////
////    //显示阅后即焚
////    protected void displayBurn() {
////        View burnView = LayoutInflater.from(context).inflate(R.layout.vim_view_chat_burn, null);
////        flMsg.removeAllViews();
////        flMsg.addView(burnView);
////    }
////
////    //显示私信
////    protected void displayPrivate() {
////        View privateView = LayoutInflater.from(context).inflate(R.layout.vim_view_chat_privacy, null);
////        flMsg.removeAllViews();
////        flMsg.addView(privateView);
////    }
//
//    @Override
//    public void onClick(View v) {
//        if (isPrivate && messageBean.getPrivateMsg() == 1) {
//            onClickPrivate();
//        } else if (isBurn) {
//            onclickBurnMsg();
//        } else {
//            if (itemListener != null) {
//                itemListener.onItemClick(messageBean);
//            }
//        }
//    }
//
//
////    private void onclickBurnMsg() {
////        int BURN_TIME = 15;//阅后即焚倒计时
////        if (messageBean instanceof MsgAudio) {
////            BURN_TIME = (int) (BURN_TIME + ((MsgAudio) messageBean).getMediaTime() / 1000);
////        } else if (messageBean instanceof MsgCard) {
////            if (messageBean != null) {
////                long userID = Long.valueOf(((MsgCard) messageBean).getMediaUrl());
////                ToastUtil.showShort("跳转到名片界面" + userID);
////                return;
////            }
////        }
////        View burnView = LayoutInflater.from(context).inflate(R.layout.vim_dialog_burn, null);
////        LinearLayout contentLayout = (LinearLayout) burnView.findViewById(R.id.layout_content);
////        final ProgressBar numberPrg = (ProgressBar) burnView.findViewById(R.id.progress_time);
////        numberPrg.setMax(BURN_TIME); // 设置为15s的倒计时
////        final TextView timeTxt = (TextView) burnView.findViewById(R.id.tv_time);
////        Button btnBurn = (Button) burnView.findViewById(R.id.btn_burn);
////        final MaterialDialog.Builder materialDialogBuilder = DialogUtil.buildCustomViewDialog(context, null, burnView, null, null, null, null, false);
////        materialDialogBuilder.showListener(new DialogInterface.OnShowListener() {
////            @Override
////            public void onShow(DialogInterface dialog) {
////
////            }
////        });
////        final MaterialDialog materialDialog = materialDialogBuilder.build();
////        itemView = ChatMsgItemFactory.createItemView(context, messageBean, this);
////        contentLayout.removeAllViews();
////        contentLayout.addView(itemView);
////        contentLayout.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                if (itemListener != null) {
////                    itemListener.onItemClick(messageBean); // todo: 图片显示
////                }
////            }
////        });
////        // 倒计时
////        final CountDownTimer countDownTimer = new CountDownTimer(BURN_TIME * 1000, 1000) {
////            @Override
////            public void onTick(long millisUntilFinished) {
////                if (materialDialog.isShowing()) {
////                    int remainingTime = (int) (millisUntilFinished / 1000);
////                    numberPrg.setProgress(remainingTime);
////                    timeTxt.setText(context.getString(R.string.vim_burn_time, String.valueOf(remainingTime)));
////                } else {
////                    cancel();
////                }
////            }
////
////            @Override
////            public void onFinish() {
////                if (materialDialog.isShowing()) {
////                    materialDialog.dismiss();
////                    itemView.burn();
////                }
////                ChatMsgUtil.deleteByMsg(messageBean);
////            }
////        };
////        if (contentLayout.isShown()) {
////            countDownTimer.start();
////        } else {
////            itemView.setListener(new ChatMsgItemListener() {
////                @Override
////                public void status(int status) {
////                    //消息加载显示到界面上开始倒计时
////                    if (status == ChatMsgItemListener.SHOW) {
////                        countDownTimer.start();
////                    }
////                }
////            });
////        }
////        btnBurn.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                materialDialog.dismiss();
////                countDownTimer.cancel();
////                itemView.burn();
////                ChatMsgUtil.deleteByMsg(messageBean);
////
////            }
////        });
////    }
////
////    private void onClickPrivate() {
////        ToastUtil.showShort(context.getString(R.string.vim_tip_privacy_error));
////    }
//
//    @Override
//    public boolean onLongClick(View view) {
//        if (itemListener != null) {
//            itemListener.onItemLongClick(messageBean);
//        }
//        return true;
//    }
//
//
//    //执行点击操作
//    public void exeClick() {
//        if (itemView != null) {
//            itemView.onClick();
//        }
//    }
//
//    //执行长按操作
//    public void exeLongClickexeLongClick() {
//        boolean isMe = RequestHelper.isMyself(messageBean.getFromID());
//        final String copy = VIMClient.getContext().getString(R.string.vim_action_copy);
//        final String forward = VIMClient.getContext().getString(R.string.vim_action_forward);
//        final String save = VIMClient.getContext().getString(R.string.vim_action_to_notepad);
//        final String export = VIMClient.getContext().getString(R.string.vim_export);
//        final String transferToTask = VIMClient.getContext().getString(R.string.vim_action_transfer_to_task);
//        final String delete = VIMClient.getContext().getString(R.string.vim_delete);
//        final String moreOptions = VIMClient.getContext().getString(R.string.vim_action_more);
//        final String recall = context.getString(R.string.vim_recall_msg);
//        final CharSequence[] items;
//        int msgType = messageBean.getMsgType();
//        boolean isPrivacyMsg = ChatMsgUtil.isPrivateMsg(messageBean);
//        boolean isTaskMsg = ChatMsgUtil.isTaskMsg(msgType);
//        boolean isDelayMsg = ChatMsgUtil.isDelayMsg(messageBean);
//        boolean isReceiptMsg = ChatMsgUtil.isReceiptMsg(messageBean);
//        boolean isBurnMsg = ChatMsgUtil.isBurnMsg(messageBean);
//        if (isBurnMsg) {
//            if (!RequestHelper.isMyself(messageBean.getFromID())) {
//                return;
//            }
//            items = new CharSequence[]{delete};
//        } else {
//            switch (msgType) {
//                case ChatMsgApi.TYPE_TEXT:
//                    if (!isMe || isTaskMsg || isDelayMsg || isReceiptMsg) {
//                        items = new CharSequence[]{copy, forward, /* transferToTask,*/ delete, save, moreOptions};
////                    } else if (isDeleteMsg || isFlashMsg || isPrivacyMsg) {
//                    } else if (isPrivacyMsg) {
//
//                        items = new CharSequence[]{delete, moreOptions};
//                    } else {
//                        items = new CharSequence[]{copy, forward,  /*transferToTask,*/ delete, recall, save, moreOptions};
//                    }
//                    break;
//                case ChatMsgApi.TYPE_IMAGE:
//                case ChatMsgApi.TYPE_FILE:
//                case ChatMsgApi.TYPE_MINI_VIDEO:
//                    if (!isMe || isPrivacyMsg) {
//                        items = new CharSequence[]{forward, export, delete, save, moreOptions};
//                    } else {
//                        items = new CharSequence[]{forward, export, delete, recall, save, moreOptions};
//                    }
//                    break;
//                case ChatMsgApi.TYPE_AUDIO:
//                case ChatMsgApi.TYPE_CARD:
//                case ChatMsgApi.TYPE_DYNAMIC:
//                case ChatMsgApi.TYPE_POSITION:
//                case ChatMsgApi.TYPE_WEB_LINK:
//                case ChatMsgApi.TYPE_CUSTOM_DYNAMIC:
//                    if (!isMe || isPrivacyMsg || isTaskMsg || isDelayMsg || isReceiptMsg) {
//                        items = new CharSequence[]{forward, delete, save, moreOptions};
//                    } else {
//                        items = new CharSequence[]{forward, delete, recall, save, moreOptions};
//                    }
//                    break;
//                case ChatMsgApi.TYPE_NEWS:
//                case ChatMsgApi.TYPE_TEMPL:
//                    if (!isMe || isPrivacyMsg || isTaskMsg || isDelayMsg || isReceiptMsg) {
//                        items = new CharSequence[]{forward, delete};
//                    } else {
//                        items = new CharSequence[]{forward, delete, recall};
//                    }
//                    break;
//                case ChatMsgApi.TYPE_MULTI:
//                case ChatMsgApi.TYPE_VIDEO:
//                case ChatMsgApi.TYPE_VOICE:
//                default:
//                    items = new CharSequence[]{delete, moreOptions};
//                    break;
//            }
//        }
//
//        final MaterialDialog.ListCallback itemOperateCallBack = new MaterialDialog.ListCallback() {
//            @Override
//            public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
//                String s = charSequence.toString();
//                if (s.equals(forward)) {
//                    itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_FORWARD, messageBean);
//                } else if (s.equals(save)) {
//                    itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_COLLECTION, messageBean);
//                } else if (s.equals(delete)) {
//                    itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_DELETE, messageBean);
//                } else if (s.equals(recall)) {
//                    itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_WITHDRAW, messageBean);
//                } else if (s.equals(moreOptions)) {
//                    itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_MORE, messageBean);
//                } else if (s.equals(copy)) {
//                    Utils.copyTxt(messageBean.getBody());
//                    ToastUtil.showShort("已复制到剪切板");
//                } else if (s.equals(transferToTask)) {// TODO:添加待执行操作
//
//                } else if (s.equals(export)) {//导出
//                    itemListener.onItemOperation(OptionBean.TYPE_OPTION_MSG_EXPORT, messageBean);
//                }
//            }
//        };
//        DialogUtil.buildOperateDialog(context, items, itemOperateCallBack).show();
//    }
//
//    protected void setListeners() {
//        itemView.setOnClickListener(this);
//        itemView.setOnLongClickListener(this);
//    }
//
//    //长按消息体，操作消息需要更新页面，控制adapter，notifyDataSetChanged；此处暂时只传递值，
//    protected ItemDataChangeListener itemListener;
//
//    public void setItemListener(ItemDataChangeListener listener) {
//        if (listener != null) {
//            this.itemListener = listener;
//        }
//    }
//}

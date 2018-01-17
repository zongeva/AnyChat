package com.kpz.AnyChat.Others;

import android.content.Context;
import android.text.TextUtils;

import com.kpz.AnyChat.R;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.VIMClient;
import com.vrv.imsdk.api.Constants;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.chatbean.ChatMsgBuilder;
import com.vrv.imsdk.chatbean.MsgCard;
import com.vrv.imsdk.chatbean.MsgCombine;
import com.vrv.imsdk.chatbean.MsgFile;
import com.vrv.imsdk.chatbean.MsgImg;
import com.vrv.imsdk.chatbean.MsgPosition;
import com.vrv.imsdk.chatbean.MsgTask;
import com.vrv.imsdk.chatbean.MsgText;
import com.vrv.imsdk.chatbean.MsgTip;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.Member;
import com.vrv.imsdk.model.SysMsgService;
import com.vrv.imsdk.model.SystemMsg;
import com.vrv.imsdk.util.JsonToolHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天消息工具类
 */
public class ChatMsgUtil {

    //////////////////////// 发送消息 ///////////////////////////////////////

    //发送文本消息
    public static void sendTxt(long targetID, String text, String msgPro, List<Long> relatedUsers, RequestCallBack callBack) {
        sendTxt(targetID, text, msgPro, relatedUsers, 0, callBack);
    }

    //发送文本消息
    public static void sendTxt(long targetID, String text, String msgPro, List<Long> relatedUsers, int fontSize, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.setRelatedUsers(relatedUsers);
        builder.setMsgProperties(msgPro);
        if (ChatMsgApi.isGroup(targetID)) {
            if (!relatedUsers.contains(targetID)) {
                builder.setLimitRange(relatedUsers);
            }
        }
        sendMsg(builder.createTxtMsg(text, fontSize), callBack);
    }

    /**
     * 最近消息会话中显示消息简要
     */
    public static String lastMsgBrief(Context context, Chat chat) {
        if (!TextUtils.isEmpty(chat.getMsgProperties()) && chat.getMsgProperties().contains("enVchat")) {
            return context.getString(R.string.privateMsg);
        }
        if (chat.getOprType() == 1) {
            return context.getString(R.string.burn);
        }
        int msgType = chat.getMsgType();
        String msg = chat.getLastMsg();
        if (TextUtils.isEmpty(msg) && TextUtils.isEmpty(chat.getMsgProperties())) {
            //删除消息之后还会返回消息类型
            msgType = ChatMsgApi.TYPE_TEXT;
        }
        switch (msgType) {
            case ChatMsgApi.TYPE_HTML:
                return context.getString(R.string.vim_html);
            case ChatMsgApi.TYPE_TEXT:
                String body = JsonToolHelper.parseTxtJson(msg);
//                if (body.contains(VrvExpressions.ORDER_DELETE_ALL) || body.contains(VrvExpressions.ORDER_DELETE_TODAY)) {
//                    return context.getString(R.string.order_eraser);
//                } else if (body.contains(VrvExpressions.ORDER_FLASH)) {
//                    return context.getString(R.string.order_shark);
//                }
                return body;
            case ChatMsgApi.TYPE_AUDIO:
                return context.getString(R.string.vim_audio);
            case ChatMsgApi.TYPE_POSITION:
                return context.getString(R.string.vim_position);
            case ChatMsgApi.TYPE_IMAGE_MULTI:
            case ChatMsgApi.TYPE_IMAGE:
                return context.getString(R.string.vim_image);
            case ChatMsgApi.TYPE_FILE:
                return context.getString(R.string.vim_file);
            case ChatMsgApi.TYPE_CARD:
                return context.getString(R.string.vim_card);
            case ChatMsgApi.TYPE_WEAK_HINT:
                String hint = JsonToolHelper.parseTxtJson(msg);
                return TextUtils.isEmpty(hint) ? context.getString(R.string.vim_weakHint) : hint;
            case ChatMsgApi.TYPE_RED_ENVELOPE:
                return context.getString(R.string.redPacket) + JsonToolHelper.parseTxtJson(msg);
            case ChatMsgApi.TYPE_INSTRUCTION:
                return context.getString(R.string.instruction);
            case ChatMsgApi.TYPE_MULTI:
                return context.getString(R.string.composite);
            case ChatMsgApi.TYPE_DYNAMIC:
                return context.getString(R.string.dynamic);
            case ChatMsgApi.TYPE_REVOKE:
                return context.getString(R.string.withdraw);
            case ChatMsgApi.TYPE_TASK:
                return context.getString(R.string.taskMsg);
            case ChatMsgApi.TYPE_NEWS:
            case ChatMsgApi.TYPE_WEB_LINK:
                return context.getString(R.string.htmlMsg);
            case ChatMsgApi.TYPE_TEMPL:
                return context.getString(R.string.templateMsg);
            case ChatMsgApi.TYPE_MINI_VIDEO:
                return context.getString(R.string.typeSmallvedio);
            case ChatMsgApi.TYPE_VIDEO:
            case ChatMsgApi.TYPE_VOICE:
                return context.getString(R.string.typeVideo);
//            case ChatMsgApi.TYPE_CUSTOM_DYNAMIC:
//                return parseCustomDynamic(context, msg);
            default:
                return context.getString(R.string.vim_unKnownMsg) + msgType;
        }
    }

    public static String sysMsgBrief(SystemMsg msgBean) {

        if (msgBean == null) return "";
        String msg = "";
        if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_REQ) {
            msg = " Request To Add Friend";
        } else if (msgBean.getMsgType() == SysMsgService.TYPE_BUDDY_RSP) {
            switch (msgBean.getOprType()) {
                case SysMsgService.OPT_BUDDY_PASS:
                    msg = " Approve Your Request";
                    break;
                case SysMsgService.OPT_BUDDY_REFUSE:
                    msg = " Deny Your Reqeust";
                    break;
                case SysMsgService.OPT_BUDDY_REFUSE_FOREVER:
                    msg = " Deny Your Request Permanantly";
                    break;
            }
        } else if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_REQ) {
            msg = " Request To Join Group";
        } else if (msgBean.getMsgType() == SysMsgService.TYPE_GROUP_RSP) {
            switch (msgBean.getOprType()) {
                case SysMsgService.OPT_GROUP_IGNORE:
                    break;
                case SysMsgService.OPT_GROUP_PASS:
                    msg = " Approve Your Request";
                    break;
                case SysMsgService.OPT_GROUP_REFUSE:
                    msg = " Deny Your Reqeust";
                    break;
                case SysMsgService.OPT_GROUP_REFUSE_FOREVER:
                    msg = " Deny Your Request Permanantly";
                    break;
                case SysMsgService.OPT_GROUP_DELETE:
                    msg = " Delete The Group";
                    break;
                case SysMsgService.OPT_GROUP_EXIT:
                    msg = " Leave The Group";
                    break;
                case SysMsgService.OPT_GROUP_REMOVE:
                    msg = " Remove You From The Group";
                    break;
            }
        }

        return  msg;
    }

//    /**
//     * 解析自定义表情 的 meaning
//     */
//    private static String parseCustomDynamic(Context context, String json) {
//        try {
//            JSONObject object = new JSONObject(json);
//            return "[" + object.get("meaning").toString() + "]";
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return context.getString(R.string.dynamic);
//        }
//    }

    //判断是否是二维码指令消息
    public static boolean isQrCodeMsg(String msgProperties) {
        if (TextUtils.isEmpty(msgProperties)) {
            return false;
        }
        try {
            JSONObject object = new JSONObject(msgProperties);
            return object.getBoolean(Constants.VRVISQR);
        } catch (Exception e) {
//            Log.e(e.toString());
        }
        return false;
    }

    //判断是否是阅后即焚消息
    public static boolean isBurnMsg(ChatMsg msg) {
        //1为阅后即焚消息
        return msg.getActiveType() == 1;
    }

    //判断是否为私信消息
    public static boolean isPrivateMsg(ChatMsg msg) {
        return msg.isPrivateMsg();
    }

    public static boolean isTaskMsg(int msgType) {
        return msgType == ChatMsgApi.TYPE_TASK;
    }

    public static <T extends ChatMsg> boolean isDelayMsg(T msg) {
        switch (msg.getMsgType()) {
            case ChatMsgApi.TYPE_TEXT:
                return ((MsgText) msg).isDelay();
            case ChatMsgApi.TYPE_IMAGE:
                return ((MsgImg) msg).isDelay();
            case ChatMsgApi.TYPE_FILE:
                return ((MsgFile) msg).isDelay();
            case ChatMsgApi.TYPE_POSITION:
                return ((MsgPosition) msg).isDelay();
            case ChatMsgApi.TYPE_CARD:
                return ((MsgCard) msg).isDelay();
            default:
                return false;
        }
    }

    public static <T extends ChatMsg> boolean isReceiptMsg(T msg) {
        if (msg.getMsgType() == ChatMsgApi.TYPE_TEXT) {
            return ((MsgText) msg).isReceipt();
        } else {
            return false;
        }
    }

//    public static <T extends ChatMsg> boolean isDeleteMsg(T msg) {
//        if (msg.getMsgType() == ChatMsgApi.TYPE_TEXT) {
//            String body = msg.getBody();
//            if (body.contains(VrvExpressions.ORDER_DELETE_ALL) || body.contains(VrvExpressions.ORDER_DELETE_TODAY)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public static <T extends ChatMsg> boolean isFlashMsg(T msg) {
//        if (msg.getMsgType() == ChatMsgApi.TYPE_TEXT) {
//            String body = msg.getBody();
//            if (body.equals(VrvExpressions.ORDER_FLASH)) {
//                return true;
//            }
//        }
//        return false;
//    }

//    public static boolean isContainsFlashMsg(String msg) {
//        return msg.contains(VrvExpressions.ORDER_FLASH);
//    }

    /**
     * 消息是否已处理
     *
     * @param msg
     * @return false 未处理  true 已处理
     */
    public static boolean isDeal(ChatMsg msg) {
        return msg.isDeal();
    }


    /**
     * 解析弱提示消息
     */
    public static String parseWeakHint(Context context, MsgTip msg) {
        if (msg != null) {
            int tipType = msg.getTipType();    //tip 类型
            int oprType = msg.getOprType();    //操作类型
            String tipTime = msg.getTipTime(); //时间
            String oprUser = msg.getOprUser(); //操作userId
            String userInfo = msg.getUserInfo();//用户信息
            String fileInfo = msg.getFileInfo();///<文件信息
            switch (tipType) {
                case MsgTip.TYPE_GROUP: //群弱提示
                    if (oprType == 0) {
                        return context.getString(R.string.vim_hint_addGroup, userInfo);
                    } else if (oprType == 1) {
                        return context.getString(R.string.vim_hint_invite, oprUser, userInfo);
                    } else if (oprType == 2) {
                        return context.getString(R.string.vim_hint_agree, oprUser, userInfo);
                    } else if (oprType == 3) {
                        return context.getString(R.string.vim_hint_exit, userInfo);
                    } else if (oprType == 4) {
                        return context.getString(R.string.vim_hint_remove, userInfo, oprUser);
                    }
                    break;
                case MsgTip.TYPE_RECEIPT: //阅后回执
                    //我响应阅后回执
                    if (RequestHelper.isMyself(msg.getFromID())) {
                        return context.getString(R.string.vim_hint_receipt_auto, userInfo);
                    } else {
                        //别人响应我的阅后回执
                        return context.getString(R.string.vim_hint_receipt_read, oprUser);
                    }
                case MsgTip.TYPE_DELETE: //橡皮擦
                    if (RequestHelper.isMyself(msg.getFromID())) {
                        return context.getString(R.string.vim_hint_delete_self, oprType == 1 ? context.getString(R.string.vim_accept) : context.getString(R.string.vim_deny), userInfo);
                    } else {
                        return context.getString(R.string.vim_hint_delete_other, oprUser, oprType == 1 ? context.getString(R.string.vim_accept) : context.getString(R.string.vim_deny));
                    }
                case MsgTip.TYPE_SHAKE: //抖一抖
                    if (!RequestHelper.isMyself(msg.getFromID())) {
                        return context.getString(R.string.vim_hint_shark_other, oprUser);
                    } else {
                        return context.getString(R.string.vim_hint_shark_self, userInfo);
                    }
                case MsgTip.TYPE_RED_BOX: //红包
                    if (RequestHelper.isMyself(msg.getFromID())) {
                        if (TextUtils.isEmpty(tipTime)) {
                            return context.getString(R.string.vim_hint_redPacket_self, oprUser);
                        } else {
                            return context.getString(R.string.vim_hint_redPacket_self_done, tipTime);
                        }
                    } else if (RequestHelper.isMyself(msg.getFromID())) {
                        return context.getString(R.string.vim_hint_redPacket_other, userInfo);
                    }
                    break;
                case MsgTip.TYPE_FILE: //接收文件若提示
                    return context.getString(R.string.receive_file_success, fileInfo);
                default:
                    return VIMClient.getContext().getString(R.string.weak_tips);
            }
        }
        return "";
    }

    /**
     * 解析撤回消息 msgProperties消息 ,通知时需要调用
     */
    public static long parseWithdrawMsgProperties(String json) {
        if (TextUtils.isEmpty(json))
            return 0;
        try {
            JSONObject object = new JSONObject(json);
            return object.getLong("messageID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 更新消息
     *
     * @param chatMsg  更改后的完整消息
     * @param callBack
     */
    public static void updateMsg(ChatMsg chatMsg, RequestCallBack callBack) {
        RequestHelper.updateMsg(chatMsg, callBack);
    }


    /**
     * 发送弱提示
     *
     * @param targetID
     * @param tipType  类型
     * @param oprType  操作类型
     * @param msgBean
     * @param callBack
     * @return
     */
    public static void sendPromptMsg(long targetID, int tipType, int oprType, ChatMsg msgBean, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.setLimitRange(msgBean.getLimitRange());
        builder.setRelatedUsers(msgBean.getLimitRange());
        String fromName = msgBean.getName();
        if (TextUtils.isEmpty(fromName)) {
            Member fromInfo = ClientManager.getDefault().getMemberService().findByID(msgBean.getFromID());
            if (fromInfo != null) {
                fromName = fromInfo.getName();
            }
        }
        sendMsg(builder.createPromptMsg(tipType, oprType, DateTimeUtils.timeStamp2Date(msgBean.getTime(), 10), RequestHelper.getMainAccount().getName(), fromName, ""), callBack);
    }

    /**
     * 发送撤回消息命令
     *
     * @param targetID
     * @param body        撤回消息发起人昵称
     * @param revokeMsgId 撤回消息id
     * @param callBack
     * @return
     */
    public static void sendWithdraw(long targetID, String body, long revokeMsgId, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.setBody(body);
        sendMsg(builder.createWithdrawMsg(revokeMsgId), callBack);
    }

    /**
     * 发送图文网页
     *
     * @param targetID
     * @param title
     * @param picUrl
     * @param url
     * @param description
     * @param callBack
     * @return
     */
    public static void sendWeb(long targetID, String title, String picUrl, String url, String description, List<Long> relatedUsers, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        builder.setRelatedUsers(relatedUsers);
        sendMsg(builder.createWebMsg(title, picUrl, url, description), callBack);
    }


    /**
     * @param targetID
     * @param imgPath
     * @param imgDesc  图片说明
     * @param callBack
     */
    public static void sendImg(long targetID, String imgPath, String imgDesc, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createImageMsg(imgPath, imgDesc, true), callBack);
    }

    //9图消息
    public static void sendNineImg(long targetID, List<String> imgPaths, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createNineImageMsg(imgPaths, "", true), callBack);
    }

    //发送微视频消息
    public static void sendMiniVideo(long targetID, byte brustFlag, String filePath, long height, long width, long length, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createMiniVideoMsg(targetID, brustFlag, filePath, height, width, length), callBack);
    }

    //发送二维码图片
    public static void sendQRImg(long targetID, String imgPath, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createImageMsg(imgPath, "", false), callBack);
    }

    //发送文件
    public static void sendFile(long targetID, String filePath, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createFileMsg(filePath), callBack);
    }

    //发送名片
    public static void sendCard(long targetID, long userID, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createCardMsg(userID), callBack);
    }

    //发送位置
    public static void sendPosition(long targetID, String address, double latitude, double longitude, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createPositionMsg(address, latitude, longitude), callBack);
    }

    /**
     * 发送语音
     *
     * @param targetID
     * @param audioPath
     * @param millisecond 毫秒
     * @param callBack
     * @return
     */
    public static void sendAudio(long targetID, String audioPath, long millisecond, RequestCallBack callBack) {
        if (millisecond < 1000) {
            ToastUtil.showShort("语音时间太短");
            return;
        }
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createAudioMsg(audioPath, millisecond), callBack);
    }

    /**
     * 发送动态表情
     *
     * @param targetID
     * @param expression
     * @param callBack
     * @return
     */
    public static void sendDynamic(long targetID, String expression, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createDynamicExpressionMsg(expression), callBack);
    }

    /**
     * @param targetID
     * @param code
     * @param emojiPath
     * @param mdCode
     * @param meaning
     * @param dyType
     * @param textSize
     * @param callBack
     */
    public static void sendCustomDynamic(long targetID, String code, String emojiPath, String mdCode, String meaning, byte dyType, int textSize, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        sendMsg(builder.createDynamicExpression2Msg(code, emojiPath, mdCode, meaning, dyType, textSize), callBack);
    }

    /**
     * 发送任务消息
     *
     * @param targetID
     * @param msg
     * @param timeZone 时区
     * @param timeTask timeTask： yyyyMMddHHmmssS 例如：201609221430256
     * @param isFinish 0：未完成，1：完成
     * @param isTask   0：非任务，1：任务
     * @param callBack
     * @return
     */
    public static void sendTask(long targetID, String msg, byte timeZone, String timeTask, boolean isFinish, boolean isTask, boolean isRead, List<Long> relatedUsers, RequestCallBack callBack) {
        ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
        MsgTask task = builder.createTaskMsg(msg, timeTask, isFinish, isTask);
        task.setTimeZone(timeZone);
        task.setRelatedUsers(relatedUsers);
        sendMsg(task, callBack);
    }

    /**
     * 发送组合消息 (转发消息)
     *
     * @param targetID
     * @param list
     * @param isCombineMsg 是否是组合消息
     * @param callBack
     */
    public static void transferMsg(long targetID, List<ChatMsg> list, boolean isCombineMsg, RequestCallBack callBack) {
        if (list == null || list.size() <= 0)
            return;
        if (!isCombineMsg) {
            RequestHelper.transferMsg(targetID, list.get(0), callBack);
        } else {
            ChatMsgBuilder builder = new ChatMsgBuilder(targetID);
            MsgCombine combine = builder.createCombineMsg(list);
            sendMsg(combine, callBack);
        }
    }

    private static <T extends ChatMsg> void sendMsg(T chatMsg, RequestCallBack callBack) {
        if (chatMsg == null) {
            ToastUtil.showShort("发送的消息异常");
            return;
        }
        RequestHelper.sendMsg(chatMsg, callBack);
    }

    //重发失败消息
    public static void reSend(ChatMsg chatMsg, RequestCallBack callBack) {
        RequestHelper.sendMsg(chatMsg, callBack);
    }

    private static boolean isBurn;//阅后即焚
    private static boolean isOneBurn;//一次阅后即焚
    private static List<Long> limitRange = new ArrayList<>();
    private static boolean privateChat = false;
    private static int orderType;// 指令消息的类型
    private static boolean isQrCode;
    private static long delayTime;

    public static void setIsQrCode(boolean isQrCode) {
        ChatMsgUtil.isQrCode = isQrCode;
    }

    public static void setOrderType(int orderType) {
        ChatMsgUtil.orderType = orderType;
    }

    public static void setDelayTime(long delayTime) {
        ChatMsgUtil.delayTime = delayTime;
    }

    /**
     * 设置指令阅后即焚
     */
    public static void setOneBurn(boolean burn) {
        if (burn) {
            isBurn = false;
        }
        isOneBurn = burn;
    }

    //设置阅后即焚
    public static void setBurn(boolean burn) {
        isBurn = burn;
        if (burn) {
            isOneBurn = false;
        }
    }

    /**
     * @return 是否是指令阅后即焚
     */
    public static boolean isOneBurn() {
        return isOneBurn;
    }

    public static boolean isBurn() {
        return isBurn;
    }

    //设置私信
    public static void setPrivate(boolean privateChat) {
        ChatMsgUtil.privateChat = privateChat;
    }

    //当前聊天模式是否为私聊模式
    public static boolean isPrivate() {
        return privateChat;
    }


    //NOTE: 聊天界面退出时需要调用
    public static void optionReset() {
        isBurn = isOneBurn = false;
        orderType = 0;
        limitRange.clear();
        privateChat = false;
        delayTime = 0;
        msgChangeListener = null;
    }


    //消息更改监听
    private static MsgChangeListener msgChangeListener;

    public static void setMsgChangeListener(MsgChangeListener changeListener) {
        if (null != changeListener) {
            msgChangeListener = changeListener;
        }
    }

    //通知界面删除聊天消息
    private static void notifyMsgDelete(List<Long> msgIDs) {

    }


    /**
     * 聊天界面消息更改监听
     */
    public interface MsgChangeListener {
        /**
         * @param msgIDs 为 NULL 表示全部清空
         */
        void onDelete(List<Long> msgIDs);
    }

    /**
     * 删除消息，删除数据库
     */
    public static void deleteByMsg(ChatMsg msg) {
        if (null == msg) {
            return;
        }
        final List<Long> msgIDs = new ArrayList<>();
        msgIDs.add(msg.getMsgID());
        RequestHelper.deleteMsgByID(msg.getTargetID(), msgIDs, new RequestCallBack() {
            @Override
            public void handleSuccess(Object o, Object o2, Object o3) {
                notifyMsgDelete(msgIDs);
            }
        });
    }
//
//    //处理emoji表情显示
//    public static void handleEmojiMsg(Context context, String msg, TextView textView) {
//        boolean expressions = ExpressionsParser.getInstance().isContainsExpressions(msg);
//        if (expressions) {
//            textView.setText(ExpressionsParser.getInstance().expression2String(msg, context, DisplayUtils.getTextViewSize(textView)));
//        } else {
//            textView.setText(Html.fromHtml(msg));
//        }
}


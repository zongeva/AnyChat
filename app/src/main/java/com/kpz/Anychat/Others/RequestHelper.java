package com.kpz.Anychat.Others;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kpz.Anychat.Others.RequestCallBack;
import com.vrv.imsdk.ClientManager;
import com.vrv.imsdk.bean.ContactVerifyType;
import com.vrv.imsdk.bean.DownloadFileProperty;
import com.vrv.imsdk.bean.Emoticon;
import com.vrv.imsdk.bean.EmoticonPackage;
import com.vrv.imsdk.bean.EmoticonResult;
import com.vrv.imsdk.bean.EntAppInfo;
import com.vrv.imsdk.bean.FileInfo;
import com.vrv.imsdk.bean.GroupUpdate;
import com.vrv.imsdk.bean.LocalSetting;
import com.vrv.imsdk.bean.LoginInfo;
import com.vrv.imsdk.bean.MsgDetailSearchProperty;
import com.vrv.imsdk.bean.MsgDetailSearchResult;
import com.vrv.imsdk.bean.MsgSearchProperty;
import com.vrv.imsdk.bean.MsgSearchResult;
import com.vrv.imsdk.bean.OnlineState;
import com.vrv.imsdk.bean.P2PUser;
import com.vrv.imsdk.bean.PageQueryEmoticon;
import com.vrv.imsdk.bean.PersonalData;
import com.vrv.imsdk.bean.PhoneBookContact;
import com.vrv.imsdk.bean.QueryMarketApplication;
import com.vrv.imsdk.bean.RecommendContact;
import com.vrv.imsdk.bean.SearchResult;
import com.vrv.imsdk.bean.SmallMarketAppInfo;
import com.vrv.imsdk.bean.SmallMarketAppPage;
import com.vrv.imsdk.bean.TransferLocalData;
import com.vrv.imsdk.bean.UserSetting;
import com.vrv.imsdk.chatbean.ChatMsg;
import com.vrv.imsdk.chatbean.ChatMsgApi;
import com.vrv.imsdk.chatbean.MsgImg;
import com.vrv.imsdk.extbean.EnterpriseUserInfo;
import com.vrv.imsdk.extbean.EnterpriseUserQueryInfo;
import com.vrv.imsdk.extbean.NoteInfo;
import com.vrv.imsdk.extbean.OrganizationInfo;
import com.vrv.imsdk.extbean.Room;
import com.vrv.imsdk.extbean.SearchNoteInfo;
import com.vrv.imsdk.extbean.Task;
import com.vrv.imsdk.model.Account;
import com.vrv.imsdk.model.AuthService;
import com.vrv.imsdk.model.Chat;
import com.vrv.imsdk.model.Contact;
import com.vrv.imsdk.model.Group;
import com.vrv.imsdk.model.Member;
import com.vrv.imsdk.model.ResultCallBack;
import com.vrv.imsdk.model.SystemMsg;
import com.vrv.imsdk.model.TinyGroup;
import com.vrv.imsdk.model.User;
import com.vrv.imsdk.util.JsonToolHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求统一管理入口
 */
public class RequestHelper {
    public final int MSG_OFFSET = 15; //每次查询的消息数量

    public boolean canGetHistory = true;
    private List<ChatMsg> chatMsgList = new ArrayList<>();
    private Map<Long, Boolean> canShowTimerMap = new HashMap<>();



    //*****************Account**********************//
    public static void resend (final long toID, final long fromID , final long fromMsgID){
        final List<Long> longList = java.util.Arrays.asList(fromMsgID);

        ClientManager.getDefault().getChatService().getMessages(fromID, fromMsgID, 1, 0, new ResultCallBack<Long, List<ChatMsg>, Void>() {

            @Override
            public void onSuccess(Long aLong, final List<ChatMsg> chatMsgs, Void aVoid) {

                RequestHelper.transferMsg(toID, chatMsgs.get(0), new ResultCallBack<Void, Void, Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Void aVoid2, Void aVoid3) {

                        Log.e("ResendStatus","Success");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e("ResendStatus","Fail " + s);
                    }
                });


            }

            @Override
            public void onError(int i, String s) {

            }
        });


    }

    public static Account getMainAccount() {
        return ClientManager.getDefault().getAccountService().getCurrent();
    }

    public static long getUserID() {
        return ClientManager.getDefault().getAccountService().getUserID();
    }

    public static boolean isMyself(long targetID) {
        return ClientManager.getDefault().getAccountService().isMySelf(targetID);
    }

    public static List<Account> getChildAccount() {
        return ClientManager.getDefault().getAccountService().getChild();
    }

    public static boolean hasChild() {
        return ClientManager.getDefault().getAccountService().hasChild();
    }

    //输入密码登录子账号
    public static void loginChild(AuthService service, String account, String password, String server, byte userType, RequestCallBack<Long, Void, Void> callBack) {
        service.login(userType, account, password, server, callBack);
    }

    //自动登录子账号
    public static void autoLoginChild(AuthService service, long userID, String server, RequestCallBack<Long, Void, Void> callBack) {
        service.autoLogin(userID, server, callBack);
    }

    //退出子账号
    public static void logoutChild(AuthService service, RequestCallBack<Void, Void, Void> callBack) {
        service.logout(callBack);
    }

    //移除子账号
    public static void removeChild(long userID) {
        ClientManager.deleteClientByID(userID);
    }

    //切换子账号
    public static void switchChild(Activity activity, long userID) {
        ClientManager.switchOver(userID);
//        MainActivity.start(activity, true);
    }

    //***************************Auth***********************************//

    public static LoginInfo getLastLoginInfo() {
        return ClientManager.getDefault().getAuthService().getLastLoginInfo();
    }

    /**
     * 获取注册验证码
     *
     * @param server   服务器地址，域名或IP均可
     * @param account  注册账号 如果是手机账户格式为“0086158********”
     * @param callBack 超时，下次请求间隔
     */
    public static void getRegCode(String server, String account, RequestCallBack<Integer, Void, Void> callBack) {
        ClientManager.getDefault().getAuthService().getRegCode(AuthService.TYPE_PHONE, server, account, callBack);
    }

    /**
     * 注册用户
     *
     * @param regCode  服务器地址，域名或IP均可
     * @param user     用户名，建议使用真实姓名
     * @param pwd      注册密码
     * @param callBack
     */
    public static void signUp(String regCode, String user, String pwd, RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().signUp(regCode, user, pwd, callBack);
    }

    /**
     * 获取密码复杂度
     *
     * @param callBack 接收结果回调 密码规则:
     *                 高8位代表最小长度
     *                 低8位，按照最低位开始，依次代表:(1代表必须，0 表示可选)
     *                 1. 是否必须有数字
     *                 2. 是否必须有小写字母
     *                 3. 是否必须有大写字母
     *                 4. 是否必须有英文字母
     *                 5. 是否必须有字符(特殊字符)
     *                 6. 是否允许注册(1允许，0不允许)
     */
    public static void getPasswordRule(RequestCallBack<Integer, Void, Void> callBack) {
        ClientManager.getDefault().getAuthService().getPasswordRule(callBack);
    }

    /**
     * 获取重置密码验证码 （找回密码）
     *
     * @param server
     * @param account
     * @param callBack 超时，下次请求间隔
     */
    public static void getResetPasswordCode(String server, String account, RequestCallBack<Integer, Void, Void> callBack) {
        ClientManager.getDefault().getAuthService().getResetPasswordCode(AuthService.TYPE_PHONE, server, account, callBack);
    }

    /**
     * 重置密码 （找回密码）
     *
     * @param code
     * @param password
     * @param callBack
     */
    public static void resetPassword(String code, String password, RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().resetPassword(code, password, callBack);
    }

    /**
     * 预登录服务器
     *
     * @param server
     * @return
     */
    public static String preLogin(String server) {
        return ClientManager.getDefault().getAuthService().getPreLogin(server);
    }

    /**
     * 手机登录
     *
     * @param user   用户名
     * @param pwd    密码
     * @param server 服务器地址
     * @return
     */
    public static long login(byte userType, String user, String pwd, String server, RequestCallBack<Long, Void, Void> callBack) {
        //TODO 添加国家码,userType
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(server)) {
            return -1;
        }
        return ClientManager.getDefault().getAuthService().login(userType, user, pwd, server, callBack);
    }

    /**
     * 自动登录（不用填密码）
     *
     * @param userID   用户ID
     * @param server   服务器地址，域名或IP均可
     * @param callBack 用户ID
     * @return :	 返回当前执行的操作ID，用于取消该次执行
     */
    public static long autoLogin(long userID, String server, RequestCallBack<Long, Void, Void> callBack) {
        return ClientManager.getDefault().getAuthService().autoLogin(userID, server, callBack);
    }

//    /**
//     * 离线登录
//     *
//     * @param userID   用户id如果为零，获取最近一次登录成功的用户id
//     * @param pwd      密码
//     * @param callBack
//     */
//    public static void offlineLogin(long userID, String pwd, RequestCallBack callBack) {
//        ClientManager.getDefault().getAuthService().offlineLogin(userID, pwd, callBack);
//    }

    /**
     * 登出
     *
     * @param callBack
     */
    public static void logout(RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().logout(callBack);
    }

    /**
     * 取消登录
     *
     * @param operateID 对应登录操作的ID
     * @param callBack
     */
    public static void abortLogin(long operateID, RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().abortLogin(operateID, callBack);
    }

    /**
     * 验证登录验证码或者获取下一张验证码
     *
     * @param account  登录账号
     * @param code     验证码内容 code为空则获取下一张验证码
     * @param callBack 下一张验证图片
     */
    public static void verifyImgCode(byte userType, String account, String code, RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getAuthService().verifyImgCode(userType, account, code, callBack);
    }

    /**
     * 获取安全中心URL
     *
     * @param server   企业服务器
     * @param callBack url
     **/
    public static void getSecUrl(String server, RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getAuthService().getSecUrl(server, callBack);
    }

    /**
     * 修改密码
     *
     * @param oldPw    旧密码
     * @param newPw    新密码
     * @param callBack
     **/
    public static void changePassword(String oldPw, String newPw, RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().changePassword(oldPw, newPw, callBack);
    }

    /**
     * 获取 clientKEY
     *
     * @param callBack clientKey
     **/
    public static void getClientKey(RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getAuthService().getClientKey(callBack);
    }

    /**
     * 获取绑定手机验证码
     *
     * @param phone    手机号
     * @param language 语言
     * @param callBack 超时重发时间,注册ID
     **/
    public static void getBindPhoneCode(String phone, String language, RequestCallBack<Integer, Long, Void> callBack) {
        ClientManager.getDefault().getAuthService().getBindPhoneCode(phone, language, callBack);
    }

    /**
     * 通过获取的验证码绑定手机
     *
     * @param phone      手机号
     * @param code       验证码
     * @param registryID 获取验证码时返回的ID
     * @param callBack   code :100参数不正确 120账号不存在 381未指定接受者  382未指定接收者类型
     *                   383验证码已过期 384验证码不正确  385发送间隔时间太短 386发送失败  387：未发送过验证码  510帐号已被使用
     **/
    public static void bindPhone(String phone, String code, long registryID, RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().bindPhone(phone, code, registryID, callBack);
    }

    /**
     * 绑定邮箱
     *
     * @param mail     邮箱号
     * @param callBack
     */
    public static void bindMail(String mail, RequestCallBack callBack) {
        ClientManager.getDefault().getAuthService().bindMail(mail, callBack);
    }

    //****************************Chat***********************************//

    /**
     * 获取最近联系人列表
     *
     * @return
     */
    public static List<Chat> getChatList() {
        return ClientManager.getDefault().getChatService().getList();
    }

    /**
     * 添加会话（保存草稿时没有最近联系人对象使用）
     *
     * @param targetID
     * @param callBack
     */
    public static void addChat(long targetID, RequestCallBack callBack) {
        Chat chat = null;
        if (ChatMsgApi.isGroup(targetID)) {
            Group group = RequestHelper.getGroupInfo(targetID);
            if (group != null) {
                chat = Chat.tinyGroup2Item(group.getInfo());
            }
        } else if (isBuddy(targetID)) {
            Contact contact = RequestHelper.getContactInfo(targetID);
            chat = Chat.contact2Item(contact);
        } else {
            return;
        }
        addChat(chat, callBack);
    }

    public static void addChat(Chat chat, RequestCallBack callBack) {
        if (chat == null || chat.getID() == 0) {
            return;
        }
        ClientManager.getDefault().getChatService().add(chat, callBack);
    }

    /**
     * 移除会话
     *
     * @param targetID 会话对应的ID，群或者人
     * @param callBack 回调
     */
    public static void removeChat(long targetID, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().remove(targetID, callBack);
    }

    /**
     * 发送消息，返回localID
     *
     * @param msg      消息
     * @param callBack 发送时间,消息ID
     */
    public static <T extends ChatMsg> void sendMsg(T msg, RequestCallBack<Void, Void, Void> callBack) {
        if (callBack == null)
            callBack = new RequestCallBack<Void, Void, Void>() {
                @Override
                public void handleSuccess(Void aVoid, Void aVoid2, Void aVoid3) {

                }
            };
        RequestCallBack<Integer, Integer, String> stateCallback = new RequestCallBack<Integer, Integer, String>() {
            @Override
            public void handleSuccess(Integer integer, Integer integer2, String s) {

            }
        };
        ClientManager.getDefault().getChatService().sendMsg(msg, callBack, stateCallback);
    }

    /**
     * 转发消息
     *
     * @param targetID
     * @param msg
     * @param callBack
     * @param <T>
     */
    public static <T extends ChatMsg> void transferMsg(long targetID, T msg, ResultCallBack<Void, Void, Void> callBack) {
        ClientManager.getDefault().getChatService().transferMsg(targetID, msg, callBack);
    }

    /**
     * 发送隐藏消息（不存储数据库）
     *
     * @param msg
     * @param callBack
     * @param <T>
     */
    public static <T extends ChatMsg> void sendHiddenMsg(T msg, ResultCallBack<Void, Void, Void> callBack) {
        ClientManager.getDefault().getChatService().sendHiddenMsg(msg, callBack);
    }

    /**
     * 删除所有消息
     *
     * @param callBack
     */
    public static void deleteAllMsg(boolean clearChatList, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().deleteAllMsg(clearChatList, callBack);
    }

    /**
     * 通过msgID删除消息
     *
     * @param targetID 会话对应的ID，群或者人
     * @param msgIDs   要删除的消息ID集合 msgIDs为空，清空对应targetID的所有消息
     * @param callBack
     */
    public static void deleteMsgByID(long targetID, List<Long> msgIDs, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().deleteMsgByID(targetID, msgIDs, callBack);
    }

    /**
     * 删除时间段消息
     *
     * @param targetID  会话对应的ID，群或者人 targetID为0，则针对所有用户
     * @param beginTime 起始时间
     * @param endTime   结束时间
     * @param callBack
     */
    public static void deleteMsgByTime(long targetID, long beginTime, long endTime, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().deleteMsgByTime(targetID, beginTime, endTime, callBack);
    }

    /**
     * 设置消息已读
     *
     * @param targetID 会话对应的ID，群或者人
     * @param msgID    要删除的消息ID集合
     */
    public static void setMsgRead(long targetID, long msgID) {
        ClientManager.getDefault().getChatService().setMsgRead(targetID, msgID);
    }

    /**
     * 设置消息未读
     *
     * @param targetID 会话对应的ID，群或者人
     */
    public static void setMsgUnread(long targetID, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().setMsgUnread(targetID, callBack);
    }

    /**
     * 获取消息
     *
     * @param targetID 会话对应的ID，群或者人
     * @param msgID    查询消息的起始ID，将从该消息的下一条消息开始查询
     * @param count    查询消息总数
     * @param flag     上一页还是下一页 向上偏移 0；向下偏移 1
     * @param callBack 会话ID，消息集合
     */
    public static void getMessages(long targetID, long msgID, int count, int flag, long sendUserID, RequestCallBack<Long, List<ChatMsg>, Void> callBack) {
        if (sendUserID <= 0) {
            ClientManager.getDefault().getChatService().getMessages(targetID, msgID, count, flag, callBack);
        } else {
            List<Long> sendUsers = new ArrayList<>();
            sendUsers.add(sendUserID);
            ClientManager.getDefault().getChatService().getMessages(targetID, msgID, count, flag, sendUsers, null, callBack);
        }
    }



    /**
     * 置顶消息
     *
     * @param targetID 置顶的目标ID
     * @param isTop    是否置顶
     * @param callBack
     */
    public static void top(long targetID, boolean isTop, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().top(targetID, isTop, callBack);
    }

    /**
     * 获取图片消息
     *
     * @param targetID 个人或群ID
     * @param callBack 消息集合
     */
    public static void getImgMsg(long targetID, RequestCallBack<List<ChatMsg>, Void, Void> callBack) {
        ClientManager.getDefault().getChatService().getImgMsg(targetID, callBack);
    }

    /**
     * 获取URL的详细信息
     *
     * @param url      网址
     * @param callBack 接收结果回调 [网址,标题,图片地址,摘要]
     */
    public static void getUrlInfo(String url, RequestCallBack<String[], Void, Void> callBack) {
        ClientManager.getDefault().getChatService().getUrlInfo(url, callBack);
    }

    /**
     * @param targetId 目标ID，个人或群
     * @param passwd   密码
     * @param callBack
     */
    public static void setPrivateKey(long targetId, String passwd, RequestCallBack callBack) {
        ClientManager.getDefault().getChatService().setPrivateKey(targetId, passwd, callBack);
    }

    /**
     * 通过消息ID解密消息
     *
     * @param msgIds   要解密的消息ID集合
     * @param callBack 传入接收结果回调 _1 错误信息 _2 解密消息的targetId _3已解密的消息
     * @paramtargetId 目标ID，个人或群
     */
    public static void decryptMsg(long targetId, List<Long> msgIds, RequestCallBack<Long, List<ChatMsg>, Void> callBack) {
        ClientManager.getDefault().getChatService().decryptMsg(targetId, msgIds, callBack);
    }

    /**
     * 更新单条消息
     */
    public static void updateMsg(ChatMsg msg, ResultCallBack callBack) {
        ClientManager.getDefault().getChatService().updateMsg(msg, callBack);
    }

    //****************************Contact***********************************//

    /**
     * 添加联系人
     *
     * @param userID   传入联系人ID
     * @param remark   传入联系人备注，可以为空
     * @param info     传入验证信息
     * @param callBack 传入接收结果回调 _1错误信息
     */
    public static void addContact(long userID, String remark, String info, RequestCallBack callBack) {
        ClientManager.getDefault().getContactService().add(userID, remark, info, callBack);
    }

    /**
     * 获取联系人验证方式
     *
     * @param userID   传入联系人 ID
     * @param callBack 传入接收结果回调 _1 错误信息 _2验证方式
     */
    public static void getVerifyType(long userID, RequestCallBack<ContactVerifyType, Void, Void> callBack) {
        ClientManager.getDefault().getContactService().getVerifyType(userID, callBack);
    }

    /**
     * 删除联系人
     *
     * @param userID   传入联系人ID
     * @param callBack 传入接收结果回调 _1 错误信息
     */
    public static void removeContact(long userID, RequestCallBack callBack) {
        ClientManager.getDefault().getContactService().remove(userID, callBack);
    }

    /**
     * 更新联系人信息（星标&V标&备注）
     *
     * @param contact  传入联系人信息
     * @param callBack 传入接收结果回调 _1错误信息
     */
    public static void updateContactInfo(Contact contact, RequestCallBack callBack) {
        ClientManager.getDefault().getContactService().updateInfo(contact, callBack);
    }

    /**
     * 设置好友聊天背景
     *
     * @param contactId 好友id
     * @param imgPath   图片路径
     * @param callBack  回调
     */
    public static void setContactBackGround(long contactId, String imgPath, ResultCallBack callBack) {
        ClientManager.getDefault().getContactService().setBackground(contactId, imgPath, callBack);
    }

    /**
     * 获取联系人列表 （同步接口）
     *
     * @return 所有的联系人信息
     */
    public static List<Contact> getContactList() {
        return ClientManager.getDefault().getContactService().getList();
    }

    /**
     * 获取联系人在线状态
     *
     * @param users    传入联系人集合 如果为空则查所有联系人状态
     * @param callBack 传入接收结果回调
     */
    public static void getContactOnline(List<Long> users, RequestCallBack<List<OnlineState>, Void, Void> callBack) {
        ClientManager.getDefault().getContactService().getOnline(users, callBack);
    }

    /**
     * 获取联系人信息 ( 同步接口 )
     *
     * @param userID 用户ID
     * @return 传入接收结果回调
     */
    public static Contact getContactInfo(long userID) {
        return ClientManager.getDefault().getContactService().getInfo(userID);
    }

    /**
     * 获取联系人/陌生人信息 异步
     *
     * @param userID
     * @param callBack
     */
    public static void getContactInfo(long userID, RequestCallBack<Contact, Void, Void> callBack) {
        ClientManager.getDefault().getContactService().getInfo(userID, callBack);
    }

    /**
     * 判断联系人与自己是否是好友关系 ( 同步接口 )
     *
     * @param userID 用户ID
     * @return true为好友  false非好友
     */
    public static boolean isBuddy(long userID) {
        return ClientManager.getDefault().getContactService().isBuddy(userID);
    }

    /**
     * 获取黑名单
     *
     * @param callBack 传入接收结果回调  _1错误信息 _2黑名单ID集合
     */
    public static void getBlackList(RequestCallBack<List<Long>, Void, Void> callBack) {
        ClientManager.getDefault().getContactService().getBlackList(callBack);
    }

    /**
     * 添加黑名单
     *
     * @param ids      要加入的黑名单对象ID集合
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void addBlackList(List<Long> ids, RequestCallBack callBack) {
        ClientManager.getDefault().getContactService().addBlackList(ids, callBack);
    }

    /**
     * \描述: 删除黑名单
     * \参数 ids 要删除的黑名单对象ID集合，为空则删除所有黑名单用户
     * \参数 callBack 传入接收结果回调  _1错误信息
     */
    public static void removeBlackList(List<Long> ids, RequestCallBack callBack) {
        ClientManager.getDefault().getContactService().removeBlackList(ids, callBack);
    }

    /**
     * 根据条件查询拓展字段信息
     *
     //     * @param dicKey   要查询的字段，为空时代表查询所有字段
     //     * @param callBack 传入接收结果回调  _1错误信息 _2返回的查询信息
     //     */
//    public static void queryExtendedField(String dicKey, RequestCallBack<List<EnterpriseDictionary>, Void, Void> callBack) {
//        ClientManager.getDefault().getContactService().queryExtendedField(dicKey, callBack);
//    }

    /**
     * 上传通讯录
     *
     * @param Contacts 要删除的黑名单对象ID集合，为空则删除所有黑名单用户
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void postContact(List<PhoneBookContact> Contacts, RequestCallBack<List<RecommendContact>, Void, Void> callBack) {
        ClientManager.getDefault().getContactService().postContact(Contacts, callBack);
    }
    //****************************Group***********************************//

    /**
     * 创建群
     *
     * @param level    传入群等级
     * @param name     传入群名称
     * @param members  传入群成员
     * @param callBack 传入接收结果回调  _1错误信息  _2群ID
     */
    public static void createGroup(int level, String name, List<Long> members, RequestCallBack<Long, Void, Void> callBack) {
        ClientManager.getDefault().getGroupService().create(level, name, members, callBack);
    }

    /**
     * 加群
     *
     * @param groupID    传入群id
     * @param verifyInfo 传入验证信息
     * @param callBack   传入接收结果回调  _1错误信息
     */
    public static void addGroup(long groupID, String verifyInfo, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().add(groupID, verifyInfo, callBack);
    }

    /**
     * 解散群
     *
     * @param groupID  传入群id
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void removeGroup(long groupID, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().remove(groupID, callBack);
    }

    /**
     * 转让群
     *
     * @param groupID  传入群id
     * @param memberID 传入新群主的id
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void transferGroup(long groupID, long memberID, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().transfer(groupID, memberID, callBack);
    }

    /**
     * 获取群设置
     *
     * @param groupID  传入群id
     * @param callBack 传入接收结果回调  _1错误信息
     *                 验证类型: 1.不允许任何人添加, 2.需要验证信息, 3.允许任何人添加.
     *                 是否允许群成员邀请好友加入群: 1.允许 2.不允许.
     */
    public static void getGroupSet(long groupID, RequestCallBack<Byte, Byte, Void> callBack) {
        ClientManager.getDefault().getGroupService().getSetting(groupID, callBack);
    }

    /**
     * 设置群设置
     *
     * @param groupID    传入群id
     * @param verifyType 传入验证类型
     * @param isAllow    传入是否允许成员邀请用户
     * @param callBack   传入接收结果回调
     */
    public static void setGroupSet(long groupID, byte verifyType, byte isAllow, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().setSetting(groupID, verifyType, isAllow, callBack);
    }

    /**
     * 获取群信息 （同步接口）
     *
     * @param groupID 传入群id
     * @return 群信息
     */
    public static Group getGroupInfo(long groupID) {
        return ClientManager.getDefault().getGroupService().getDetailInfo(groupID);
    }

    /**
     * 获取群详细（异步）
     *
     * @param groupID
     * @param callBack
     */
    public static void getGroupInfo(long groupID, RequestCallBack<Group, Void, Void> callBack) {
        ClientManager.getDefault().getGroupService().getDetailInfo(groupID, callBack);
    }

    /**
     * 设置群信息
     *
     * @param groupID    设置的群ID
     * @param updateInfo 可设置的群信息
     * @param callBack   传入接收结果回调
     */
    public static void setGroupInfo(long groupID, GroupUpdate updateInfo, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().updateInfo(groupID, updateInfo, callBack);
    }

    /**
     * 修改群图像
     *
     * @param groupID
     * @param avatar
     * @param callBack
     */
    public static void setGroupInfoAvatar(long groupID, String avatar, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().updateAvatar(groupID, avatar, callBack);
    }


    /**
     * 群成员设置群聊天背景
     *
     * @param groupID
     * @param imagePath
     * @param callBack
     */
    public static void setGroupBackgroundByAdmin(long groupID, String imagePath, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().setBackgroundByAdmin(groupID, imagePath, callBack);

    }

    /**
     * 获取群列表 （同步接口）
     *
     * @return
     */
    public static List<TinyGroup> getGroupList() {
        return ClientManager.getDefault().getGroupService().getList();
    }

    /**
     * 邀请群成员
     *
     * @param groupID  传入群id
     * @param members  传入成员名单
     * @param callBack 传入接收结果回调
     */
    public static void inviteMember(long groupID, List<Long> members, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().inviteMember(groupID, members, callBack);
    }

    /**
     * 移除群成员
     *
     * @param groupID  传入群id
     * @param memberID 传入需要移除的成员id
     * @param callBack 传入接收结果回调
     */
    public static void removeMember(long groupID, long memberID, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().removeMember(groupID, memberID, callBack);
    }

    /**
     * 设置群成员信息
     *
     * @param member   传入成员信息
     * @param callBack 传入接收结果回调
     */
    public static void setMemberInfo(Member member, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().setMemberInfo(member, callBack);
    }

    /**
     * 判断用户是否在群里 (同步接口)
     *
     * @param groupID  传入群id
     * @param memberID 传入成员id
     * @return false代表不是群成员
     */
    public static boolean isInGroup(long groupID, long memberID) {
        return ClientManager.getDefault().getGroupService().isInGroup(groupID, memberID);
    }

    /**
     * 获取群成员信息 (同步接口)
     *
     * @param groupID  传入群id
     * @param memberID 传入成员id
     * @return member   返回成员信息
     */
    public static Member getMemberInfo(long groupID, long memberID) {
        return ClientManager.getDefault().getGroupService().getMemberInfo(groupID, memberID);
    }

    /**
     * 获取群成员列表
     *
     * @param groupID  传入群id
     * @param callBack 传入接收结果回调  群成员信息集合
     */
    public static void getMemberList(long groupID, RequestCallBack<List<Member>, Void, Void> callBack) {
        ClientManager.getDefault().getGroupService().getMemberList(groupID, callBack);
    }

    /**
     * 获取群文件列表
     *
     * @param groupID  传入群id
     * @param beginID  传入起始id
     * @param count    传入数量
     * @param flag     传入偏移标志0为向上1为向下
     * @param callBack 传入接收结果回调 文件信息集合
     */
    public static void getGroupFileList(long groupID, long beginID, int count, byte flag, RequestCallBack<List<FileInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getGroupService().getFileList(groupID, beginID, count, flag, callBack);
    }

    /**
     * 删除群文件
     *
     * @param files    传入群文件id
     * @param callBack 传入接收结果回调
     */
    public static void deleteGroupFile(List<Long> files, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().deleteFile(files, callBack);
    }

    /**
     * 获取个人群聊背景图片
     *
     * @param groupID  群ID
     * @param callBack 图片URL
     */
    public static void getPersonalGroupImg(long groupID, RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getGroupService().getPersonalGroupImg(groupID, callBack);
    }

    /**
     * 设置个人群聊背景图片
     *
     * @param groupID  群ID
     * @param imgUrl   图片URL
     * @param callBack 传入接收结果回调
     */
    public static void setPersonalGroupImg(long groupID, String imgUrl, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().setPersonalGroupImg(groupID, imgUrl, callBack);
    }


    /**
     * 设置群消息提醒模式
     *
     * @param groupID  群ID
     * @param mode     提醒模式 1：提示并接收消息；2：不提示，接收仅显示数目；3：屏蔽消息
     * @param callBack 传入接收结果回调   _1错误信息
     */
    public static void setGroupMsgRemindType(long groupID, byte mode, RequestCallBack callBack) {
        ClientManager.getDefault().getGroupService().setGroupMsgRemindType(groupID, mode, callBack);
    }

    /**
     * 获取群消息提醒模式
     *
     * @param groupID  群ID
     * @param callBack 传入接收结果回调   提醒模式
     */
    public static void getGroupMsgRemindType(long groupID, RequestCallBack<Byte, Void, Void> callBack) {
        ClientManager.getDefault().getGroupService().getGroupMsgRemindType(groupID, callBack);
    }


    /**
     * 设置群 v标
     *
     * @param groupID
     * @param vSign
     */
    public static void setGroupVSign(long groupID, boolean vSign, ResultCallBack callBack) {
        ClientManager.getDefault().getGroupService().setGroupVSign(groupID, vSign, callBack);
    }

    /**
     * 设置群消息内容提醒模式
     *
     * @param groupID
     * @param msgMode  群通知消息内容模式: 1、通知详情  2、通知源，隐藏内容  3、完全隐藏
     * @param callBack
     */
    public static void setGroupMsgContentMode(long groupID, byte msgMode, ResultCallBack callBack) {
        ClientManager.getDefault().getGroupService().setGroupMsgContentMode(groupID, msgMode, callBack);
    }


    //****************************File***********************************//

    /**
     * 下载文件
     *
     * @param msg
     * @param callBack      传入接收结果回调 _1错误信息  _2本地路径 _3发送者ID
     * @param stateCallBack 进度回调
     * @return 每个文件对应的唯一localID
     */
    public static <T extends ChatMsg> long downloadChatFile(T msg, byte type, RequestCallBack<String, Long, Void> callBack,
                                                            RequestCallBack<Integer, Integer, String> stateCallBack) {
        return ClientManager.getDefault().getChatService().downloadFile(msg, type, callBack, stateCallBack);
    }

    /**
     * 下载聊天图片
     *
     * @param msgImg
     * @param originImg
     * @param callBack
     * @param stateCallBack
     * @return
     */
    public static void downloadChatImage(MsgImg msgImg, boolean originImg, ResultCallBack<String, Long, Void> callBack,
                                         ResultCallBack<Integer, Integer, String> stateCallBack) {
        ClientManager.getDefault().getChatService().downloadImage(msgImg, originImg, callBack, stateCallBack);
    }

    /**
     * 下载文件
     *
     * @param property
     * @param callBack      传入接收结果回调 _1错误信息  _2本地路径 _3发送者ID
     * @param stateCallBack 进度回调
     * @return 每个文件对应的唯一localID
     */
    public static long downloadFile(DownloadFileProperty property, RequestCallBack<String, Long, Void> callBack,
                                    RequestCallBack<Integer, Integer, String> stateCallBack) {
        return ClientManager.getDefault().getFileService().downloadFile(property, callBack, stateCallBack);
    }

    /**
     * 下载外部文件
     *
     * @param localPath     文件本地保存路径
     * @param url           远程路径完整的URL路径
     * @param isFullUrl     是否是全路径
     * @param callBack      传入接收结果回调 _1错误信息
     * @param stateCallBack 进度回调
     * @return 每个文件对应的唯一localID
     */
    public static long downloadExternalFile(String localPath, String url, boolean isFullUrl, RequestCallBack callBack,
                                            RequestCallBack<Integer, Integer, String> stateCallBack) {
        return ClientManager.getDefault().getFileService().downloadExternalFile(localPath, url, isFullUrl, callBack, stateCallBack);
    }

    /**
     * 上传照片
     *
     * @param targetID   人或群的id
     * @param thumbPath  传入缩略图
     * @param srcPath    传入原图
     * @param encryptKey 传入解密密码
     * @param callBack   传入接收结果回调 _1错误信息 _2目标ID， _3原图JSON， _4缩略图JSON
     */
    public static void uploadImage(long targetID, long localID, String thumbPath, String srcPath, String encryptKey,
                                   RequestCallBack<Long, String, String> callBack,
                                   RequestCallBack<Integer, Integer, String> stateCallBack) {
        ClientManager.getDefault().getFileService().uploadImage(targetID, localID, thumbPath, srcPath, encryptKey, callBack, stateCallBack);
    }

    /**
     * 下载图片
     *
     * @param targetID 人或群的id
     * @param url      传入图片url
     * @param callBack 传入接收结果回调 _1错误信息  _2图片名  _3对方ID
     */
    public static void downloadImage(long targetID, String url, RequestCallBack<String, Long, Void> callBack, RequestCallBack<Integer, Integer, String> stateCallBack) {
        ClientManager.getDefault().getFileService().downloadImage(targetID, url, callBack, stateCallBack);
    }

    /**
     * 解密文件
     *
     * @param encryptKey 传入解密密码
     * @param srcPath    传入原图路径
     * @param destPath   传入解密后图片路径
     */
    public static boolean decryptFile(String encryptKey, String srcPath, String destPath) {
        return ClientManager.getDefault().getFileService().decryptFile(encryptKey, srcPath, destPath);
    }


    /**
     * 获取文件列表
     *
     * @param targetID 传入查询对象id
     * @param fileID   传入起始文件id
     * @param count    传入数量
     * @param flag     传入偏移标志0为向上1为向下
     * @param callBack 传入接收结果回调 _1错误信息  _2文件信息集合
     */
    public static void getFileList(long targetID, long fileID, int count, int flag, RequestCallBack<List<FileInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getFileService().getFileList(targetID, fileID, count, flag, callBack);
    }

    /**
     * 通过文件消息ID得到文件详细信息
     *
     * @param fileMsgIds 文件消息ID集合
     */
    public static Map<Long, FileInfo> getFilesInfo(List<Long> fileMsgIds) {
        return ClientManager.getDefault().getFileService().getFilesInfo(fileMsgIds);
    }


    /**
     * 判断是否有文件在传输
     *
     * @param localID 文件的local, 0代表判断是否存在任意文件在上传状态
     */
    public static boolean isTransmitting(int localID) {
        return ClientManager.getDefault().getFileService().isTransmitting(localID);
    }

    /**
     * 取消文件上传或下载
     *
     * @param localID 文件的localID
     * @param type    1.上传 2.下载 3.外部下载
     */
    public static void cancelTransfer(long localID, int type, RequestCallBack callBack) {
        ClientManager.getDefault().getFileService().cancelTransfer(localID, type, callBack);
    }

    /**
     * @描述: 取消所有文件传输
     */
    public static void cancelAllTransfer(RequestCallBack callBack) {
        ClientManager.getDefault().getFileService().cancelAllTransfer(callBack);
    }

    /**
     * 获取局域网可以P2P通讯的用户
     */
    public List<P2PUser> getP2pUsers() {
        return ClientManager.getDefault().getFileService().getP2pUsers();
    }

    /**
     * 取消正在进行的传输或拒绝尚未开始的p2p传输
     */
    public void p2pTransferCancle(long taskID) {
        ClientManager.getDefault().getFileService().p2pTransferCancel(taskID);
    }
    //****************************Search***********************************//

    /**
     * 全局查找消息
     *
     * @param key         传入查找关键字
     * @param msgProperty 传入查找的附加属性
     * @param callBack    传入接收结果回调  _1错误信息  _2消息搜索结果集合
     */
    public static void searchMessage(String key, MsgSearchProperty msgProperty,
                                     RequestCallBack<MsgSearchResult, Void, Void> callBack) {
        ClientManager.getDefault().getSearchService().searchMessage(key, msgProperty, callBack);
    }

    /**
     * 查找相应targetID的详细消息
     *
     * @param key               传入查找关键字
     * @param msgDetailProperty 传入查找的附加属性
     * @param callBack          传入接收结果回调  _1错误信息  _2消息搜索结果集合
     */
    public static void searchDetailMessage(String key, MsgDetailSearchProperty msgDetailProperty,
                                           RequestCallBack<MsgDetailSearchResult, Void, Void> callBack) {
        ClientManager.getDefault().getSearchService().searchDetailMessage(key, msgDetailProperty, callBack);
    }
    //****************************SysMsg***********************************//

    /**
     * 响应加好友的请求
     *
     * @param userId       请求者的用户ID
     * @param msgID        请求消息的msgID
     * @param operType     对此请求的操作
     * @param remark       好友备注
     * @param refuseReason 附带的拒绝信息
     * @param callBack     传入接收结果回调  _1错误信息
     */
    public static void respToAddBuddy(long userId, long msgID, int operType, String remark, String refuseReason, RequestCallBack callBack) {
        ClientManager.getDefault().getSysMsgService().respToAddBuddy(userId, msgID, operType, remark, refuseReason, callBack);
    }

    /**
     * 响应进群的请求
     *
     * @param groupId      要进入的群ID
     * @param msgID        请求消息的msgID
     * @param operType     对此请求的操作，如果是回应被邀请进群消息，只有同意和拒绝选项
     * @param refuseReason 附带的拒绝信息
     */
    public static void respToEnterGroup(long groupId, long msgID, int operType, String refuseReason, RequestCallBack callBack) {
        ClientManager.getDefault().getSysMsgService().respToEnterGroup(groupId, msgID, operType, refuseReason, callBack);
    }

    /**
     * 设置消息已读
     *
     * @param systemMsgs 传入消息id
     */
    public static void setSysMsgRead(List<SystemMsg> systemMsgs) {
        ClientManager.getDefault().getSysMsgService().setMsgRead(systemMsgs);
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
    public static void getSysMessages(int type, int count, long msgID, int flag, RequestCallBack<List<SystemMsg>, Void, Void> callBack) {
        ClientManager.getDefault().getSysMsgService().getMessages(type, count, msgID, flag, callBack);
    }

    /**
     * 通过类型删除系统消息
     *
     * @param type     消息类型  传入操作类型1 好友请求验证框已读|2 好友请求返回框已读| 3 设置群验证请求消息已读|4 设置群验证响应消息已读
     * @param msgIDs   消息ID集合
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void deleteMessageByType(int type, List<Long> msgIDs, RequestCallBack callBack) {
        ClientManager.getDefault().getSysMsgService().deleteMessageByType(type, msgIDs, callBack);
    }

    /**
     * 删除全部系统消息
     *
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void deleteAllMessage(RequestCallBack callBack) {
        ClientManager.getDefault().getSysMsgService().deleteAllMessage(callBack);
    }
    //****************************User***********************************//

    /*****************************************请求接口*******************************************/

    /**
     * 获取账户信息，同步接口
     */
    public static Account getAccountInfo() {
        return getMainAccount();
//                return ClientManager.getDefault().getUserService().getAccountInfo();
//
    }

    /**
     * 更新用户信息
     *
     * @param account  传入用户信息
     * @param callBack 传入接收结果回调  错误信息
     */
    public static void updateAccountInfo(Account account, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().updateAccountInfo(account, callBack);
    }

    /**
     * 更新用户头像
     *
     * @param avatar   头像本地路径
     * @param callBack
     */
    public static void updateAccountAvatar(String avatar, final ResultCallBack callBack) {
        ClientManager.getDefault().getUserService().updateAccountAvatar(avatar, callBack);
    }

    /**
     * 设置账号设置项
     *
     * @param flag     传入设置项属性
     * @param type     传入设置项类型
     * @param callBack 传入接收结果回调 _1 错误信息
     *                 <p>
     *                 type = 1: 设置是否显示在线信息 flag: 0显示 1不显示 默认0\n
     *                 type = 3: 设置豆豆号查找 flag: 0允许 1不允许 默认0\n
     *                 type = 4: 设置手机号查找 flag: 0允许 1不允许 默认0\n
     *                 type = 5: 设置邮箱号查找 flag: 0允许 1不允许 默认0\n
     *                 type = 6: 设置分享更新   flag: 0提示更新 1不提示更新 默认0\n
     *                 type = 7: 新消息通知是否提醒 flag: 0提醒 1不提醒 默认0\n
     *                 type = 12: 多服务新消息通知是否提醒 flag: 0不始终提示 1始终提示 默认0\n
     *                 type = 13: 多服务设置V标好友始终提醒 flag: 0不始终提示 1始终提示 默认0\n
     *                 type = 14: 多服务设置设置@相关人始终提醒 flag: 0不始终提示 1始终提示 默认0
     */
    public static void setSetting(byte flag, int type, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().setSetting(flag, type, callBack);
    }

    /**
     * 获取账号设置项
     * <p>
     * type为1，则返回值为value_i64第一位 如果type=0,返回所有字段，每个字段所占的位于type相对应\n
     *
     * @param callBack 传入接收结果回调  _1错误信息  _2用户设置
     */
    public static void getSetting(RequestCallBack<UserSetting, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getSetting(callBack);
    }

    /**
     * 通过密码获取隐藏对象(好友或群)
     *
     * @param password 之前设置过的密码
     * @param callBack 传入接收结果回调  _1错误信息  _2返回对象ID合集  _3 true代表存在此密码，false代表不存在
     */
    public static void getHiddenTarget(String password, RequestCallBack<List<Long>, Boolean, Void> callBack) {
        ClientManager.getDefault().getUserService().getHiddenTarget(password, callBack);
    }

    /**
     * 设置隐藏对象(好友或群)
     *
     * @param password  设置的密码
     * @param hiddenIDs 设置的隐藏好友集合
     * @param callBack  传入接收结果回调  _1错误信息
     */
    public static void setHiddenTarget(String password, List<Long> hiddenIDs, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().setHiddenTarget(password, hiddenIDs, callBack);
    }

    /**
     * 删除隐藏对象(好友或群)
     *
     * @param password  密码
     * @param hiddenIDs 要删除的ID集合
     * @param callBack  传入接收结果回调  _1错误信息
     */
    public static void delHiddenTarget(String password, List<Long> hiddenIDs, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().delHiddenTarget(password, hiddenIDs, callBack);
    }

    /**
     * 更改隐藏密码
     *
     * @param oldPwd   旧密码
     * @param newPwd   新密码
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void changeHiddenPsw(String oldPwd, String newPwd, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().changeHiddenPsw(oldPwd, newPwd, callBack);
    }

    /**
     * 获取全局勿扰模式
     *
     * @param callBack 传入接收结果回调  _1错误信息 _2起始时间  _3结束时间  _4是否打开
     */
    public static void getGlobalNoDisturbMode(RequestCallBack<Integer, Integer, Boolean> callBack) {
        ClientManager.getDefault().getUserService().getGlobalNoDisturbMode(callBack);
    }

    /**
     * 设置全局勿扰模式
     *
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @param isOpen    是否打开
     * @param callBack  传入接收结果回调  _1错误信息
     */
    public static void setGlobalNoDisturbMode(int startTime, int endTime, boolean isOpen, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().setGlobalNoDisturbMode(startTime, endTime, isOpen, callBack);
    }

    /**
     * 获取针对目标用户的勿扰模式
     *
     * @param targetId 用户ID
     * @param callBack 传入接收结果回调  _1错误信息  _2用户ID  _3设置的值:  1为接收提醒 2表示不提醒仅显示数字 3:为免打扰
     */
    public static void getUserNoDisturbMode(long targetId, RequestCallBack<Long, Byte, Void> callBack) {
        ClientManager.getDefault().getUserService().getUserNoDisturbMode(targetId, callBack);
    }

    /**
     * 设置针对目标用户的勿扰模式
     *
     * @param targetId 用户ID
     * @param value    设置的值:  1为接收提醒 2表示不提醒仅显示数字 3:为免打扰, 默认1
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void setUserNoDisturbMode(long targetId, byte value, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().setUserNoDisturbMode(targetId, value, callBack);
    }

    /**
     * 获取针对目标消息的通知模式
     *
     * @param targetId 用户ID
     * @param callBack 传入接收结果回调  _1错误信息  _2用户ID _3设置的值:  1.通知详情 2.通知源，隐藏内容 3.完全隐藏
     */
    public static void getMsgNotifyMode(long targetId, RequestCallBack<Long, Byte, Void> callBack) {
        ClientManager.getDefault().getUserService().getMsgNotifyMode(targetId, callBack);
    }

    /**
     * 设置针对目标消息的通知模式
     *
     * @param targetId 用户ID
     * @param value    设置的值:  1.通知详情 2.通知源，隐藏内容 3.完全隐藏  默认开启模式1
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void setMsgNotifyMode(long targetId, byte value, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().setMsgNotifyMode(targetId, value, callBack);
    }

    /**
     * 获取服务器时间
     *
     * @param callBack 传入接收结果回调  _1错误信息   _2服务器时间，单位毫秒
     */
    public static void getServerTime(RequestCallBack<Long, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getServerTime(callBack);
    }

    /**
     * 增加本地配置，以键值对方式保存
     *
     * @param items    配置信息集合
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void addLocalSetting(List<LocalSetting> items, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().addLocalSetting(items, callBack);
    }

    /**
     * 获取本地配置
     *
     * @param keys     获取的配置信息的键集合
     * @param callBack 传入接收结果回调  _1错误信息  _2获取的配置信息集合
     */
    public static void getLocalSetting(List<String> keys, RequestCallBack<List<LocalSetting>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getLocalSetting(keys, callBack);
    }

    /**
     * 更新本地配置
     *
     * @param newItems 插入的新的配置信息集合
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void updateLocalSetting(List<LocalSetting> newItems, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().updateLocalSetting(newItems, callBack);
    }

    /**
     * 删除本地配置
     *
     * @param keys     删除的配置信息集合
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void deleteLocalSetting(List<String> keys, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().deleteLocalSetting(keys, callBack);
    }


    /**
     * 设置个人设置项\n
     * type: 1 (生日)，
     * ２（电话），
     * ３（邮件）\n
     * 　    value:  1：所有人可见 2：仅好友可见 3：仅自己可见，默认1\n
     * type: 4 (好友验证方式)\n
     * 　　   value:   1：需要验证信息,2:不允许任何人添加,3:允许任何人添加，默认1\n
     * type: 5 V标\n
     * value: 1:表示始终有声音提醒，2：表示始终无声音提醒 3:不始终提醒，默认1\n
     * type: 6 @相关人提醒模式\n
     * value: 1:表示始终有声音提醒，2：表示始终无声音提醒 3:不始终提醒，默认1\n
     * type: 7 全局通知消息内容展现模式\n
     * value: 1:通知详情，2：通知源，隐藏内容 3:完全隐藏，默认2\n
     *
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void setPersonalData(List<PersonalData> items, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().setPersonalData(items, callBack);
    }

    /**
     * 获取个人设置项
     *
     * @param types
     * @param callBack
     */
    public static void getPersonalData(List<Integer> types, RequestCallBack<List<PersonalData>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getPersonalData(types, callBack);
    }

    /**
     * 通过应用ID获取应用信息
     *
     * @param appId    应用程序ID
     * @param callBack 传入接收结果回调  _1错误信息  _2返回的值
     */
    public static void getAppInfo(long appId, RequestCallBack<EntAppInfo, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getAppInfo(appId, callBack);
    }

    /**
     * 查询应用市场应用信息
     *
     * @param qData    传入的查询信息
     * @param callBack 传入接收结果回调  _1错误信息  _2返回的应用信息
     */
    public static void queryMarketApplication(QueryMarketApplication qData, RequestCallBack<SmallMarketAppPage, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().queryMarketApplication(qData, callBack);
    }

    /**
     * 删除添加应用
     *
     * @param type     传入的查询类型
     * @param appID    操作的appID
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void addOrDeleteApplication(byte type, long appID, RequestCallBack callBack) {
        ClientManager.getDefault().getUserService().addOrDeleteApplication(type, appID, callBack);
    }

    /**
     * 获取已安装的应用
     *
     * @param callBack 传入接收结果回调  _1错误信息 _2返回的查询信息
     */
    public static void getInstalledApplication(RequestCallBack<List<SmallMarketAppInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getInstalledApplication(callBack);
    }

    /**
     * 单个查询表情包
     *
     * @param label    表情包标签
     * @param callBack 传入接收结果回调  _1错误信息 _2返回的查询信息
     */
    public static void queryEmoticonPackageByLabel(String label, RequestCallBack<List<EmoticonPackage>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().queryEmoticonPackageByLabel(label, callBack);
    }

    /**
     * 单个表情查询
     *
     * @param md5      单个表情的md5
     * @param callBack 传入接收结果回调  _1错误信息 _2返回的查询信息
     */
    public static void queryEmoticon(String md5, RequestCallBack<List<Emoticon>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().queryEmoticon(md5, callBack);
    }

    /**
     * 根据表情包标识查询所有表情
     *
     * @param md5 表情包的md5
     */
    public static void queryEmoticonByPackageMd5(String md5, ResultCallBack<List<Emoticon>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().queryEmoticon(md5, callBack);
    }

    /**
     * 获取自己的自定义表情
     */
    public static void queryMyEmoticon(ResultCallBack<List<Emoticon>, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().queryMyEmoticon(callBack);
    }

    /**
     * 单个查询表情包
     *
     * @param md5      md5值
     * @param callBack 传入接收结果回调  _1错误信息 _2返回的查询信息
     */
    public static void queryEmoticonPackage(String md5, RequestCallBack<EmoticonPackage, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().queryEmoticonPackage(md5, callBack);
    }

    /**
     * 分页查询表情包
     *
     * @param pageNum  页码
     * @param pageSize 页长
     * @param callBack 传入接收结果回调  _1错误信息 _2返回的查询信息
     */
    public static void getEmoticonPackageList(int pageNum, int pageSize, RequestCallBack<PageQueryEmoticon, Void, Void> callBack) {
        ClientManager.getDefault().getUserService().getEmoticonPackageList(pageNum, pageSize, callBack);
    }

    /**
     * 增删自定义表情
     *
     * @param isAdd    添加/删除
     * @param emoticon 表情
     * @param callBack 传入接收结果回调  _1错误信息 _2resultCode _3resultMsg _4successList _5failedList
     */
    public static void addOrDeleteCustomEmoticon(boolean isAdd, Emoticon emoticon, RequestCallBack<Integer, String, List<EmoticonResult>[]> callBack) {
        ClientManager.getDefault().getUserService().addOrDeleteCustomEmoticon(isAdd, emoticon, callBack);
    }

    /**
     * 本地数据导入导出
     *
     * @param req      见结构体定义处说明
     * @param callBack 传入接收结果回调  _1错误信息 _2resultCode _3resultMsg _4successList _5failedList
     */
//    public static void transLocalData(TransferLocalData req, RequestCallBack callBack) {
//        ClientManager.getDefault().getUserService().transLocalData(req, callBack);
//    }


    /**
     * 从网络进行查找
     *
     * @param key      传入关键字
     * @param callBack 传入接收结果回调  _1错误信息  _2搜索结果
     */
    public static void searchFromNet(String key, RequestCallBack<SearchResult, Void, Void> callBack) {
        ClientManager.getDefault().getSearchService().searchFromNet(key, callBack);
    }

    /**
     * 从本地数据库进行查找
     *
     * @param key      传入关键字
     * @param callBack 传入接收结果回调  _1错误信息  _2搜索结果
     */
    public static void searchFromLocal(String key, RequestCallBack<SearchResult, Void, Void> callBack) {
        ClientManager.getDefault().getSearchService().searchFromLocal(key, callBack);
    }

    /**
     * 从本地数据库进行查找
     *
     * @param userID   传入用户ID
     * @param callBack 传入接收结果回调  _1错误信息  _2用户信息
     */
    public static void getUserInfo(long userID, RequestCallBack<Contact, Void, Void> callBack) {
        ClientManager.getDefault().getContactService().getInfo(userID, callBack);
    }
    //****************************Face2Face***********************************//


    /**
     * 创建近距离加好友房间
     *
     * @param longitude 经度
     * @param latitude  维度
     * @param callBack  服务器返回的房间密码
     */
    public static void createBuddyRoom(double longitude, double latitude, RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getFaceService().createBuddyRoom(getUserID(), longitude, latitude, callBack);
    }

    /**
     * 创建近距离加群房间
     *
     * @param longitude 经度
     * @param latitude  维度
     * @param callBack  传入接收结果回调  _1错误信息  _2服务器返回的房间密码
     */
    public static void createGroupRoom(double longitude, double latitude, RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getFaceService().createGroupRoom(getUserID(), longitude, latitude, callBack);
    }

    /**
     * 进入近距离加好友房间
     *
     * @param userId    用户ID
     * @param longitude 经度
     * @param latitude  纬度
     * @param passwd    房间密码
     * @param callBack  传入接收结果回调  _1错误信息
     */
    public static void enterBuddyRoom(long userId, double longitude, double latitude, String passwd, ResultCallBack<List<User>, Void, Void> callBack) {
        ClientManager.getDefault().getFaceService().enterBuddyRoom(userId, latitude, latitude, passwd, callBack);
    }

    /**
     * 进入近距离加群房间
     *
     * @param userId    用户ID
     * @param longitude 经度
     * @param latitude  纬度
     * @param passwd    房间密码
     * @param callBack  传入接收结果回调  _1错误信息
     */
    public static void enterGroupRoom(long userId, double longitude, double latitude, String passwd, RequestCallBack<List<User>, Void, Void> callBack) {
        ClientManager.getDefault().getFaceService().enterGroupRoom(userId, longitude, latitude, passwd, callBack);
    }

    /**
     * 退出近距离加好友房间
     *
     * @param userId   用户ID
     * @param roomId   房间ID
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void exitBuddyRoom(long userId, String roomId, RequestCallBack callBack) {
        ClientManager.getDefault().getFaceService().exitBuddyRoom(userId, roomId, callBack);
    }

    /**
     * 退出近距离加群房间
     *
     * @param userId   用户ID
     * @param roomId   房间ID
     * @param callBack 传入接收结果回调  _1错误信息
     */
    public static void eixtGroupRoom(long userId, String roomId, RequestCallBack callBack) {
        ClientManager.getDefault().getFaceService().exitGroupRoom(userId, roomId, callBack);
    }


    /**
     * 批量向好友房间的人发送好友请求(房间创建者操作)
     *
     * @param userId     用户ID
     * @param roomId     房间ID
     * @param verifyInfo 验证信息
     * @param userList   发送的好友请求的用户ID集合，为房间中用户的子集
     * @param callBack   传入接收结果回调  _1错误信息
     */
    public static void addBuddysFromRoom(long userId, String roomId, String verifyInfo, List<Long> userList,
                                         RequestCallBack callBack) {
        ClientManager.getDefault().getFaceService().addBuddysFromRoom(userId, roomId, verifyInfo, userList, callBack);
    }

    /**
     * 创建一个群，批量要求群房间的人进入 (房间创建者操作)
     *
     * @param userId     用户ID
     * @param roomId     房间ID
     * @param groupLevel 群等级
     * @param userList   发送的好友请求的用户ID集合，为房间中用户的子集
     * @param callBack   传入接收结果回调  _1错误信息
     */
    public static void createGroupFromRoom(long userId, String roomId, byte groupLevel, String groupName, List<Long> userList,
                                           RequestCallBack callBack) {
        ClientManager.getDefault().getFaceService().createGroupFromRoom(userId, roomId, groupLevel, groupName, userList, callBack);
    }


    //****************************ext***********************************//

    //----------企业组织相关----------

    /**
     //     * 获取企业列表
     //     *
     //     * @param callBack 传入接收结果回调 _1 错误号; _2 企业信息集合;
     //     */
//    public static void getEnterpriseListByUser(RequestCallBack<List<EnterpriseInfo>, Void, Void> callBack) {
//        ClientManager.getDefault().getExtService().getEnterpriseListByUser(callBack);
//    }

    /**
     * getOrgInfo
     *
     * @param orgId    组织Id
     * @param callBack 传入接收结果回调 _1 错误号;_2 组织信息;
     */
    public static void getOrgInfo(long orgId, RequestCallBack<OrganizationInfo, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getOrgInfo(orgId, callBack);
    }


    /**
     * etVisibleOrgUsers
     *
     * @param orgId    组织ID  传 0 表示获取根组织
     * @param callBack 组织集合, 企业用户信息;
     */
    public static void getVisibleOrgUsers(long orgId, ResultCallBack<byte[], List<OrganizationInfo>, List<EnterpriseUserInfo>> callBack) {
        ClientManager.getDefault().getExtService().getVisibleOrgUsers(orgId, callBack);
    }

    /**
     * 根据条件查询企业用户列表
     *
     * @param enterpriseUserQueryInfo 查询信息
     * @param callBack                传入接收结果回调 _1 错误号; _2 total 总数; _3 totalPage 总页数; _4 用户信息集合;
     */
    public static void queryEnterpriseUserList(EnterpriseUserQueryInfo enterpriseUserQueryInfo,
                                               RequestCallBack<Long, Long, Map<String, List<EnterpriseUserInfo>>> callBack) {
        ClientManager.getDefault().getExtService().queryEnterpriseUserList(enterpriseUserQueryInfo, callBack);
    }

    /**
     * 获取企业所有用户信息
     *
     * @param orgIds   需要获取的用户集合
     * @param callBack 传入接收结果回调 _1 错误号;_2 返回企业用户信息;
     */
    public static void queryEntUserAll(List<Long> orgIds, RequestCallBack<List<EnterpriseUserInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().queryEntUserAll(orgIds, callBack);
    }

    /**
     * queryEntUser
     *
     * @param userId   userID
     * @param callBack 传入接收结果回调 _1 错误号;_2 返回企业用户信息;
     */
    public static void queryEntUserOneById(long userId, RequestCallBack<List<EnterpriseUserInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().queryEntUserOneById(userId, callBack);
    }

    /**
     * queryEntUser
     *
     * @param userName 用户名
     * @param callBack 传入接收结果回调 _1 错误号;_2 返回企业用户信息;
     */
    public static void queryEntUserOneByName(String userName, RequestCallBack<List<EnterpriseUserInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().queryEntUserOneByName(userName, callBack);
    }

    //---------------------???--------------------

    /**
     * getMsgCountByTargetID
     *
     * @param targetId targetId
     * @param callBack 传入接收结果回调 _1 错误号;_2 消息数目;
     */
    public static void getMsgCountByTargetID(long targetId, RequestCallBack<Integer, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getMsgCountByTargetID(targetId, callBack);
    }

    /**
     * 获取\自己最多的topN群
     *
     * @param topN     获取前topN会话的群组
     * @param callBack 传入接收结果回调 _1 错误号;_2 targetID集合;_3 消息数目;
     */
    public static void getMsgTopNAtGroup(int topN, RequestCallBack<List<Long>, List<Integer>, Void> callBack) {
        ClientManager.getDefault().getExtService().getMsgTopNAtGroup(topN, callBack);
    }

    /**
     * getMsgTopNGroup
     *
     * @param topN     获取前topN群组的消息
     * @param callBack 传入接收结果回调 _1 错误号;_2 targetID集合;_3 消息数目;
     */
    public static void getMsgTopNGroup(int topN, RequestCallBack<List<Long>, List<Integer>, Void> callBack) {
        ClientManager.getDefault().getExtService().getMsgTopNGroup(topN, callBack);
    }

    /**
     * getMsgTopNSession
     *
     * @param topN     获取前topN会话的消息
     * @param callBack 传入接收结果回调 _1 错误号;_2 targetID集合;_3 消息数目;
     */
    public static void getMsgTopNSession(int topN, RequestCallBack<List<Long>, List<Integer>, Void> callBack) {
        ClientManager.getDefault().getExtService().getMsgTopNSession(topN, callBack);
    }


    //--------------task---------------

    /**
     * 获取任务回复消息
     * <p>
     * //     * @param taskId   要获取的的任务id
     *
     * @param callBack 传入接收结果回调 _1 错误号;_2 回复消息;
     */
    public static void getRecvMsg(byte type, long msgID, String time, int count, ResultCallBack<List<ChatMsg>, List<Task>, Void> callBack) {
        ClientManager.getDefault().getExtService().getReceiveMsg(type, msgID, time, count, callBack);
    }

    /**
     * 获取接收的任务
     *
     * @param callBack 传入接收结果回调  _1 错误号; _2 任务集;
     */
    public static void getRecvTask(RequestCallBack<List<Task>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getReceiveTask(callBack);
    }

    /**
     * 发送任务消息
     *
     * @param msgId    传入TaskId
     * @param isTop    是否置顶
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void topTask(long msgId, byte isTop, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().topTask(msgId, isTop, callBack);
    }

    /**
     * 恢复任务
     *
     * @param taskId   需要恢复的TaskID
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void recoveryTask(long taskId, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().recoveryTask(taskId, callBack);
    }

    /**
     * 获取历史任务
     *
     * @param callBack 传入接收结果回调 _1 错误号; _2 任务集;
     */
    public static void getHistoryTask(RequestCallBack<List<Task>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getHistoryTask(callBack);
    }

    /**
     * 发送任务消息
     *
     * @param msg      传入组织id
     * @param callBack 传入接收结果回调 _1 错误号; _2 msgID; _3 sendTime;
     */
    public static void sendTaskMsg(ChatMsg msg, RequestCallBack<Long, Long, Void> callBack) {
        ClientManager.getDefault().getExtService().sendTaskMsg(msg, callBack);
    }

    /**
     * 获取指派的任务
     *
     * @param callBack 传入接收结果回调 _1 错误号; _2 任务集;
     */
    public static void getApTask(RequestCallBack<List<Task>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getApTask(callBack);
    }

    /**
     * 获取任务上下文
     *
     * @param taskId   指定的TaskID
     * @param callBack 传入接收结果回调 _1 错误号; _2 任务上下文;
     */
    public static void getBodyTask(long taskId, RequestCallBack<String, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getBodyTask(taskId, callBack);
    }

    /**
     * 完成任务
     *
     * @param taskId   需要完成的任务 id
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void finishTask(long taskId, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().finishTask(taskId, callBack);
    }

    //-----------------note----------------

    /**
     * 获取记事本
     *
     * @param beginID    起始消息ID offsetFlag = 0 msgBeginID = 0时，代表从最大的消息Id进行查找
     * @param offset     查询的数量
     * @param offsetFlag 偏移标志；0.消息Id由大到小偏移 1.消息Id由小到大偏移 offsetFlag.
     * @param callBack   传入接收结果回调 _1 错误号; _2 记事本集合
     */
    public static void getNote(long beginID, int offset, byte offsetFlag, RequestCallBack<List<NoteInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getNote(beginID, offset, offsetFlag, callBack);
    }

    /**
     * 搜索记事本
     * <p>
     * //     * @param key      搜索关键字
     *
     * @param callBack 传入接收结果回调 _1 错误号; _2 搜索的记事本集合
     */
    public static void searchNote(SearchNoteInfo info, RequestCallBack<List<NoteInfo>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().searchNote(info, callBack);
    }


    /**
     * 置顶记事本
     *
     * @param noteId   要置顶的note id
     * @param isTop    是否置顶
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void topNote(long noteId, boolean isTop, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().topNote(noteId, isTop, callBack);
    }

    /**
     * 删除记事本
     *
     * @param noteId   需删除noteID
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void delNote(List<Long> noteId, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().deleteNote(noteId, callBack);
    }


    /**
     * 添加记事本
     *
     * @param noteInfo 记事本信息
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void addNote(NoteInfo noteInfo, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().addNote(noteInfo, callBack);
    }

    /**
     * 归档记事本
     *
     * @param noteId    要归档的note id
     * @param isArchive 是否归档
     * @param callBack  传入接收结果回调 _1 错误号;
     */
    public static void archiveNote(long noteId, byte isArchive, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().archiveNote(noteId, isArchive, callBack);
    }

    /**
     * 修改记事本
     * \description: 修改记事本
     *
     * @param noteInfo 新的note信息
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void editNote(NoteInfo noteInfo, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().editNote(noteInfo, callBack);
    }

    //-----------------room----------------

    /**
     * 房间置顶
     *
     * @param roomId   房间ID
     * @param isTop    是否置顶
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void topRoom(long roomId, boolean isTop, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().topRoom(roomId, isTop, callBack);
    }

    /**
     * 获取房间信息
     *
     * @param roomID   房间ID
     * @param callBack 传入接收结果回调 _1 错误号; _2 获取的房间信息;
     */
    public static void getRoom(long roomID, RequestCallBack<List<Room>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getRoom(roomID, callBack);
    }


    /**
     * changRoomIcon
     *
     * @param roomId   要修改的roomID
     * @param icoURL   头像的URL
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void changRoomIcon(long roomId, String icoURL, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().changRoomIcon(roomId, icoURL, callBack);
    }

    /**
     * 修改房间名称
     *
     * @param roomId   要修改的roomID
     * @param newName  新名称
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void changRoomName(long roomId, String newName, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().changRoomName(roomId, newName, callBack);
    }

    /**
     * 创建房间
     *
     * @param roomName 房间名
     * @param ids      成员集合
     * @param flag     是否置顶 0:不置顶 ，1：置顶
     * @param url      头像url
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void createRoom(String roomName, List<Long> ids,
                                  byte flag, String url, RequestCallBack<Integer, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().createRoom(roomName, ids, flag, url, callBack);
    }

    /**
     * 删除房间
     *
     * @param roomID   房间ID
     * @param callBack 传入接收结果回调 _1 错误号;
     */
    public static void deleteRoom(long roomID, RequestCallBack callBack) {
        ClientManager.getDefault().getExtService().deleteRoom(roomID, callBack);
    }

    /**
     * 获取所有房间信息
     *
     * @param callBack 传入接收结果回调 _1 错误号; _2 获取的房间信息集合;
     */
    public static void getAllRoom(RequestCallBack<List<Room>, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().getAllRoom(callBack);
    }

    //添加房间成员
    public static void addRoomMember(long roomID, List<Long> member, RequestCallBack<Void, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().addRoomMember(roomID, member, callBack);
    }

    //移除房间成员
    public static void deleteRoomMember(long roomID, List<Long> member, RequestCallBack<Void, Void, Void> callBack) {
        ClientManager.getDefault().getExtService().delRoomMember(roomID, member, callBack);
    }

    public static Chat system2Chat(SystemMsg systemMsg, int unReadNum) {
        Chat chat = new Chat();
        chat.setMsgType(systemMsg.getMsgType());
        chat.setID(ChatMsgApi.getSysMsgID());
        chat.setLastMsgID(systemMsg.getMsgID());
        chat.setMsgType(ChatMsgApi.TYPE_TEXT);
        chat.setMsgTime(systemMsg.getTime());
        chat.setUnreadCount(unReadNum);
        chat.setRealUnreadCount(unReadNum);
        return chat;
    }
}

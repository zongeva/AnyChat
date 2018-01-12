//package com.kpz.AnyChat.Others;
//
//import android.content.Context;
//
//import com.kpz.AnyChat.Chat.ChatMessageView;
//import com.kpz.AnyChat.Chat.ChatMsgItemView;
//import com.vrv.imsdk.chatbean.ChatMsg;
//import com.vrv.imsdk.chatbean.ChatMsgApi;
//
//public class ChatMsgItemFactory {
//
//    public static ChatMsgItemView createItemView(Context context, ChatMsg msgBean, ChatMessageView chatMessageView) {
//        if (msgBean == null) {
//            return new ChatUnknownView(context, null);
//        }
//        int msgType = msgBean.getMsgType();
//        switch (msgType) {
//            case ChatMsgApi.TYPE_TEXT:
//                return new ChatTxtView(context, msgBean, chatMessageView);
//            case ChatMsgApi.TYPE_IMAGE:
//                return new ChatImgView(context, msgBean);
//            case ChatMsgApi.TYPE_FILE:
//                return new ChatFileView(context, msgBean);
//            case ChatMsgApi.TYPE_AUDIO:
//                return new ChatAudioView(context, msgBean);
//            case ChatMsgApi.TYPE_CARD:
//                return new ChatCardView(context, msgBean);
//            case ChatMsgApi.TYPE_POSITION:
//                return new ChatPositionView(context, msgBean);
//            case ChatMsgApi.TYPE_WEAK_HINT:
//        }
//        return new ChatUnknownView(context, msgBean);
//    }
//}

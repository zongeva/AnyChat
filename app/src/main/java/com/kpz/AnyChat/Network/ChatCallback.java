package com.kpz.AnyChat.Network;

import com.kpz.AnyChat.Others.BaseInfoBean;

import java.util.ArrayList;

/**
 * Created by Lenovo on 2017-11-08.
 */

public interface ChatCallback {

        void handleReturnData(ArrayList<BaseInfoBean> list);


}



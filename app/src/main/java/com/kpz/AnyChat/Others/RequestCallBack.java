package com.kpz.AnyChat.Others;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.vrv.imsdk.api.ResponseCode;
import com.vrv.imsdk.model.ResultCallBack;


/**
 */
public abstract class RequestCallBack<X, Y, Z> implements ResultCallBack<X, Y, Z> {

    private final String TAG = RequestCallBack.class.getSimpleName();
    private Dialog dialog;
    private boolean isShow; // 默认不显示对话框
    private Context mContext;

    public RequestCallBack() {

    }

    /**
     * @param show 是否显示请求对话框
     */
    public RequestCallBack(boolean show, Context context) {
        if (context == null) {
            return;
        }
        mContext = context;
        this.isShow = show;
        if (show) {
            this.dialog = DialogUtil.buildProgressDialog(context);
//            this.dialog.setCancelable(false);
            this.dialog.show();
        }
    }

    @Override
    public void onSuccess(X x, Y y, Z z) {
        dismissDialog();
        handleSuccess(x, y, z);
    }

    @Override
    public void onError(int code, String message) {
        dismissDialog();
        handleFailure(code, message);
    }

    public abstract void handleSuccess(X x, Y y, Z z);

    private void dismissDialog() {
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    public void handleFailure(int code, String message) {
        Log.e("", " Request Fail {code=" + code + ",msg=" + message + "}");
        // 错误码1304 和 115 需要重新登录
        if (mContext == null) return;
        if (ResponseCode.RSP_LOGIN_FORCE_LOGOUT == code || ResponseCode.RSP_LOGIN_SESSION_INVALID == code) {
//            DialogUtil.accountSafeTips(mContext); // todo: 存在activity不可见，但需要显示dialog的情况
            return;
        }

        String codeID = "CODE_" + code;
        if (code < 0) {
            codeID = "CODE1_" + Math.abs(code);
        }
        int resCodeID = ResourceUtils.getIdentifier(mContext, codeID, "string");
        String toast;
        if (resCodeID <= 0) {
            toast = "Unknow Error，code = " + code;
        } else {
            toast = ResourceUtils.getString(mContext, resCodeID);
        }
        ToastUtil.showShort(mContext, toast);
    }
}

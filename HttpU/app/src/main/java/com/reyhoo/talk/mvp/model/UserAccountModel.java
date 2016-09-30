package com.reyhoo.talk.mvp.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.reyhoo.talk.App;
import com.reyhoo.talk.component.Req;
import com.reyhoo.talk.component.Resp;
import com.reyhoo.talk.component.TalkEngine;
import com.reyhoo.talk.entity.User;
import com.reyhoo.talk.exception.NoConnectionException;
import com.reyhoo.talk.util.CommonUtil;
import com.reyhoo.talk.util.GlobalConst;
import com.reyhoo.talk.util.Md5Util;
import com.reyhoo.talk.util.Preferences;

/**
 * Created by Administrator on 2016/7/29.
 */
public class UserAccountModel extends BaseModel {



    private static boolean isLogin = false;


    private static Preferences preferences;

    public static synchronized boolean isLogin() {
        return isLogin;
    }


    public static synchronized void setLoginUser(User loginUser) {
        String json = new Gson().toJson(loginUser);
        preferences.setPreString(GlobalConst.LOGIN_USER_INFO, json);
    }
    public static synchronized void saveMobile(String mobile) {
        preferences.setPreString(GlobalConst.LOGIN_USER_MOBILE, mobile);
    }
    public static synchronized String getMobileFromCache() {
       return preferences.getPreString(GlobalConst.LOGIN_USER_MOBILE);
    }
    public static synchronized void clearLoginUser() {
        preferences.setPreString(GlobalConst.LOGIN_USER_INFO, "");
    }
    public static synchronized User getLoginUser() {
        String json = preferences.getPreString(GlobalConst.LOGIN_USER_INFO);
        if (CommonUtil.isEmptyString(json))
            return null;
        User u = new Gson().fromJson(json, User.class);
        return u;
//        return null;
    }

    public static synchronized void setLogin(boolean login) {
        isLogin = login;
    }

    public interface UserAccountModelCallback {
        /****
         * obj = User
         */
        int LOGIN_SUCCESS = 1;
//        int LOGIN_TIMEOUT = 2;
        /****
         * obj = String
         */
        int LOGIN_FAIL = 2;

        /****
         * obj = null
         */
        int LOGIN_FAIL_DATA_ERROR = 3;


        void onEvent(int type, Object obj);


    }

    static {
        preferences = new Preferences(App.instance);
    }



    public void loginAuto(final String mobile, String password, final UserAccountModelCallback callback) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(password);
        Req<User> req = new Req<>();
        req.content = user;
        req.type = GlobalConst.CMD_LOGIN;
        TalkEngine.getInstance().sendData(req, new TalkEngine.RequestCallback() {
            @Override
            public void onSent(Req req) {
                Log.i("UserService", "UserAccountModel::loginAuto::onSent::req:" + req);
//                callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "请求失败" + "(" + e + ")");
            }

            @Override
            public void onSendException(Exception e) {
                Log.i("UserService", "UserAccountModel::loginAuto::onSendException::e:" + e);
                if(e instanceof NoConnectionException){
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "未连接服务器");
                }else{
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "请求失败" + "(" + e + ")");
                }
            }

            @Override
            public void onConnectionClosed() {
                Log.i("UserService", "UserAccountModel::loginAuto::onConnectionClosed::");
                callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "连接断开");
            }

            @Override
            public void onReadTimeout() {
                Log.i("UserService", "UserAccountModel::loginAuto::onReadTimeout::");
                callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "登录超时");
            }

            @Override
            public void onReceive(Resp resp) {
                Log.i("UserService", "UserAccountModel::loginAuto::onReceive::resp:" + resp);
                if (resp.isSuccess()) {
                    saveMobile(mobile);
                    setLogin(true);
                    setLoginUser((User) resp.content);
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_SUCCESS, resp.content);
                } else {
                    clearLoginUser();
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL_DATA_ERROR, "");
                }

            }
        });
    }
    public void login(final String mobile, String password, final UserAccountModelCallback callback) {
        User user = new User();
        user.setMobile(mobile);
        user.setPassword(Md5Util.getMD5Str(password));
        Req<User> req = new Req<>();
        req.content = user;
        req.type = GlobalConst.CMD_LOGIN;
        TalkEngine.getInstance().sendData(req, new TalkEngine.RequestCallback() {
            @Override
            public void onSent(Req req) {
                Log.i("UserService", "UserAccountModel::login::onSent::req:" + req);
//                callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "请求失败" + "(" + e + ")");
            }

            @Override
            public void onSendException(Exception e) {
                Log.i("UserService", "UserAccountModel::login::onSendException::e:" + e);
                if(e instanceof NoConnectionException){
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "未连接服务器");
                }else{
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "请求失败" + "(" + e + ")");
                }
            }

            @Override
            public void onConnectionClosed() {
                Log.i("UserService", "UserAccountModel::login::onConnectionClosed::");
                callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "连接断开");
            }

            @Override
            public void onReadTimeout() {
                Log.i("UserService", "UserAccountModel::login::onReadTimeout::");
                callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, "登录超时");
            }

            @Override
            public void onReceive(Resp resp) {
                Log.i("UserService", "UserAccountModel::login::onReceive::resp:" + resp);
                if (resp.isSuccess()) {
                    saveMobile(mobile);
                    setLogin(true);
                    setLoginUser((User) resp.content);
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_SUCCESS, resp.content);
                } else {
                    callBackInMainThread(callback, UserAccountModelCallback.LOGIN_FAIL, resp.errMsg + "(" + resp.errCode + ")");
                }

            }
        });
    }

    public void logout(String mobile, String password, UserAccountModelCallback callback) {

    }

    private void callBackInMainThread(final UserAccountModelCallback callback, final int type, final Object obj) {
        if (callback == null) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onEvent(type, obj);
            }
        });
    }

}

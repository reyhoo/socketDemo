package com.reyhoo.talk.mvp.presenter;

import com.reyhoo.talk.entity.User;
import com.reyhoo.talk.mvp.model.UserAccountModel;
import com.reyhoo.talk.mvp.view.LoginViewDelegate;

/**
 * Created by Administrator on 2016/7/29.
 */
public class LoginPresenter extends BasePresenter<LoginViewDelegate, UserAccountModel> {


    public void login() {
        String mobile = mView.getMobile();
        String password = mView.getPassword();
        mModel.login(mobile, password, new UserAccountModel.UserAccountModelCallback() {
            @Override
            public void onEvent(int type, Object obj) {
                switch (type) {
                    case LOGIN_SUCCESS:
                        mView.toast("登录成功");
                        mView.startMainActivity();
                        break;
                    case LOGIN_FAIL:
                        mView.toast("登录失败：" + obj);
                        break;
                }
            }
        });
    }

    public void showMobileAndPassword() {
        mView.InputEt(mModel.getMobileFromCache());
    }

    public boolean toMainPageWithAuth() {
        User user = mModel.getLoginUser();
        if (user == null)
            return false;
        mView.startMainActivity();
        return true;
    }

    @Override
    public Class<UserAccountModel> getModelClass() {
        return UserAccountModel.class;
    }

    @Override
    public Class<LoginViewDelegate> getViewClass() {
        return LoginViewDelegate.class;
    }
}

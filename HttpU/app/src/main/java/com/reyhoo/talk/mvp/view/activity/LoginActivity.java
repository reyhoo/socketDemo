package com.reyhoo.talk.mvp.view.activity;

import android.os.Bundle;
import android.view.View;

import com.reyhoo.talk.R;
import com.reyhoo.talk.mvp.presenter.LoginPresenter;

/**
 * Created by Administrator on 2016/7/29.
 */
public class LoginActivity extends BaseActivity<LoginPresenter> {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isAuth = mPresenter.toMainPageWithAuth();
        if(isAuth){
            return;
        }
        mPresenter.showMobileAndPassword();
        mPresenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.login();
            }
        }, R.id.login_logining_btn);
    }

    @Override
    public Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }
}

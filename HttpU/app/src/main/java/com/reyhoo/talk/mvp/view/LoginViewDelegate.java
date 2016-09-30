package com.reyhoo.talk.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import com.reyhoo.talk.R;
import com.reyhoo.talk.entity.User;
import com.reyhoo.talk.mvp.view.activity.MainActivity;

/**
 * Created by Administrator on 2016/7/29.
 */
public class LoginViewDelegate extends BaseViewDelegate {



    @Override
    public void initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.initLayout(inflater, container, savedInstanceState);

    }

    public void startMainActivity(){
        Intent intent = getIntent(MainActivity.class);
        startActivityFinishSelf(intent);
    }

    public String getMobile(){
        EditText et = get(R.id.login_mobile_phone_et);
        return et.getText().toString().trim();
    }
    public String getPassword(){
        EditText et = get(R.id.login_password_et);
        return et.getText().toString().trim();
    }
    @Override
    public int getMainLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public int getPageTitleId() {
        return R.string.login;
    }

    public void InputEt(String mobile) {
        EditText et = get(R.id.login_mobile_phone_et);
        et.setText(mobile);
    }
}

package com.reyhoo.talk.mvp.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.reyhoo.talk.App;
import com.reyhoo.talk.mvp.presenter.BasePresenter;

/**
 * Created by Administrator on 2016/7/29.
 */
public abstract class BaseActivity<P extends BasePresenter> extends Activity implements View.OnClickListener{

    public P mPresenter;
    public BaseActivity() {
        try {
            mPresenter = getPresenterClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getClass()+":创建失败",e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.addActivity(this);
        super.onCreate(savedInstanceState);
        mPresenter.mView.initLayout(getLayoutInflater(),null,savedInstanceState);
        setContentView(mPresenter.mView.getMainView());
        mPresenter.mView.initActionBar();
        mPresenter.mView.setGoBackListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == mPresenter.mView.getGoBackButtonId()){
            finish();
        }
    }

    public abstract Class<P> getPresenterClass();


    @Override
    protected void onResume() {
        App.activeActivity = this;
        super.onResume();
    }

    @Override
    protected void onPause() {
        App.activeActivity = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        App.removeActivity(this);
        super.onDestroy();
    }
}

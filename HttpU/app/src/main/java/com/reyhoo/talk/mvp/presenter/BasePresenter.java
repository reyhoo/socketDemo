package com.reyhoo.talk.mvp.presenter;

import android.view.View;
import android.widget.Toast;

import com.reyhoo.talk.mvp.model.BaseModel;
import com.reyhoo.talk.mvp.view.BaseViewDelegate;

/**
 * Created by Administrator on 2016/7/29.
 */
public abstract class BasePresenter<V extends BaseViewDelegate, M extends BaseModel> {

    public V mView;
    public M mModel;

    public BasePresenter() {
        try {
            mModel =getModelClass().newInstance();
            mView = getViewClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getClass()+":创建失败",e);
        }
    }


    public void setOnClickListener(View.OnClickListener listener,int... ids){
        mView.setOnClickListener(listener,ids);
    }

    public abstract Class<M> getModelClass();

    public abstract Class<V>getViewClass();

}

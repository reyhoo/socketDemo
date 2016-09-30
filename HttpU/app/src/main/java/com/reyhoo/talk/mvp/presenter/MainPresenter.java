package com.reyhoo.talk.mvp.presenter;

import com.reyhoo.talk.mvp.model.EmptyModel;
import com.reyhoo.talk.mvp.view.MainViewDelegate;

/**
 * Created by Administrator on 2016/7/29.
 */
public class MainPresenter extends BasePresenter<MainViewDelegate,EmptyModel> {
    @Override
    public Class<EmptyModel> getModelClass() {
        return EmptyModel.class;
    }

    @Override
    public Class<MainViewDelegate> getViewClass() {
        return MainViewDelegate.class;
    }
}

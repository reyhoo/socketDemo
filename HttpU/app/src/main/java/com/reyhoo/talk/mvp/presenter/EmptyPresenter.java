package com.reyhoo.talk.mvp.presenter;

import com.reyhoo.talk.mvp.model.EmptyModel;
import com.reyhoo.talk.mvp.view.EmptyViewDelegate;

/**
 * Created by Administrator on 2016/7/29.
 */
public class EmptyPresenter extends BasePresenter<EmptyViewDelegate,EmptyModel> {
    @Override
    public Class<EmptyModel> getModelClass() {
        return EmptyModel.class;
    }

    @Override
    public Class<EmptyViewDelegate> getViewClass() {
        return EmptyViewDelegate.class;
    }
}

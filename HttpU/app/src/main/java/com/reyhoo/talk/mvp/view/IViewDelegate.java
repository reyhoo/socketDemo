package com.reyhoo.talk.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/7/29.
 */
public interface IViewDelegate {

    void initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    int getMainLayoutId();

    View getMainView();

    void initActionBar();

    int getPageTitleId();
}

package com.reyhoo.talk.mvp.view;

import com.reyhoo.talk.R;

/**
 * Created by Administrator on 2016/7/29.
 */
public class MainViewDelegate extends BaseViewDelegate {
    @Override
    public int getMainLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public int getPageTitleId() {
        return R.string.app_name;
    }
}

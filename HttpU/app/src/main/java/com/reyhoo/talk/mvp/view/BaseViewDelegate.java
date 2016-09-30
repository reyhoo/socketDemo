package com.reyhoo.talk.mvp.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.reyhoo.talk.R;

/**
 * Created by Administrator on 2016/7/29.
 */
public abstract class BaseViewDelegate implements IViewDelegate {

    private SparseArray<View> mViews = new SparseArray<>();

    private View mainView;

    private boolean isInitedActionBar =false;

    protected ActionBar mActionBar;
    @Override
    public void initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getMainLayoutId();
        if(layoutId == 0){
            mainView = inflater.inflate(R.layout.empty_page,container,false);
        }else{
            mainView = inflater.inflate(getMainLayoutId(),container,false);
        }
    }
    public void toast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void startActivity(Intent intent){
        getActivity().startActivity(intent);
    }
    public void startActivityFinishSelf(Intent intent){
        getActivity().startActivity(intent);
        getActivity().finish();
    }
    public void startActivityForResult(Intent intent,int requestCode){
        getActivity().startActivityForResult(intent,requestCode);
    }
    public Intent getIntent(Class< ? extends Activity> cls){
        Intent intent = new Intent(getActivity(),cls);
        return intent;
    }


    @Override
    public void initActionBar() {
        if(!isInitedActionBar){
            isInitedActionBar = true;
            mActionBar = getActivity().getActionBar();
            if(mActionBar == null){
                return;
            }
            mActionBar.setDisplayUseLogoEnabled(false);
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);

            mActionBar.setDisplayShowCustomEnabled(true);

            mActionBar.setCustomView(R.layout.title_layout);

            if(0 != getPageTitleId()){
               TextView titleTv = (TextView) mActionBar.getCustomView().findViewById(R.id.title_tv);
                titleTv.setText(getPageTitleId());
            }
        }
    }

    public void setGoBackListener(View.OnClickListener listener){
        if(mActionBar == null){
            return;
        }
        View actionBarCustomView = mActionBar.getCustomView();
        if(actionBarCustomView != null){
            View goBackBtn = actionBarCustomView.findViewById(getGoBackButtonId());
            if (goBackBtn != null) {
                goBackBtn.setOnClickListener(listener);
            }
        }
    }

    public int getGoBackButtonId(){
        return R.id.title_left_btn;
    }
    @Override
    public View getMainView() {
        return mainView;
    }

    public <T extends View> T get(int id){
        return bindView(id);
    }

    public void setOnClickListener(View.OnClickListener listener,int... ids){
        if(ids!=null){
            for (int id:ids){
                View v = get(id);
                if(v!=null){
                    v.setOnClickListener(listener);
                }
            }
        }
    }


    private <T extends View> T bindView(int id){
        T view = (T) mViews.get(id);
        if(view == null){
            view = (T) mainView.findViewById(id);
            mViews.put(id,view);
        }
        return view;
    }

    public <T extends Activity> T getActivity(){
        return (T) mainView.getContext();
    }
}

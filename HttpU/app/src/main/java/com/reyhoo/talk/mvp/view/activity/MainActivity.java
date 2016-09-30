package com.reyhoo.talk.mvp.view.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.reyhoo.talk.R;
import com.reyhoo.talk.entity.Message;
import com.reyhoo.talk.mvp.model.TalkModel;
import com.reyhoo.talk.mvp.presenter.MainPresenter;
import com.reyhoo.talk.util.GlobalConst;

import org.w3c.dom.Text;

public class MainActivity extends BaseActivity<MainPresenter> {


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = (Message) intent.getSerializableExtra(GlobalConst.INTENT_EXTRA_MSG_KEY);
            tv.append(msg+"\n");


        }
    };
    private EditText et;
    private TextView tv;
    private Button btn;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        et = (EditText) findViewById(R.id.msg_et);
        tv = (TextView) findViewById(R.id.msg_tv);
        btn = (Button) findViewById(R.id.send_btn);
        tv.setText("");
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String msg = et.getText().toString().trim();
                if(TextUtils.isEmpty(msg)){
                    return;
                }
                new TalkModel().sendMsg(msg,1);
                et.setText("");
            }
        });
        IntentFilter filter = new IntentFilter(GlobalConst.BROADCAST_ACTION_PUSH_MSG);
        registerReceiver(receiver,filter);
    }

    @Override
    public Class<MainPresenter> getPresenterClass() {
        return MainPresenter.class;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}

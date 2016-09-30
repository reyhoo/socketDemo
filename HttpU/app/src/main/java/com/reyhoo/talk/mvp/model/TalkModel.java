package com.reyhoo.talk.mvp.model;

import android.util.Log;

import com.reyhoo.talk.component.Req;
import com.reyhoo.talk.component.Resp;
import com.reyhoo.talk.component.TalkEngine;
import com.reyhoo.talk.entity.Message;
import com.reyhoo.talk.util.GlobalConst;

/**
 * Created by Administrator on 2016/8/5.
 */
public class TalkModel {

    private static final String TAG = "TalkModel";
    public void sendMsg(String msgBody,int toId){
        if(!UserAccountModel.isLogin()){
            Log.i(TAG, TAG+":sendMsg::user id not login");
            return;
        }
        Req<Message>req = new Req<>();
        req.type = GlobalConst.CMD_SENDMSG;
        Message msg = new Message();
        msg.setFrom(UserAccountModel.getLoginUser().getId());
        msg.setTo(toId);
        msg.setMsgBody(msgBody);
        req.content = msg;
        TalkEngine.getInstance().sendData(req, new TalkEngine.RequestCallback() {
            @Override
            public void onSent(Req req) {
                Log.i(TAG, TAG+":onSent::");
            }

            @Override
            public void onSendException(Exception e) {
                Log.i(TAG, TAG+":onSendException::"+e);
            }

            @Override
            public void onConnectionClosed() {
                Log.i(TAG, TAG+":onConnectionClosed::");
            }

            @Override
            public void onReadTimeout() {
                Log.i(TAG, TAG+":onReadTimeout::");
            }

            @Override
            public void onReceive(Resp resp) {
                Log.i(TAG, TAG+":onReceive::"+resp);
            }
        });
    }
}

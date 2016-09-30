package com.reyhoo.talk;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

import com.reyhoo.talk.component.TalkEngine;
import com.reyhoo.talk.mvp.view.activity.LoginActivity;
import com.reyhoo.talk.util.AlarmUtil;
import com.reyhoo.talk.util.DialogUtil;
import com.reyhoo.talk.util.GlobalConst;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/14.
 */
public class App extends Application {

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GlobalConst.BROADCAST_ACTION_PUSH_LOGOUT.equals(intent.getAction())){
                App.showOfflineDialog("其它设备登录当前账号，请重新登录");
            }
        }
    };
    public static App instance;
    private HandlerThread handlerThread;
    private Handler workHandler;
    private static List<Activity> activities = new ArrayList<>();

    public static Activity activeActivity;
    public static void addActivity(Activity act){
        activities.add(act);
    }
    public static void removeActivity(Activity act){
        activities.remove(act);
    }
    public static boolean exitAllActivity(){
        if(activities.isEmpty()){
            return false;
        }
        for (int i = activities.size()-1;i >= 0;i --){
            Activity act = activities.remove(i);
            act.finish();
        }
        return true;
    }

    public static void showOfflineDialog(String msg){
        if(activeActivity == null){
            return;
        }

        DialogUtil.showTipDialog(activeActivity, "下线通知", msg, new Runnable() {
            @Override
            public void run() {
                offlineToLoginActivity();
            }
        });
    }
    public static void offlineToLoginActivity(){
        boolean flag = exitAllActivity();
        if(flag){
            Intent intent = new Intent(instance,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            instance.startActivity(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AlarmUtil.startRepeat(this,5,MyService.class,null);
        instance = this;
        handlerThread = new HandlerThread("workThread");
        handlerThread.start();
        workHandler = new Handler(handlerThread.getLooper());
        TalkEngine.getInstance().connectServer();
        IntentFilter filter = new IntentFilter(GlobalConst.BROADCAST_ACTION_PUSH_LOGOUT);
        registerReceiver(receiver,filter);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public synchronized Handler getHandler() {
        return workHandler;
    }
}

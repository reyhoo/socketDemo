package com.reyhoo.talk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.reyhoo.talk.util.AlarmUtil;

/**
 * Created by Administrator on 2016/8/1.
 */
public class MyService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MyService","MyService::onStartCommand():");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        startService(new Intent(this,MyService.class));
        super.onDestroy();
    }
}

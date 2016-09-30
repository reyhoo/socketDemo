package com.reyhoo.talk.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/22.
 */
public class AlarmUtil {

    public static void startRepeat(Context context,int seconds,Class<?>cls ,String action){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,cls);
        PendingIntent pendingIntent = PendingIntent.getService(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerAtTime = System.currentTimeMillis();
        long clockTime = SystemClock.elapsedRealtime();
        Log.i("AlarmUtil","triggerAtTime:"+triggerAtTime+";clockTime:"+clockTime);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,triggerAtTime,seconds*1000,pendingIntent);
    }
}

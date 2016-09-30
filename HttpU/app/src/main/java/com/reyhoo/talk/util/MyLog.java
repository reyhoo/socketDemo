package com.reyhoo.talk.util;

import android.util.Log;

/**
 * Created by Administrator on 2016/7/19.
 */
public class MyLog {

    private static final String TAG = "Talk" ;
    public static void i(String t,String msg){

        String tag = TAG;
        if( t!=null && !"".equals( t.trim())){
            tag = t;
        }
        Log.i(tag,msg);
    }
    public static void i(String msg){
        i(null,msg);
    }
}

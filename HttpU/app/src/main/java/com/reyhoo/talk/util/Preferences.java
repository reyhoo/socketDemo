package com.reyhoo.talk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Administrator on 2016/7/20.
 */
public class Preferences {


    private SharedPreferences preferences;

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    public void setPreString(String key,String value){
        preferences.edit().putString(key,value).commit();
    }
    public String getPreString(String key){
        return preferences.getString(key,"");
    }

}

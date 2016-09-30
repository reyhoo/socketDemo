package com.reyhoo.talk.util;

/**
 * Created by Administrator on 2016/7/11.
 */
public interface HttpCallback<T> {
    void onResult(int statusCode, T data);
    void onException(Exception e);
    void onProgress(long writencount, long totalcount);
}

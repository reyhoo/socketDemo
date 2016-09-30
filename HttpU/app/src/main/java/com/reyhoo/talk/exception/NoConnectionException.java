package com.reyhoo.talk.exception;

/**
 * Created by Administrator on 2016/7/22.
 */
public class NoConnectionException extends Exception{

    public NoConnectionException() {
    }

    public NoConnectionException(String detailMessage) {
        super(detailMessage);
    }

    public NoConnectionException(Throwable throwable) {
        super(throwable);
    }

    public NoConnectionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}

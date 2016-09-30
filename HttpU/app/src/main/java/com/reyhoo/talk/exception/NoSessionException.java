package com.reyhoo.talk.exception;

/**
 * Created by Administrator on 2016/7/22.
 */
public class NoSessionException extends Exception {
    public NoSessionException() {
        super();
    }

    public NoSessionException(Throwable throwable) {
        super(throwable);
    }

    public NoSessionException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public NoSessionException(String detailMessage) {
        super(detailMessage);
    }
}

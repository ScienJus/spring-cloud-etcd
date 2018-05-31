package com.scienjus.spring.cloud.etcd.exception;

public class EtcdOperationException extends RuntimeException {

    public EtcdOperationException(String message) {
        super(message);
    }

    public EtcdOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EtcdOperationException(Throwable cause) {
        super(cause);
    }

    protected EtcdOperationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

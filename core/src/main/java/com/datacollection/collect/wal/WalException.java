package com.datacollection.collect.wal;

/**
 * TODO: Class description here.
 *
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public class WalException extends RuntimeException {

    public WalException() {
    }

    public WalException(String message) {
        super(message);
    }

    public WalException(String message, Throwable cause) {
        super(message, cause);
    }

    public WalException(Throwable cause) {
        super(cause);
    }
}

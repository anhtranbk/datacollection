package com.datacollection.exc;

public class TaskErrorExceedLimitException extends RuntimeException {

    public TaskErrorExceedLimitException() {
    }

    public TaskErrorExceedLimitException(String message) {
        super(message);
    }

    public TaskErrorExceedLimitException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskErrorExceedLimitException(Throwable cause) {
        super(cause);
    }
}

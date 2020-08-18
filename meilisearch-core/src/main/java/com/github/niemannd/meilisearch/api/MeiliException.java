package com.github.niemannd.meilisearch.api;

public class MeiliException extends RuntimeException {
    public MeiliException() {
    }

    public MeiliException(String message) {
        super(message);
    }

    public MeiliException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeiliException(Throwable cause) {
        super(cause);
    }

    public MeiliException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

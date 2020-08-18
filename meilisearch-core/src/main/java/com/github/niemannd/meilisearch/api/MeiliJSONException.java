package com.github.niemannd.meilisearch.api;

public class MeiliJSONException extends MeiliException {
    public MeiliJSONException() {
    }

    public MeiliJSONException(String message) {
        super(message);
    }

    public MeiliJSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeiliJSONException(Throwable cause) {
        super(cause);
    }

    public MeiliJSONException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

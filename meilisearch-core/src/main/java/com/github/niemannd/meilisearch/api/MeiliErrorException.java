package com.github.niemannd.meilisearch.api;

public class MeiliErrorException extends RuntimeException {
    private MeiliError error;

    public MeiliErrorException(MeiliError error, Throwable cause) {
        super(cause);
        this.error = error;
    }

    public MeiliErrorException(String message, MeiliError error) {
        super(message);
        this.error = error;
    }

    public MeiliErrorException(Throwable cause) {
        super(cause);
    }

    public MeiliErrorException(String message) {
        super(message);
    }

    public MeiliError getError() {
        return error;
    }
}

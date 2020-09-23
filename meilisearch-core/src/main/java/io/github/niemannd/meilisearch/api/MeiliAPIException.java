package io.github.niemannd.meilisearch.api;

public class MeiliAPIException extends MeiliException {
    private final MeiliError error;

    public MeiliAPIException(String message, MeiliError error) {
        super(message);
        this.error = error;
    }

    public MeiliAPIException(Throwable cause) {
        super(cause);
        this.error = null;
    }

    public MeiliAPIException(String message) {
        super(message);
        this.error = null;
    }

    public MeiliError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}

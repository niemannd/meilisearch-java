package com.github.niemannd.meilisearch.api;

public class MeiliError {
    private String name;
    private String message;
    private String stack;
    private String errorCode;
    private String errorType;
    private String errorLink;

    public MeiliError() {
    }

    public MeiliError(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorLink() {
        return errorLink;
    }

    public void setErrorLink(String errorLink) {
        this.errorLink = errorLink;
    }
}

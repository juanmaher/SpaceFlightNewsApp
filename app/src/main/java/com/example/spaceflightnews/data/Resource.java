package com.example.spaceflightnews.data;

public class Resource<T> {
    public enum Status { SUCCESS, ERROR, LOADING }

    public final Status mStatus;
    public final T mData;
    public final String mMessage;

    private Resource(Status status, T data, String message) {
        this.mStatus = status;
        this.mData = data;
        this.mMessage = message;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg) {
        return new Resource<>(Status.ERROR, null, msg);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }
}

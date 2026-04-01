package com.example.spaceflightnews.repository;

public interface RepositoryCallback {
    void onSuccess();
    void onError(String message);
}

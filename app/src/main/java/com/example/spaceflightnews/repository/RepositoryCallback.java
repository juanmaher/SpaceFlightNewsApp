package com.example.spaceflightnews.repository;

/**
 * Callback to report the result of a network operation.
 */
public interface RepositoryCallback {
    void onSuccess();
    void onError(String message);
}

package com.example.spaceflightnews.repository;

import androidx.lifecycle.LiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.data.ArticleDao;
import com.example.spaceflightnews.data.ArticleResponse;
import com.example.spaceflightnews.data.SpaceFlightApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class that abstracts access to multiple data sources.
 * It manages the query flow between the Room local database and the Retrofit API service.
 */
public class ArticleRepository {
    private final ArticleDao mArticleDao;
    private final SpaceFlightApiService mApiService;

    public ArticleRepository(ArticleDao articleDao, SpaceFlightApiService api) {
        this.mArticleDao = articleDao;
        this.mApiService = api;
    }

    /**
     * Performs a search for articles.
     * It triggers a network refresh while simultaneously returning local results.
     * @param query The search term.
     * @param callback Interface to notify the caller about network success or failure.
     * @return LiveData list of articles matching the query from the local DB.
     */
    public LiveData<List<Article>> search(String query, RepositoryCallback callback) {
        refreshArticlesByQuery(query, callback);
        // Use SQL wildcards for the LIKE operator
        return mArticleDao.searchArticles("%" + query + "%");
    }

    /**
     * Synchronizes the latest articles from the API to the local database.
     * @param callback To report operation status.
     */
    public void syncArticles(RepositoryCallback callback) {
        executeRequest(mApiService.getArticles(), callback);
    }

    /**
     * Internal method to refresh the cache based on a specific search term.
     */
    private void refreshArticlesByQuery(String query, RepositoryCallback callback) {
        executeRequest(mApiService.getArticles(query), callback);
    }

    /**
     * Fetches a single article by ID from the local database.
     */
    public LiveData<Article> getArticleById(int id) {
        return mArticleDao.getArticleById(id);
    }

    /**
     * Retrieves the most recent articles stored in the local cache.
     */
    public LiveData<List<Article>> getRecentArticles() {
        return mArticleDao.getRecentArticles();
    }

    /**
     * Generic helper to execute Retrofit calls and handle network responses.
     */
    private void executeRequest(Call<ArticleResponse> call, RepositoryCallback callback) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    processAndSave(response.body().results, callback);
                } else {
                    callback.onError("Server error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                callback.onError("No internet connection");
            }
        });
    }

    /**
     * Persists API results into the Room database on a background thread.
     */
    private void processAndSave(List<Article> articles, RepositoryCallback callback) {
        new Thread(() -> {
            try {
                // Room handles the update or insertion of articles
                mArticleDao.insertArticles(articles);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Database error: " + e.getMessage());
            }
        }).start();
    }
}
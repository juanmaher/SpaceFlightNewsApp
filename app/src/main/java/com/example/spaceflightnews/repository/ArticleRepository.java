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

public class ArticleRepository {
    private final ArticleDao mArticleDao;
    private final SpaceFlightApiService mApiService;

    public ArticleRepository(ArticleDao articleDao, SpaceFlightApiService api) {
        this.mArticleDao = articleDao;
        this.mApiService = api;
    }

    public LiveData<List<Article>> search(String query, RepositoryCallback callback) {
        refreshArticlesByQuery(query, callback);
        return mArticleDao.searchArticles("%" + query + "%");
    }

    public void syncArticles(RepositoryCallback callback) {
        executeRequest(mApiService.getArticles(), callback);
    }

    private void refreshArticlesByQuery(String query, RepositoryCallback callback) {
        executeRequest(mApiService.getArticles(query), callback);
    }

    public LiveData<Article> getArticleById(int id) {
        return mArticleDao.getArticleById(id);
    }

    public LiveData<List<Article>> getRecentArticles() {
        return mArticleDao.getRecentArticles();
    }

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

    private void processAndSave(List<Article> articles, RepositoryCallback callback) {
        new Thread(() -> {
            try {
                mArticleDao.insertArticles(articles);
                callback.onSuccess();
            } catch (Exception e) {
                callback.onError("Database error: " + e.getMessage());
            }
        }).start();
    }
}

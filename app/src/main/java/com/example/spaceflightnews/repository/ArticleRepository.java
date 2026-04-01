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
    private final ArticleDao articleDao;
    private final SpaceFlightApiService apiService;

    public ArticleRepository(ArticleDao articleDao, SpaceFlightApiService api) {
        this.articleDao = articleDao;
        this.apiService = api;
    }

    public LiveData<List<Article>> search(String query, RepositoryCallback callback) {
        refreshArticlesByQuery(query, callback);
        return articleDao.searchArticles("%" + query + "%");
    }

    private void refreshArticlesByQuery(String query, RepositoryCallback callback) {
        apiService.getArticles(query).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        articleDao.insertArticles(response.body().results);
                        callback.onSuccess();
                    }).start();
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

    public LiveData<Article> getArticleById(int id) {
        return articleDao.getArticleById(id);
    }

    public void syncArticles(RepositoryCallback callback) {
        apiService.getArticles().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        articleDao.insertArticles(response.body().results);
                        callback.onSuccess();
                    }).start();
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

    public LiveData<List<Article>> getRecentArticles() {
        return articleDao.getRecentArticles();
    }
}

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

    public LiveData<List<Article>> search(String query) {
        // 1. Disparamos la búsqueda a la API de forma asíncrona
        refreshArticlesByQuery(query);

        // 2. Retornamos el LiveData de la DB local.
        // En cuanto la API responda y guarde en el DAO, este LiveData se activará.
        return articleDao.searchArticles("%" + query + "%");
    }

    private void refreshArticlesByQuery(String query) {
        apiService.getArticles(query).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        // Guardamos los resultados frescos de la API en SQLite
                        articleDao.insertArticles(response.body().results);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                // Manejar error de red si es necesario
            }
        });
    }

    public LiveData<Article> getArticleById(int id) {
        return articleDao.getArticleById(id);
    }

    public void syncArticles() {
        apiService.getArticles().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    new Thread(() -> {
                        articleDao.insertArticles(response.body().results);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable throwable) {
            }
        });
    }

    public LiveData<List<Article>> getRecentArticles() {
        return articleDao.getRecentArticles();
    }
}

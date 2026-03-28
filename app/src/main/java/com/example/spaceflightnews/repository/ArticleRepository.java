package com.example.spaceflightnews.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.data.ArticleDao;
import com.example.spaceflightnews.data.ArticleResponse;
import com.example.spaceflightnews.data.NetworkModule;
import com.example.spaceflightnews.data.SpaceFlightDatabase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleRepository {
    private final ArticleDao articleDao;

    public ArticleRepository(Context context) {
        // Obtenemos la instancia de la DB y extraemos el DAO
        SpaceFlightDatabase db = SpaceFlightDatabase.getDatabase(context);
        this.articleDao = db.articleDao();
    }

    // Retorna el observable de la base de datos
    public LiveData<List<Article>> getAllArticles() {
        return articleDao.getAllArticles();
    }

    public LiveData<List<Article>> search(String query) {
        return articleDao.searchArticles("%" + query + "%");
    }

    public LiveData<Article> getArticleById(int id) {
        return articleDao.getArticleById(id);
    }

    public void syncArticles() {
        // Llamada a la API de Spaceflight
        NetworkModule.getApiService().getArticles().enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Ejecutar en hilo secundario para no bloquear la UI
                    new Thread(() -> {
                        // "INSERT OR REPLACE" asegura que los datos existentes se actualicen
                        articleDao.insertArticles(response.body().results);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable throwable) {}
        });
    }
}

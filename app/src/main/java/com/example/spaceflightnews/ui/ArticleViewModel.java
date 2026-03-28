package com.example.spaceflightnews.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.repository.ArticleRepository;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {
    private ArticleRepository repository;
    private LiveData<List<Article>> allArticles;

    public ArticleViewModel(@NonNull Application application) {
        super(application);
        repository = new ArticleRepository(application);
        allArticles = repository.getAllArticles();
    }

    public LiveData<List<Article>> getAllArticles() {
        return allArticles;
    }

    public LiveData<List<Article>> searchArticles(String query) {
        return repository.search(query);
    }

    public LiveData<Article> getArticleById(int id) {
        return repository.getArticleById(id);
    }

    public void sync() {
        repository.syncArticles();
    }
}

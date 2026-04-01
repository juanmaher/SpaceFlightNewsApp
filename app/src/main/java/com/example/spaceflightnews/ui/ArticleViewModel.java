package com.example.spaceflightnews.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.repository.ArticleRepository;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {
    private final ArticleRepository mRepository;

    public ArticleViewModel(@NonNull Application application, ArticleRepository repository) {
        super(application);
        mRepository = repository;
    }

    public LiveData<List<Article>> searchArticles(String query) {
        return mRepository.search(query);
    }

    public LiveData<Article> getArticleById(int id) {
        return mRepository.getArticleById(id);
    }

    public LiveData<List<Article>> getRecentArticles() {
        return mRepository.getRecentArticles();
    }

    public void sync() {
        mRepository.syncArticles();
    }
}

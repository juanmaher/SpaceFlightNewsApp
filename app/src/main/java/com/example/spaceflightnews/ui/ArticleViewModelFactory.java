package com.example.spaceflightnews.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceflightnews.repository.ArticleRepository;

public class ArticleViewModelFactory implements ViewModelProvider.Factory {
    private final Application application;
    private final ArticleRepository repository;

    public ArticleViewModelFactory(Application application, ArticleRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArticleViewModel.class)) {
            return (T) new ArticleViewModel(application, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

package com.example.spaceflightnews.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceflightnews.repository.ArticleRepository;

/**
 * Factory class to create ArticleViewModel instances.
 */
public class ArticleViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final ArticleRepository mRepository;

    public ArticleViewModelFactory(Application application, ArticleRepository repository) {
        this.mApplication = application;
        this.mRepository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ArticleViewModel.class)) {
            return (T) new ArticleViewModel(mApplication, mRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

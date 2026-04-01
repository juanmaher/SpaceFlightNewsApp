package com.example.spaceflightnews.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.data.Resource;
import com.example.spaceflightnews.repository.ArticleRepository;
import com.example.spaceflightnews.repository.RepositoryCallback;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {
    private final ArticleRepository mRepository;
    private final MutableLiveData<Resource<List<Article>>> articlesStatus = new MutableLiveData<>();

    public ArticleViewModel(@NonNull Application application, ArticleRepository repository) {
        super(application);
        mRepository = repository;
    }

    public LiveData<List<Article>> searchArticles(String query) {
        return mRepository.search(query, new RepositoryCallback() {
            @Override
            public void onSuccess() {
                articlesStatus.postValue(Resource.success(null));
            }

            @Override
            public void onError(String error) {
                articlesStatus.postValue(Resource.error(error));
            }
        });
    }

    public LiveData<Article> getArticleById(int id) {
        return mRepository.getArticleById(id);
    }

    public LiveData<List<Article>> getRecentArticles() {
        return mRepository.getRecentArticles();
    }

    public void fetchArticles() {
        articlesStatus.setValue(Resource.loading());

        mRepository.syncArticles(new RepositoryCallback() {
            @Override
            public void onSuccess() {
                articlesStatus.postValue(Resource.success(null));
            }

            @Override
            public void onError(String error) {
                articlesStatus.postValue(Resource.error(error));
            }
        });
    }

    public LiveData<Resource<List<Article>>> getArticlesStatus() {
        return articlesStatus;
    }
}

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

/**
 * ViewModel responsible for managing UI-related data for Space Flight News.
 * It acts as a bridge between the Activity/Fragment and the ArticleRepository,
 * handling data streams and network/database operation statuses.
 */
public class ArticleViewModel extends AndroidViewModel {
    private final ArticleRepository mRepository;

    /**
     * Observable state for network operations (Loading, Success, Error).
     * Used by the UI to show/hide progress bars or display error messages.
     */
    private final MutableLiveData<Resource<List<Article>>> mArticlesStatus = new MutableLiveData<>();

    public ArticleViewModel(@NonNull Application application, ArticleRepository repository) {
        super(application);
        mRepository = repository;
    }

    /**
     * Triggers a search query through the repository.
     * @param query The search term entered by the user.
     * @return An observable list of articles matching the criteria from the local database.
     */
    public LiveData<List<Article>> searchArticles(String query) {
        mArticlesStatus.setValue(Resource.loading());
        return mRepository.search(query, createStatusCallback());
    }

    /**
     * Retrieves a single article by its unique identifier.
     * @param id The primary key of the article.
     * @return LiveData containing the requested Article.
     */
    public LiveData<Article> getArticleById(int id) {
        return mRepository.getArticleById(id);
    }

    /**
     * Provides a list of the most recent articles available in the local cache.
     * @return LiveData list of Articles sorted by publication date.
     */
    public LiveData<List<Article>> getRecentArticles() {
        return mRepository.getRecentArticles();
    }

    /**
     * Initiates a remote synchronization to fetch the latest news from the API.
     * Updates mArticlesStatus during the process.
     */
    public void fetchArticles() {
        mArticlesStatus.setValue(Resource.loading());
        mRepository.syncArticles(createStatusCallback());
    }

    /**
     * Exposes the current status of background operations.
     * @return The current Resource state (LOADING, SUCCESS, or ERROR).
     */
    public LiveData<Resource<List<Article>>> getArticlesStatus() {
        return mArticlesStatus;
    }

    /**
     * Unified helper to handle repository responses and update the UI status accordingly.
     */
    private RepositoryCallback createStatusCallback() {
        return new RepositoryCallback() {
            @Override
            public void onSuccess() {
                mArticlesStatus.postValue(Resource.success(null));
            }

            @Override
            public void onError(String error) {
                mArticlesStatus.postValue(Resource.error(error));
            }
        };
    }
}
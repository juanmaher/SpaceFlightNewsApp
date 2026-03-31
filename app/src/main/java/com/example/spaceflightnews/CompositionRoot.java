package com.example.spaceflightnews;

import android.app.Application;

import com.example.spaceflightnews.data.ArticleDao;
import com.example.spaceflightnews.data.NetworkModule;
import com.example.spaceflightnews.data.SpaceFlightApiService;
import com.example.spaceflightnews.data.SpaceFlightDatabase;
import com.example.spaceflightnews.repository.ArticleRepository;
import com.example.spaceflightnews.ui.ArticleViewModelFactory;

public class CompositionRoot {

    public final ArticleRepository mRepository;
    public final ArticleViewModelFactory mFactory;

    public CompositionRoot(Application application) {
        SpaceFlightDatabase db = SpaceFlightDatabase.getDatabase(application);
        ArticleDao articleDao = db.articleDao();
        SpaceFlightApiService apiService = NetworkModule.getApiService();
        mRepository = new ArticleRepository(articleDao, apiService);
        mFactory = new ArticleViewModelFactory(application, mRepository);
    }
}

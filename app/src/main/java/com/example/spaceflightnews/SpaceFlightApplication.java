package com.example.spaceflightnews;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.spaceflightnews.repository.ArticleRepository;
import com.example.spaceflightnews.ui.ArticleViewModel;
import com.example.spaceflightnews.ui.ArticleViewModelFactory;

public class SpaceFlightApplication extends Application {

    private CompositionRoot mCompositionRoot;

    @Override
    public void onCreate() {
        super.onCreate();
        mCompositionRoot = new CompositionRoot(this);
    }

    public CompositionRoot getCompositionRoot() {
        return mCompositionRoot;
    }
}

package com.example.spaceflightnews.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceflightnews.CompositionRoot;
import com.example.spaceflightnews.R;
import com.example.spaceflightnews.SpaceFlightApplication;
import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.ui.ArticleAdapter;
import com.example.spaceflightnews.ui.ArticleViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArticleViewModel mViewModel;
    private ArticleAdapter mAdapter;
    private LiveData<List<Article>> mCurrentSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();
        setupViewModel();
        observeViewModelStatus();

        // Initial data load
        showRecentArticles();
        mViewModel.fetchArticles();

        setupSearchView();
    }

    /**
     * Initializes the RecyclerView with a vertical layout manager and custom adapter.
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ArticleAdapter();
        recyclerView.setAdapter(mAdapter);
        setupAdapterClick();
    }

    /**
     * Sets up the ViewModel using a custom Factory provided by the CompositionRoot.
     */
    private void setupViewModel() {
        CompositionRoot compositionRoot = ((SpaceFlightApplication) getApplication()).getCompositionRoot();
        mViewModel = new ViewModelProvider(this, compositionRoot.mFactory).get(ArticleViewModel.class);
    }

    /**
     * Observer for Network/Repository status (Loading, Success, Error).
     */
    private void observeViewModelStatus() {
        ProgressBar progressBar = findViewById(R.id.main_progress_bar);
        mViewModel.getArticlesStatus().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.mStatus) {
                case LOADING:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    break;
                case ERROR:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    /**
     * Switches the UI back to the main article list view.
     */
    public void showList() {
        toggleViewVisibility(true);
    }

    /**
     * Sets up the click listener for items in the RecyclerView.
     * Navigates to the ArticleDetailFragment with a slide-in animation.
     */
    private void setupAdapterClick() {
        mAdapter.setOnItemClickListener(article -> {
            // Prevent multiple rapid clicks if a fragment is already loading
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) return;

            toggleViewVisibility(false);

            ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(article.id);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right, R.anim.fade_out,
                            R.anim.fade_in, R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    /**
     * Configures the SearchView appearance and query listeners.
     */
    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        customizeSearchViewIcons(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) updateSubscription(mViewModel.searchArticles(query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) showRecentArticles();
                return true;
            }
        });
    }

    /**
     * Applies custom theme colors to the internal components of the SearchView.
     */
    private void customizeSearchViewIcons(SearchView searchView) {
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(getResources().getColor(R.color.text_dark_blue, getTheme()));
            searchEditText.setHintTextColor(getResources().getColor(R.color.text_soft_gray, getTheme()));
        }

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) searchIcon.setColorFilter(getResources().getColor(R.color.text_soft_gray, getTheme()));

        ImageView closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeBtn != null) closeBtn.setColorFilter(getResources().getColor(R.color.text_soft_gray, getTheme()));
    }

    /**
     * Loads and observes the most recent articles from the local database.
     */
    private void showRecentArticles() {
        updateSubscription(mViewModel.getRecentArticles());
    }

    /**
     * Unified method to handle LiveData swapping.
     * Removes old observers before attaching to a new data source to prevent leaks.
     */
    private void updateSubscription(LiveData<List<Article>> newLiveData) {
        if (mCurrentSubscription != null) {
            mCurrentSubscription.removeObservers(this);
        }
        mCurrentSubscription = newLiveData;
        mCurrentSubscription.observe(this, articles -> mAdapter.setArticles(articles));
    }

    /**
     * Toggles visibility between the main list and the detail fragment container.
     * @param showList If true, shows the list; otherwise shows the fragment container.
     */
    private void toggleViewVisibility(boolean showList) {
        findViewById(R.id.main_list_container).setVisibility(showList ? View.VISIBLE : View.GONE);
        findViewById(R.id.fragment_container).setVisibility(showList ? View.GONE : View.VISIBLE);
    }
}

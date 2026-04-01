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
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;
    private LiveData<List<Article>> currentSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticleAdapter();
        recyclerView.setAdapter(adapter);
        setupAdapterClick();

        CompositionRoot compositionRoot = ((SpaceFlightApplication) getApplication()).getCompositionRoot();
        viewModel = new ViewModelProvider(this, compositionRoot.mFactory).get(ArticleViewModel.class);

        showRecentArticles();

        ProgressBar progressBar = findViewById(R.id.main_progress_bar);
        viewModel.getArticlesStatus().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
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

        viewModel.fetchArticles();
        setupSearchView();
    }

    public void showList() {
        findViewById(R.id.main_list_container).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    private void setupAdapterClick() {
        adapter.setOnItemClickListener(article -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) return;

            findViewById(R.id.main_list_container).setVisibility(View.GONE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(article.id);

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.fade_out,
                            R.anim.fade_in,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(getResources().getColor(R.color.text_dark_blue, getTheme()));
            searchEditText.setHintTextColor(getResources().getColor(R.color.text_soft_gray, getTheme()));
        }

        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocus();
        });

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setColorFilter(getResources().getColor(R.color.text_soft_gray, getTheme()));
        }

        ImageView closeBtn = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeBtn != null) {
            closeBtn.setColorFilter(getResources().getColor(R.color.text_soft_gray, getTheme()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    subscribeToSearch(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showRecentArticles();
                }
                return true;
            }
        });
    }

    private void showRecentArticles() {
        if (currentSubscription != null) {
            currentSubscription.removeObservers(this);
        }

        currentSubscription = viewModel.getRecentArticles();
        currentSubscription.observe(this, articles -> {
            adapter.setArticles(articles);
        });
    }

    private void subscribeToSearch(String query) {
        if (currentSubscription != null) {
            currentSubscription.removeObservers(this);
        }

        ProgressBar progressBar = findViewById(R.id.main_progress_bar);
        viewModel.getArticlesStatus().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
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

        currentSubscription = viewModel.searchArticles(query);
        currentSubscription.observe(this, articles -> {
            adapter.setArticles(articles);
        });
    }
}

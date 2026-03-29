package com.example.spaceflightnews;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.ui.ArticleAdapter;
import com.example.spaceflightnews.ui.ArticleViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;
    private LiveData<List<Article>> currentSubscription; // Para rastrear qué estamos viendo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ArticleAdapter();
        recyclerView.setAdapter(adapter);
        /*adapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("ARTICLE_ID", article.id);
            startActivity(intent);
        });*/

        adapter.setOnItemClickListener(article -> {
            ArticleDetailSheet sheet = ArticleDetailSheet.newInstance(article.id);
            sheet.show(getSupportFragmentManager(), "detail_sheet");
        });

        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        showRecentArticles();
        viewModel.sync();

        setupSearchView();
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
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
                    showRecentArticles(); // Volver a los 10 recientes si borra el texto
                }
                return true;
            }
        });
    }

    private void showRecentArticles() {
        // Removemos el observador anterior para que no se mezclen las listas
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

        // El repository disparará la API y Room actualizará este LiveData
        currentSubscription = viewModel.searchArticles(query);
        currentSubscription.observe(this, articles -> {
            adapter.setArticles(articles);
        });
    }
}

package com.example.spaceflightnews;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceflightnews.data.Article;
import com.example.spaceflightnews.ui.ArticleAdapter;
import com.example.spaceflightnews.ui.ArticleViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ArticleViewModel viewModel;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Configurar búsqueda
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Si borra la búsqueda, volvemos a mostrar todo
                    observeAllArticles();
                } else {
                    performSearch(newText);
                }
                return true;
            }
        });

        adapter.setOnItemClickListener(article -> {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            // Pasamos el ID del artículo
            intent.putExtra("ARTICLE_ID", article.id);
            startActivity(intent);
        });

        // Obtener el ViewModel
        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        // OBSERVAR los datos: Cuando cambie la DB, este bloque se ejecuta solo
        viewModel.getAllArticles().observe(this, articles -> {
            if (articles != null) {
                adapter.setArticles(articles);
            }
        });

        // Sincronizar al iniciar
        viewModel.sync();
    }

    // Método auxiliar para cambiar la observación
    private void performSearch(String query) {
        // IMPORTANTE: Quitamos el observador anterior si existe para no duplicar
        viewModel.searchArticles(query).observe(this, results -> {
            if (results != null) {
                adapter.setArticles(results);
            }
        });
    }

    private void observeAllArticles() {
        viewModel.getAllArticles().observe(this, articles -> {
            adapter.setArticles(articles);
        });
    }
}


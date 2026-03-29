package com.example.spaceflightnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
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
        setupAdapterClick();

        viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);
        showRecentArticles();
        viewModel.sync();

        setupSearchView();
    }

    public void showList() {
        findViewById(R.id.main_list_container).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    private void setupAdapterClick() {
        adapter.setOnItemClickListener(article -> {
            // Si ya hay un fragmento mostrándose, no hagas nada
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) return;

            // 1. Ocultar la lista (opcional, el fragmento la tapará)
            findViewById(R.id.main_list_container).setVisibility(View.GONE);
            findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

            // 2. Crear instancia del fragmento pasándole el ID
            ArticleDetailFragment detailFragment = ArticleDetailFragment.newInstance(article.id);

            // 3. Transacción con animación fluida
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animación al entrar
                            R.anim.fade_out,        // Animación de la lista al salir
                            R.anim.fade_in,         // Animación de la lista al volver
                            R.anim.slide_out_right  // Animación del fragment al salir
                    )
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null) // Vital para que el botón 'Atrás' funcione
                    .commit();
        });
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(getResources().getColor(R.color.text_dark_blue));
            searchEditText.setHintTextColor(getResources().getColor(R.color.text_soft_gray));
        }

        searchView.setOnClickListener(v -> {
            searchView.setIconified(false);
            searchView.requestFocus();
        });

        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (searchIcon != null) {
            searchIcon.setColorFilter(getResources().getColor(R.color.text_soft_gray));
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

package com.example.spaceflightnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceflightnews.ui.ArticleViewModel;

public class ArticleDetailFragment extends Fragment {

    public static ArticleDetailFragment newInstance(int id) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putInt("ARTICLE_ID", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);

        Toolbar toolbar = v.findViewById(R.id.detail_toolbar);

        // Al hacer clic en la flecha de volver
        toolbar.setNavigationOnClickListener(view -> {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Primero: Volver a mostrar la lista en la Activity
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showList();
                }

                // Segundo: Quitar este callback para que no interfiera más
                this.remove();

                // Tercero: Ejecutar el atrás real del FragmentManager
                getParentFragmentManager().popBackStack();
            }
        };

        // El Owner asegura que esto muera con el fragmento
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        int articleId = getArguments().getInt("ARTICLE_ID");
        ArticleViewModel viewModel = new ViewModelProvider(requireActivity()).get(ArticleViewModel.class);

        viewModel.getArticleById(articleId).observe(getViewLifecycleOwner(), article -> {
            if (article != null) {
                ((TextView) v.findViewById(R.id.detail_title)).setText(article.title);
                ((TextView) v.findViewById(R.id.detail_summary)).setText(article.summary);
                ((TextView) v.findViewById(R.id.detail_date)).setText(article.publishedAt);
            }
        });

        return v;
    }
}

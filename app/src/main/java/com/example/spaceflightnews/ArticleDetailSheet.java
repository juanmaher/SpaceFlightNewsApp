package com.example.spaceflightnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceflightnews.ui.ArticleViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ArticleDetailSheet extends BottomSheetDialogFragment {
    private int articleId;

    public static ArticleDetailSheet newInstance(int id) {
        ArticleDetailSheet fragment = new ArticleDetailSheet();
        Bundle args = new Bundle();
        args.putInt("ID", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_detail, container, false);
        articleId = getArguments().getInt("ID");

        // Usamos el mismo ViewModel de la Activity (requireActivity)
        ArticleViewModel viewModel = new ViewModelProvider(requireActivity()).get(ArticleViewModel.class);

        viewModel.getArticleById(articleId).observe(getViewLifecycleOwner(), article -> {
            if (article != null) {
                ((TextView)v.findViewById(R.id.detail_title)).setText(article.title);
                ((TextView)v.findViewById(R.id.detail_summary)).setText(article.summary);
            }
        });
        return v;
    }
}

package com.example.spaceflightnews.pages;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.spaceflightnews.CompositionRoot;
import com.example.spaceflightnews.R;
import com.example.spaceflightnews.SpaceFlightApplication;
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
        toolbar.setNavigationOnClickListener(view -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getActivity() instanceof MainActivity) { ((MainActivity) getActivity()).showList(); }
                this.remove();
                getParentFragmentManager().popBackStack();
            }
        });

        Drawable backArrow = toolbar.getNavigationIcon();
        if (backArrow != null) {
            backArrow.setTint(ContextCompat.getColor(requireContext(), R.color.text_dark_blue));
            toolbar.setNavigationIcon(backArrow);
        }

        int articleId = getArguments().getInt("ARTICLE_ID");
        SpaceFlightApplication app = (SpaceFlightApplication) requireActivity().getApplication();
        CompositionRoot compositionRoot = app.getCompositionRoot();
        ArticleViewModel viewModel = new ViewModelProvider(this, compositionRoot.mFactory).get(ArticleViewModel.class);

        viewModel.getArticleById(articleId).observe(getViewLifecycleOwner(), article -> {
            if (article != null) {
                ((TextView) v.findViewById(R.id.detail_title)).setText(article.title);
                ((TextView) v.findViewById(R.id.detail_summary)).setText(article.summary);
                ((TextView) v.findViewById(R.id.detail_date)).setText(article.publishedAt);

                ImageView imageView = v.findViewById(R.id.detail_image);
                Glide.with(this).load(article.imageUrl).into(imageView);

                v.findViewById(R.id.btn_open_url).setOnClickListener(view -> {

                    CustomTabColorSchemeParams defaultColors = new CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(ContextCompat.getColor(requireContext(), R.color.pastel_blue_medium))
                            .build();

                    CustomTabsIntent intent = new CustomTabsIntent.Builder()
                            .setDefaultColorSchemeParams(defaultColors)
                            .build();
                    intent.launchUrl(requireContext(), Uri.parse(article.url));
                });
            }
        });

        return v;
    }
}

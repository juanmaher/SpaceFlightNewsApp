package com.example.spaceflightnews;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.spaceflightnews.ui.ArticleViewModel;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int articleId = getIntent().getIntExtra("ARTICLE_ID", -1);

        ArticleViewModel viewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        viewModel.getArticleById(articleId).observe(this, article -> {
            if (article != null) {
                ((TextView)findViewById(R.id.detail_title)).setText(article.title);
                ((TextView)findViewById(R.id.detail_date)).setText(article.publishedAt);
                ((TextView)findViewById(R.id.detail_summary)).setText(article.summary);
            }
        });
    }
}
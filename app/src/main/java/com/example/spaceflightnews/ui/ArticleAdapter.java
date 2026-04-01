package com.example.spaceflightnews.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceflightnews.R;
import com.example.spaceflightnews.data.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleViewHolder> {

    private final List<Article> articles = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article current = articles.get(position);
        holder.title.setText(current.title);
        holder.summaryPreview.setText(current.summary);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(current);
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setArticles(List<Article> newArticles) {
        ArticleDiffCallback diffCallback = new ArticleDiffCallback(this.articles, newArticles);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.articles.clear();
        this.articles.addAll(newArticles);

        diffResult.dispatchUpdatesTo(this);
    }
}

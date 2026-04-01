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

    private final List<Article> mArticles = new ArrayList<>();
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
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
        Article current = mArticles.get(position);
        holder.mTitle.setText(current.title);
        holder.mSummaryPreview.setText(current.summary);

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) mListener.onItemClick(current);
        });
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void setArticles(List<Article> newArticles) {
        ArticleDiffCallback diffCallback = new ArticleDiffCallback(this.mArticles, newArticles);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.mArticles.clear();
        this.mArticles.addAll(newArticles);

        diffResult.dispatchUpdatesTo(this);
    }
}

package com.example.spaceflightnews.ui;

import androidx.recyclerview.widget.DiffUtil;

import com.example.spaceflightnews.data.Article;

import java.util.List;

/**
 * DiffUtil.Callback implementation to optimize RecyclerView updates.
 */
public class ArticleDiffCallback extends DiffUtil.Callback {

    private final List<Article> mOldList;
    private final List<Article> mNewList;

    public ArticleDiffCallback(List<Article> oldList, List<Article> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).id == mNewList.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Article oldArticle = mOldList.get(oldItemPosition);
        Article newArticle = mNewList.get(newItemPosition);

        return oldArticle.title.equals(newArticle.title) &&
                oldArticle.summary.equals(newArticle.summary);
    }
}
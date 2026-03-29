package com.example.spaceflightnews.ui;

import androidx.recyclerview.widget.DiffUtil;

import com.example.spaceflightnews.data.Article;

import java.util.List;

public class ArticleDiffCallback extends DiffUtil.Callback {

    private final List<Article> oldList;
    private final List<Article> newList;

    public ArticleDiffCallback(List<Article> oldList, List<Article> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Comparamos por ID único (Primary Key de SQLite)
        return oldList.get(oldItemPosition).id == newList.get(newItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Comparamos si el contenido visual cambió
        Article oldArticle = oldList.get(oldItemPosition);
        Article newArticle = newList.get(newItemPosition);

        return oldArticle.title.equals(newArticle.title) &&
                oldArticle.summary.equals(newArticle.summary);
    }
}
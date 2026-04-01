package com.example.spaceflightnews.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceflightnews.R;

/**
 * ViewHolder for a single article item in the RecyclerView.
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder {
    public final TextView mTitle, mSummaryPreview;

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        mTitle = itemView.findViewById(R.id.text_title);
        mSummaryPreview = itemView.findViewById(R.id.text_summary_preview);
    }
}

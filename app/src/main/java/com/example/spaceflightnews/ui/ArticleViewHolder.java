package com.example.spaceflightnews.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spaceflightnews.R;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    TextView title, summaryPreview;

    public ArticleViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.text_title);
        summaryPreview = itemView.findViewById(R.id.text_summary_preview);
    }
}

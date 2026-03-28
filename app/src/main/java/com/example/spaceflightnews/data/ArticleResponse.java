package com.example.spaceflightnews.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArticleResponse {
    @SerializedName("results")
    public List<Article> results;
}

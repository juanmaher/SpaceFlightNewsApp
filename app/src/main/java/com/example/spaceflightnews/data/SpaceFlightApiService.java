package com.example.spaceflightnews.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SpaceFlightApiService {

    @GET("articles/")
    Call<ArticleResponse> getArticles();

    @GET("articles/{id}/")
    Call<Article> getArticleById(@Path("id") int id);
}


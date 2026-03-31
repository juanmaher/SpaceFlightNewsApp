package com.example.spaceflightnews.data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpaceFlightApiService {

    @GET("articles/")
    Call<ArticleResponse> getArticles();

    @GET("articles/")
    Call<ArticleResponse> getArticles(@Query("search") String searchTerm);
}


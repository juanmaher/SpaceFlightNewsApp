package com.example.spaceflightnews.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertArticles(List<Article> articles);

    // LiveData notificará a la UI cada vez que la tabla 'articles' se actualice
    @Query("SELECT * FROM articles")
    LiveData<List<Article>> getAllArticles();

    @Query("SELECT * FROM articles WHERE title LIKE :query OR summary LIKE :query")
    LiveData<List<Article>> searchArticles(String query);

    @Query("SELECT * FROM articles WHERE id = :id")
    LiveData<Article> getArticleById(int id);
}
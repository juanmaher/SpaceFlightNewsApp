package com.example.spaceflightnews.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "articles")
public class Article {
    @PrimaryKey
    @SerializedName("id")
    public int id;

    @SerializedName("title")
    public String title;

    @SerializedName("summary")
    public String summary;

    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @SerializedName("published_at")
    @ColumnInfo(name = "published_at")
    public String publishedAt;
}
package com.example.spaceflightnews.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Article.class}, version = 1)
public abstract class SpaceFlightDatabase extends RoomDatabase {

    public abstract ArticleDao articleDao();

    private static java.util.concurrent.ExecutorService databaseWriteExecutor =
            java.util.concurrent.Executors.newFixedThreadPool(4);

    private static volatile SpaceFlightDatabase INSTANCE;

    public static SpaceFlightDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SpaceFlightDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SpaceFlightDatabase.class, "spaceflight_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

package com.example.imagesearch.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//@Database(entities = {SearchResultModel.class}, version = 1, exportSchema = false)
public abstract class SearchResultDatabase extends RoomDatabase {

    public abstract SearchResultDao searchResultDao();

    private static volatile SearchResultDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static SearchResultDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SearchResultDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SearchResultDatabase.class, "search_result_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
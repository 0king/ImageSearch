package com.example.imagesearch.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import io.reactivex.rxjava3.core.Observable;

//@Dao
public interface SearchResultDao {

    /*@Query("SELECT * FROM search_results " +
            "WHERE search_term = :searchTerm " +
            " AND " +
            "page_number = :pageNumber")
    Observable<SearchResultModel> getResponse(String searchTerm, int pageNumber);*/

    //@Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SearchResultModel model);

}

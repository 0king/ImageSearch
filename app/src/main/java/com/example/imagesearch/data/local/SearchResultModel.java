package com.example.imagesearch.data.local;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;

import com.example.imagesearch.data.model.FlickrResponse;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SearchResultModel extends RealmObject {

    @PrimaryKey
    private long id = System.currentTimeMillis();

    private String searchTerm;

    private int pageNumber;

    private FlickrResponse flickrResponse;

    public SearchResultModel(){}

    public SearchResultModel(String searchTerm, int pageNumber, FlickrResponse flickrResponse) {
        this.searchTerm = searchTerm;
        this.pageNumber = pageNumber;
        this.flickrResponse = flickrResponse;
    }

    //getters

    public String getSearchTerm() {
        return searchTerm;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public FlickrResponse getResponse() {
        return flickrResponse;
    }

    //setters
    public void setSearchTerm(@NonNull String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setResponse(FlickrResponse flickrResponse) {
        this.flickrResponse = flickrResponse;
    }
}

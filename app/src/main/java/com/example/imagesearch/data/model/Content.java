package com.example.imagesearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Content extends RealmObject {

    @PrimaryKey
    //@ColumnInfo()
    @Expose @SerializedName("page")
    private int pageNumber;

    //@ColumnInfo()
    @Expose @SerializedName("pages")
    private int totalPages;

    //@ColumnInfo()
    @Expose @SerializedName("perpage")
    private int perPage;

    //@ColumnInfo()
    @Expose @SerializedName("total")
    private int total;

    //@ColumnInfo() @TypeConverters(DataTypeConverter.class)
    @Expose @SerializedName("photo")
    private RealmList<Photo> photosList;
    //private List<Photo> photosList;

    //getters
    public int getPageNumber() {
        return pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPerPage() {
        return perPage;
    }

    public int getTotal() {
        return total;
    }

    public List<Photo> getPhotosList() {
        return photosList;
    }

    //setters
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    /*public void setPhotosList(List<Photo> photosList) {
        this.photosList = photosList;
    }*/
}

package com.example.imagesearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class FlickrResponse extends RealmObject {

    //@ColumnInfo()
    @Expose @SerializedName("photos")
    private Content photos;

    //@ColumnInfo()
    @Expose @SerializedName("stat")
    private String status;

    public Content getPhotos() {
        return photos;
    }

    public String getStatus() {
        return status;
    }

    public void setPhotos(Content photos) {
        this.photos = photos;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

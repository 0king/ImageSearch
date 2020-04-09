package com.example.imagesearch.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Photo extends RealmObject {
    private static final String IMAGE_URL = "https://farm%s.staticflickr.com/%s/%s_%s_%s.jpg";
    //https://farm1.staticflickr.com/2/1418878_1e92283336_m.jpg
    //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
    //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_[mstzb].jpg

    @PrimaryKey
    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("secret")
    private String secret;

    @Expose
    @SerializedName("server")
    private String server;

    @Expose
    @SerializedName("farm")
    private int farm;

    @Expose
    @SerializedName("owner")
    private String owner;

    @Expose
    @SerializedName("title")
    private String title;

    @Expose
    @SerializedName("ispublic")
    private int ispublic;

    @Expose
    @SerializedName("isfamily")
    private int isfamily;

    @Expose
    @SerializedName("isfriend")
    private int isfriend;

    public String getUrl(ImageSize size) {
        return String.format(IMAGE_URL, farm, server, id, secret, size.getValue());
    }

    public enum ImageSize {
        SMALL, MEDIUM, LARGE;
        public String getValue() {
            switch (this){
                case LARGE: return "b";
                case SMALL: return "q";
                case MEDIUM: return "z";
                default: return "q";
            }
        }
    }
}

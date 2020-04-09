package com.example.imagesearch;

import android.app.Application;

import io.realm.Realm;

public class ImageSearchApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //init realm once
        Realm.init(this);
    }
}

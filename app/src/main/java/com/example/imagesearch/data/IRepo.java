package com.example.imagesearch.data;

import com.example.imagesearch.data.model.FlickrResponse;

import io.reactivex.rxjava3.core.Single;

public interface IRepo {
    Single<FlickrResponse> getPhotosResponse(String searchTerm, int pageNumber);
}

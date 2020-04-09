package com.example.imagesearch.data.remote;

import com.example.imagesearch.data.model.FlickrResponse;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {

    int PAGE_SIZE = 20;
    String API_KEY = "a9420e12df8ab60cfa922a4bf8f27219";
    String URL_BASE = "https://api.flickr.com";
    String URL_SEARCH = "services/rest/" +
            "?format=json&nojsoncallback=1" +
            "&api_key=" + API_KEY +
            "&method=flickr.photos.search" +
            "&per_page=" + PAGE_SIZE;

    //https://api.flickr.com
    // /services/rest/
    // ?format=json
    // &nojsoncallback=1&
    // api_key=a9420e12df8ab60cfa922a4bf8f27219
    // &method=flickr.photos.search
    // &per_page=10
    // &page=1
    // &text=hello

    @GET(URL_SEARCH)
    Observable<FlickrResponse> getPhotosResponse(@Query("text") String searchTerm, @Query("page") int pageNumber);

}

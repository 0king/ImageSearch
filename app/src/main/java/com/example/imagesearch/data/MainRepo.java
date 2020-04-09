package com.example.imagesearch.data;

import android.app.Application;
import android.util.Log;

import com.example.imagesearch.data.local.SearchResultModel;
import com.example.imagesearch.data.model.FlickrResponse;
import com.example.imagesearch.data.remote.FlickrApi;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.realm.Case;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum MainRepo implements IRepo {
    INSTANCE; //singleton

    private FlickrApi mFlickrApi;

    MainRepo(){
        initRetrofit();
    }

    @Override
    public Single<FlickrResponse> getPhotosResponse(String searchTerm, int pageNumber){
        FlickrResponse fs = getDataFromRealm(searchTerm, pageNumber);//todo - debug why run on main thread
        Maybe<FlickrResponse> diskMaybe = Maybe.create(emitter -> {
            if (!emitter.isDisposed()){
                if ((fs!=null)) {
                    emitter.onSuccess(fs);
                }
                else {
                    emitter.onComplete(); return;
                }
            }
        });
        Observable<FlickrResponse> network = mFlickrApi.getPhotosResponse(searchTerm, pageNumber)
                .doOnNext(response -> {
                    saveToRealm(new SearchResultModel(searchTerm, pageNumber, response));
                });
        return Observable.concat(diskMaybe.toObservable(), network)
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toSingle();
    }

    private FlickrResponse getDataFromRealm(String searchTerm, int pageNumber){
        Realm r = Realm.getDefaultInstance();
        SearchResultModel res = r.where(SearchResultModel.class)
                .contains("searchTerm", searchTerm, Case.INSENSITIVE)
                .and().equalTo("pageNumber", pageNumber)
                .findFirst();
        if (res==null) return null;
        else return res.getResponse();
    }

    private void saveToRealm(SearchResultModel model){
        Realm r = Realm.getDefaultInstance();
        r.executeTransaction(realm -> {
            realm.insert(model);
            //log("realm inserted");
        });
    }

    private void initRetrofit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FlickrApi.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                //.client(client)
                .build();
        mFlickrApi = retrofit.create(FlickrApi.class);
    }

    private void log(String msg){
        Log.d("durga", msg);
    }

}

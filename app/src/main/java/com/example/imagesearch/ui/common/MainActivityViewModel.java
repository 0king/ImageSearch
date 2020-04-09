package com.example.imagesearch.ui.common;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.imagesearch.data.MainRepo;
import com.example.imagesearch.data.model.Photo;
import com.example.imagesearch.data.model.StateLiveData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivityViewModel extends ViewModel {

    private int mPageNumber = 1;
    private String mSearchTerm;
    private List<Photo> mPhotoList = new ArrayList<>();
    private boolean mIsLoading = false;

    private StateLiveData<List<Photo>> mStateList = new StateLiveData<>();
    private CompositeDisposable mDisposable = new CompositeDisposable();

    /*public MainActivityViewModel(@NonNull Application application) {
        super(application);
        //do not trigger load call on rotation
    }*/

    //get list of all images
    public List<Photo> getAllPhotoList(){
        return mPhotoList;
    }

    public int getPageNumber(){
        return mPageNumber;
    }

    public boolean isLoading(){
        return mIsLoading;
    }

    private void increasePageNumber(){
        mPageNumber++;
    }

    //get new list of images
    StateLiveData<List<Photo>> getPhotoListEmitter(){
        //loadPhotoList(searchTerm, pageNumber);
        return mStateList;
    }

    public void loadPhotoList(String searchTerm){
        mSearchTerm = searchTerm;
        resetData();
        loadMore(searchTerm, 1);
    }

    public void loadMore(){
        loadMore(mSearchTerm, mPageNumber);
    }

    private void loadMore(String searchTerm, int pageNumber){
        //log("load more, page = " + pageNumber);
        mStateList.postLoading();
        mIsLoading = true;
        Disposable d = MainRepo.INSTANCE.getPhotosResponse(searchTerm, pageNumber)
                .subscribe(response -> {
                    //log("response = " );
                    if (response!=null){
                        //log("response not null");
                        //log(response.getPhotos().getPageNumber()+"");
                        //log("gson=" + new Gson().toJson(response.getPhotos().getPhotosList().size()));
                        List<Photo> l = response.getPhotos().getPhotosList();
                        mPhotoList.addAll(l);
                        increasePageNumber();
                        mStateList.postSuccess(l);
                    }
                    mIsLoading = false;
                    new Handler().postDelayed(() -> mStateList.postComplete(), 100);//todo debug why?
                }, err -> {
                    //log("error");
                    mStateList.postError(err);
                    //mStateList.postComplete();
                    new Handler().postDelayed(() -> mStateList.postComplete(), 100);
                    mIsLoading = false;
                });
        mDisposable.add(d);
    }

    private void resetData(){
        mPhotoList.clear();
        mPageNumber = 1;
    }

    void log(String msg){
        Log.d("durga", msg);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDisposable.dispose();
    }
}

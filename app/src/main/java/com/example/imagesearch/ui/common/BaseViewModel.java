package com.example.imagesearch.ui.common;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.imagesearch.data.MainRepo;
import com.example.imagesearch.data.model.Photo;
import com.example.imagesearch.data.model.StateLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class BaseViewModel extends ViewModel {

    private int mRVColumnCount = 2;
    private int mRVCurrentPosition=0;

    private int mPageNumber = 1;
    private String mSearchTerm;
    private List<Photo> mAllPhotoList = new ArrayList<>();
    private boolean mIsLoading = false;

    private StateLiveData<List<Photo>> mStateList = new StateLiveData<>();
    private CompositeDisposable mDisposable = new CompositeDisposable();

    //get list of all images
    public List<Photo> getAllPhotoList(){
        return mAllPhotoList;
    }

    public int getPageNumber(){
        return mPageNumber;
    }

    public boolean isLoading(){
        return mIsLoading;
    }

    public void increasePageNumber(){
        mPageNumber++;
    }

    //get new list of images
    public StateLiveData<List<Photo>> getPhotoListEmitter(){
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
        mStateList.postLoading();
        mIsLoading = true;
        Disposable d = MainRepo.INSTANCE.getPhotosResponse(searchTerm, pageNumber)
                .subscribe(response -> {
                    if (response!=null){
                        List<Photo> l = response.getPhotos().getPhotosList();
                        mAllPhotoList.addAll(l);
                        increasePageNumber();
                        mStateList.postSuccess(l);
                    }
                    mIsLoading = false;
                    new Handler().postDelayed(() -> mStateList.postComplete(), 100);
                }, err -> {
                    mStateList.postError(err);
                    new Handler().postDelayed(() -> mStateList.postComplete(), 100);
                    mIsLoading = false;
                });
        mDisposable.add(d);
    }

    private void resetData(){
        mAllPhotoList.clear();
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

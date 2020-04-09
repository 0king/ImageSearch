package com.example.imagesearch.data.model;

import androidx.lifecycle.MutableLiveData;

public class StateLiveData<T> extends MutableLiveData<Resource<T>> {

    /**
     * Use this to put the Data on a LOADING Status
     */
    public void postLoading() {
        postValue(new Resource<T>().loading());
    }

    /**
     * Use this to put the Data on a ERROR DataStatus
     * @param throwable the error to be handled
     */
    public void postError(Throwable throwable) {
        postValue(new Resource<T>().error(throwable));
    }

    /**
     * Use this to put the Data on a SUCCESS DataStatus
     * @param data
     */
    public void postSuccess(T data) {
        //set value or post value?
        postValue(new Resource<T>().success(data));
    }

    /**
     * Use this to put the Data on a COMPLETE DataStatus
     */
    public void postComplete() {
        postValue(new Resource<T>().complete());
    }

}

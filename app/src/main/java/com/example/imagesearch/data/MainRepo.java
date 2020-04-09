package com.example.imagesearch.data;

import android.app.Application;
import android.util.Log;

import com.example.imagesearch.data.local.SearchResultDao;
import com.example.imagesearch.data.local.SearchResultDatabase;
import com.example.imagesearch.data.local.SearchResultModel;
import com.example.imagesearch.data.model.FlickrResponse;
import com.example.imagesearch.data.remote.FlickrApi;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum MainRepo {
    INSTANCE; //singleton
    //private static MainRepo instance = null;

    private FlickrApi mFlickrApi;
    //private SearchResultDao mSearchResultDao = null;
    //private Application mApplication;

    MainRepo(){
        initRetrofit();
    }

    /*public static MainRepo getInstance(){
        if (instance==null){
            instance = new MainRepo();
            //mApplication = a;
        }
        return instance;
    }*/

    public Single<FlickrResponse> getPhotosResponse(String searchTerm, int pageNumber){
        //log(mFlickrApi.getPhotosResponse(searchTerm, pageNumber));

        //Observable<FlickrResponse> disk = getDataFromDisk(searchTerm, pageNumber);
        FlickrResponse fs = getDataFromRealm(searchTerm, pageNumber);//todo - debug why run on main thread
        //log("fs thread name= " +Thread.currentThread());
        //fs thread name= Thread[main,5,main]
        Maybe<FlickrResponse> diskMaybe = Maybe.create(emitter -> {
            //log("maybe thread name= " +Thread.currentThread());
            //maybe thread name= Thread[RxCachedThreadScheduler-1,5,main]
            if (!emitter.isDisposed()){
                if ((fs!=null)) {
                    log("fs found");
                    emitter.onSuccess(fs);
                }
                else {
                    log("fs not found");
                    emitter.onComplete(); return;
                }
            }
        });

        Observable<FlickrResponse> network = mFlickrApi.getPhotosResponse(searchTerm, pageNumber)
                .doOnNext(response -> {
                    //log("do on next thread name= " +Thread.currentThread());
                    //do on next thread name= Thread[RxCachedThreadScheduler-1,5,main]
                    //saveResponseToDisk(new SearchResultModel(searchTerm, pageNumber, response));
                    log("saveToRealm=" + searchTerm + pageNumber);
                    saveToRealm(new SearchResultModel(searchTerm, pageNumber, response));
                });

        return Observable.concat(diskMaybe.toObservable(), network)
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toSingle();

        /*return realmSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());*/

        /*return mFlickrApi.getPhotosResponse(searchTerm, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());*/

        /*Single<FlickrResponse> realmSingle = Single.create(emitter -> {
            if (fs==null){
                log("fs not found");
                //emitter.onError(new Exception("emitter err = fs not found"));
            }
            else {
                log("fs found");
                emitter.onSuccess(fs);
            }
        });*/

        /*Realm r = Realm.getDefaultInstance();
        io.reactivex.Observable<RealmObject> realm = r.where(SearchResultModel.class)
                .contains("searchTerm", searchTerm)
                .and()
                .equalTo("pageNumber", pageNumber)
                .findFirstAsync().asFlowable().toObservable();*/
    }

    private FlickrResponse getDataFromRealm(String searchTerm, int pageNumber){
        Realm r = Realm.getDefaultInstance();
        log("getDataFromRealm=" + searchTerm + pageNumber);
        SearchResultModel res = r.where(SearchResultModel.class)
                .contains("searchTerm", searchTerm, Case.INSENSITIVE)
                //.findAll().where().equalTo("pageNumber", pageNumber)
                .and().equalTo("pageNumber", pageNumber)
                //.findAllAsync()
                .findFirst(); //IllegalStateException: Async query cannot be created on current thread.
        // Realm cannot be automatically updated on a thread without a looper.
        //r.close();
        if (res==null) return null;
        else return res.getResponse();
    }

    private void saveToRealm(SearchResultModel model){
        Realm r = Realm.getDefaultInstance();
        r.executeTransactionAsync(realm -> {
            realm.insert(model);
        });
        //r.close();
    }


    /*private Observable<FlickrResponse> getDataFromDisk2(String searchTerm, int pageNumber){
        if (mSearchResultDao==null)
            initDb(mApplication);
        return mSearchResultDao.getResponse(searchTerm, pageNumber)
                .flatMap(model -> Observable.just(model.getResponse()));
    }*/

    /*// Combining Realm, Retrofit and RxJava (Using Retrolambda syntax for brevity)
// Load all persons and merge them with their latest stats from GitHub (if they have any)
Realm realm = Realm.getDefaultInstance();
GitHubService api = retrofit.create(GitHubService.class);
realm.where(Person.class).isNotNull("username").findAllAsync().asFlowable()
    .filter(persons.isLoaded)
    .flatMap(persons -> Observable.from(persons))
    .flatMap(person -> api.user(person.getGithubUserName())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(user -> showUser(user));
    */

    private void saveResponseToDisk(SearchResultModel model){
        //if (mSearchResultDao==null) initDb(mApplication);
        SearchResultDatabase.databaseWriteExecutor.execute(()->{
            //mSearchResultDao.insert(model);
        });
    }

    void log(String msg){
        Log.d("durga", msg);
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

    private void initDb(Application a){
        SearchResultDatabase db = SearchResultDatabase.getDatabase(a);
        //mSearchResultDao = db.searchResultDao();
        //mAllMessages = mMessageDao.getAll();
    }

}

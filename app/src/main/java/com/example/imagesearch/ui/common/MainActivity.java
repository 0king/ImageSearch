package com.example.imagesearch.ui.common;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagesearch.R;
import com.example.imagesearch.data.model.Photo;
import com.example.imagesearch.data.model.Resource;
import com.example.imagesearch.ui.common.BaseActivity;
import com.example.imagesearch.ui.common.BaseViewModel;
import com.example.imagesearch.ui.common.Router;
import com.example.imagesearch.ui.slider.FullPhotoActivity;
import com.example.imagesearch.ui.util.ItemDecorationAlbumColumns;
import com.example.imagesearch.ui.util.SquareGridLayoutManager;
import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION = "currentPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ActivityCompat.postponeEnterTransition(this);

        setContentView(R.layout.activity_main);
        Router.INSTANCE.init(getSupportFragmentManager());
        if (savedInstanceState==null){
            Router.INSTANCE.showHomeScreen(this.getApplication());
        }
        else {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
        }

        //initUi();
        //observeData();
        //addScrollListener();
        //log("main thread name= " +Thread.currentThread());
        //main thread name= Thread[main,5,main]
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }




    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem mSearch = menu.findItem(R.id.appSearchBar);
        mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //showToast("done");
                if (query.isEmpty())
                    return false;
                mViewModel.loadPhotoList(query);
                hideKeyboard();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
        //return true;
    }*/

    @Override
    protected void onRestart() {
        super.onRestart();
        //mSearchView.clearFocus();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.options:
                View v = findViewById(R.id.options);
                showPopupMenu(v.getContext(), v);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    //@Override
    public void onItemClick(View v, Photo photo) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, v, "imageMain");
        Intent in = new Intent(this, FullPhotoActivity.class);
        in.putExtra("PHOTO_URL", photo.getUrl(Photo.ImageSize.LARGE));
        startActivity(in, options.toBundle());
    }

    // Size in bytes (100 MB)
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 100; //250000000

    private void initPicasso(){
        // Create memory cache
        Cache memoryCache = new LruCache(100000);

        Picasso picasso =  new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(getCacheDir(), DISK_CACHE_SIZE))
                //.memoryCache(memoryCache)
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    void log(String msg){
        Log.d("durga", msg);
    }

    private int dpToPx(int dp){
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}

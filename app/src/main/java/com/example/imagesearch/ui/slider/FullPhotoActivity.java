package com.example.imagesearch.ui.slider;

import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.example.imagesearch.R;
import com.example.imagesearch.ui.common.BaseActivity;
import com.example.imagesearch.ui.common.BaseViewModel;

public class FullPhotoActivity extends BaseActivity {

    //private ImageView mImageView;
    private ViewPager2 mPager;
    private BaseViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo);

        //mViewModel = getSharedViewModel();

        //mImageView = findViewById(R.id.ivPhoto);
        mPager = findViewById(R.id.view_pager);

        mPager.setAdapter(new TabsPagerAdapter(this, mViewModel.getAllPhotoList()));
        log("2nd=" + mViewModel.getAllPhotoList().size());

        //String url = getIntent().getStringExtra("PHOTO_URL");
        //url = getIntent().getExtras().getString("PHOTO_URL");

        //log("URL = " + url);
        //todo - make shared transition perfect fit

    }

    void log(String msg){
        Log.d("durga", msg);
    }
}

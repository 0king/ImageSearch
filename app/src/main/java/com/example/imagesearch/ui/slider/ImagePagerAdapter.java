package com.example.imagesearch.ui.slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.imagesearch.data.model.Photo;

import java.util.List;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private List<Photo> mList;

    public ImagePagerAdapter(Fragment fragment, List<Photo> list) {
        // Note: Initialize with the child fragment manager.
        super(fragment.getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FullPhotoFragment.newInstance(mList.get(position).getUrl(Photo.ImageSize.SMALL));
    }
}
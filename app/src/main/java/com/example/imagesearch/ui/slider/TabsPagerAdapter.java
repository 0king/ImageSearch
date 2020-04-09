package com.example.imagesearch.ui.slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.imagesearch.data.model.Photo;

import java.util.List;

public class TabsPagerAdapter extends FragmentStateAdapter {

    private List<Photo> mList;

    public TabsPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Photo> list) {
        super(fragmentActivity);
        mList = list;
    }

    public TabsPagerAdapter(@NonNull Fragment fragment, List<Photo> list) {
        super(fragment);
        mList = list;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return FullPhotoFragment.newInstance(mList.get(position).getUrl(Photo.ImageSize.SMALL));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}

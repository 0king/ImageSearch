package com.example.imagesearch.ui.common;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.example.imagesearch.R;
import com.example.imagesearch.data.model.Photo;
import com.example.imagesearch.ui.home.HomeFragment;
import com.example.imagesearch.ui.slider.FullPhotoFragment;
import com.example.imagesearch.ui.slider.ViewPagerFragment;

public enum Router {
    INSTANCE;

    private FragmentManager fm;

    public void init(FragmentManager fm){
        this.fm = fm;
    }

    public static final String FRAGMENT_HOME = "FRAGMENT_HOME";
    public static final String FRAGMENT_FULL = "FRAGMENT_FULL";

    public void showHomeScreen(Context c){
        if (fm==null)
            return;
        //throw new Exception("Please initialize the Navigator");

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_HOME);
        if (fragment==null){
            fragment = new HomeFragment();
        }

        Transition changeTransform = TransitionInflater.from(c).
                inflateTransition(R.transition.change_image_transform);
        Transition explodeTransform = TransitionInflater.from(c).
                inflateTransition(android.R.transition.move);

        // Setup exit transition on first fragment
        fragment.setSharedElementReturnTransition(changeTransform);
        fragment.setExitTransition(explodeTransform);

        fm.beginTransaction()
                .replace(R.id.main_container, fragment, FRAGMENT_HOME)
                .commit();
    }

    public void showFullPhotoScreen(int itemPosition, View sharedView){
        if (fm==null)
            return;
        //throw new Exception("Please initialize the Navigator");

        Fragment fragment = fm.findFragmentByTag(FRAGMENT_FULL);
        if (fragment==null){
            //fragment = FullPhotoFragment.newInstance(photo.getUrl(Photo.ImageSize.LARGE));
            fragment = ViewPagerFragment.newInstance(itemPosition);
        }
        /*fm.beginTransaction()
                .replace(R.id.main_container, fragment, FRAGMENT_FULL)
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();*/
        Context c = sharedView.getContext();
        Transition changeTransform = TransitionInflater.from(c).
                inflateTransition(R.transition.change_image_transform);
        Transition explodeTransform = TransitionInflater.from(c).
                inflateTransition(android.R.transition.move);

        // Setup exit transition on first fragment
        //fragmentOne.setSharedElementReturnTransition(changeTransform);
        //fragmentOne.setExitTransition(explodeTransform);

        // Setup enter transition on second fragment
        fragment.setSharedElementEnterTransition(changeTransform);
        fragment.setEnterTransition(explodeTransform);

        // Find the shared element (in Fragment A)
        //ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);

        // Add second fragment by replacing first
        fm.beginTransaction()
                .replace(R.id.main_container, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .addSharedElement(sharedView, "imageMain")
                //.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .commit();
    }
}

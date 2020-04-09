package com.example.imagesearch.ui.slider;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.imagesearch.R;
import com.example.imagesearch.ui.common.BaseViewModel;
import com.example.imagesearch.ui.common.MainActivity;

import java.util.List;
import java.util.Map;

public class ViewPagerFragment extends Fragment {

    private ViewPager mPager;
    private ImagePagerAdapter mAdapter;
    private BaseViewModel mViewModel;
    private int mItemPosition;

    private static final String KEY_POSITION = "ITEM_POSITION";

    public ViewPagerFragment(){}

    public static ViewPagerFragment newInstance(int itemPosition){
        ViewPagerFragment f = new ViewPagerFragment();
        Bundle b = new Bundle();
        b.putInt(KEY_POSITION, itemPosition);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        if (getArguments() != null) {
            mItemPosition = getArguments().getInt(KEY_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_full_photo, container, false);
        mPager = view.findViewById(R.id.view_pager);

        mAdapter = new ImagePagerAdapter(this, mViewModel.getAllPhotoList());
        mPager.setAdapter(mAdapter);
        new Handler().post(() -> {
            mPager.setCurrentItem(MainActivity.currentPosition);
        });

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MainActivity.currentPosition = position;
            }
        });

        prepareSharedElementTransition();

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }

        return view;
    }

    /**
     * Prepares the shared element transition from and back to the grid fragment.
     */
    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the image view at the primary fragment (the ImageFragment that is currently
                        // visible). To locate the fragment, call instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the position and will
                        // not create a new one.
                        Fragment currentFragment = (Fragment) mPager.getAdapter()
                                .instantiateItem(mPager, MainActivity.currentPosition);
                        View view = currentFragment.getView();
                        if (view == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), view.findViewById(R.id.ivPhoto));
                    }
                });
    }

    void log(String msg){
        Log.d("durga", msg);
    }
}

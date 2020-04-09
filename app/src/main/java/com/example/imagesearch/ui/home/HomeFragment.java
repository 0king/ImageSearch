package com.example.imagesearch.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import com.example.imagesearch.R;
import com.example.imagesearch.data.model.Photo;
import com.example.imagesearch.data.model.Resource;
import com.example.imagesearch.ui.common.BaseViewModel;
import com.example.imagesearch.ui.common.MainActivity;
import com.example.imagesearch.ui.util.ItemDecorationAlbumColumns;
import com.example.imagesearch.ui.util.SquareGridLayoutManager;

import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    //views
    private RecyclerView mPhotosListView;
    private PhotosListAdapter mListAdapter;
    private GridLayoutManager mLayoutManager;
    private TextView mStatusView;
    private SearchView mSearchView;

    private BaseViewModel mViewModel;

    private static final int SCROLL_DIRECTION_DOWN = 1;

    private Observer<Resource> mListObserver = (response) -> {
        switch (response.getStatus()){
            case ERROR: {
                log("home-observer-err=" + response.getError().toString());
                showToast(response.getError().toString());
            }
            break;
            case SUCCESS: {
                List<Photo> newList = (List<Photo>) response.getData();
                //log(newList.toString());
                if (newList.isEmpty()) //UI logic
                    showToast("Nothing found");
                mListAdapter.notifyItemRangeInserted(newList);
                log("frag adapter size  = " + mListAdapter.getItemCount());
            }
            break;
            case LOADING: {
                mStatusView.setText("Loading...");
                mStatusView.setVisibility(View.VISIBLE);
                break;
            }
            case COMPLETE:{
                mStatusView.setText("Completed");
                mStatusView.setVisibility(View.GONE);
                break;
            }
            case CREATED:{
                mStatusView.setText("Empty. Search for something.");
                mStatusView.setVisibility(View.VISIBLE);
                break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mViewModel = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initUi(view);
        observeData();
        addScrollListener();
        prepareTransitions();
        postponeEnterTransition();
        return view;
    }

    private void initUi(View v){
        mStatusView = v.findViewById(R.id.tvStatus);
        mPhotosListView = v.findViewById(R.id.recyclerView);
        mLayoutManager = new SquareGridLayoutManager(getContext(),
                2,
                GridLayoutManager.VERTICAL,
                false);
        mPhotosListView.setLayoutManager(mLayoutManager); // determine item width at runtime
        mListAdapter = new PhotosListAdapter(this, mViewModel.getAllPhotoList());
        mPhotosListView.setAdapter(mListAdapter);
        //mPhotosListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mPhotosListView.addItemDecoration(new ItemDecorationAlbumColumns(dpToPx(3), 2));
        //mPhotosListView.addItemDecoration(new MediaSpaceDecoration(dpToPx(3)));
    }

    private void addScrollListener(){
        //todo what to do when RV size less than screen
        mPhotosListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy < 0 //scroll up
                if (dy>0){ //scroll down
                    if (!mPhotosListView.canScrollVertically(SCROLL_DIRECTION_DOWN) &&
                            !mViewModel.isLoading()){
                        log("on scroll called");
                        mViewModel.loadMore();
                    }
                }
            }
        });
    }

    private void observeData(){
        mViewModel.getPhotoListEmitter().observe(getViewLifecycleOwner(), mListObserver);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scrollToPosition();
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */
    private void scrollToPosition() {
        mPhotosListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                mPhotosListView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = mPhotosListView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(MainActivity.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    mPhotosListView.post(() -> layoutManager.scrollToPosition(MainActivity.currentPosition));
                }
            }
        });
    }

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other transitions
     * that affect the flow.
     */
    private void prepareTransitions() {
        setExitTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition));

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.
                        RecyclerView.ViewHolder selectedViewHolder = mPhotosListView
                                .findViewHolderForAdapterPosition(MainActivity.currentPosition);
                        if (selectedViewHolder == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.ivPhoto));
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
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
        super.onCreateOptionsMenu(menu, inflater);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id){
            case R.id.two:
                changeLayoutManagerColumns(2);
                return true;
            case R.id.three:
                changeLayoutManagerColumns(3);
                return true;
            case R.id.four:
                changeLayoutManagerColumns(4);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPopupMenu(Context context, View anchorView){
        PopupMenu popup = new PopupMenu(context, anchorView);
        popup.inflate(R.menu.options);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.two:
                        changeLayoutManagerColumns(2);
                        return true;
                    case R.id.three:
                        changeLayoutManagerColumns(3);
                        return true;
                    case R.id.four:
                        changeLayoutManagerColumns(4);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //displaying the popup
        popup.show();
    }

    private void changeLayoutManagerColumns(int noOfColumns){
        //todo save #col in VM
        mPhotosListView.addItemDecoration(new ItemDecorationAlbumColumns(dpToPx(3), noOfColumns));
        mLayoutManager.setSpanCount(noOfColumns);
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showToast(String msg){
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    void log(String msg){
        Log.d("durga", msg);
    }

    private int dpToPx(int dp){
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}

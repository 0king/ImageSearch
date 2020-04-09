package com.example.imagesearch.ui.home;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.imagesearch.R;
import com.example.imagesearch.data.model.Photo;
import com.example.imagesearch.ui.common.MainActivity;
import com.example.imagesearch.ui.slider.ViewPagerFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.imagesearch.ui.common.Router.FRAGMENT_FULL;

public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.PhotoViewHolder> {

    private List<Photo> mList;

    private final ViewHolderListener mViewHolderListener;
    private final RequestManager mRequestManager;

    public PhotosListAdapter(Fragment fragment, List<Photo> l){
        mList = l;
        this.mViewHolderListener = new ViewHolderListenerImpl(fragment);
        mRequestManager = Glide.with(fragment);
    }

    public void notifyItemRangeInserted(List<Photo> items){
        int endIndex = mList.size()-1;
        if (endIndex<0) endIndex = 0;
        notifyItemRangeInserted(endIndex+1, items.size());
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid, parent, false);
        return new PhotoViewHolder(view, mRequestManager, mViewHolderListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class PhotoViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private final ViewHolderListener viewHolderListener;
        private final RequestManager requestManager;

        public PhotoViewHolder(@NonNull View itemView, RequestManager requestManager,
                               ViewHolderListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPhoto);
            viewHolderListener = listener;
            this.requestManager = requestManager;
            itemView.setOnClickListener(v -> {
                // Let the listener start the ImagePagerFragment.
                viewHolderListener.onItemClicked(v, getAdapterPosition());
                //Router.INSTANCE.showFullPhotoScreen(getAdapterPosition(), itemView);
            });
        }

        void bind(Photo photo){
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            // Set the string value of the image resource as the unique transition name for the view.
            imageView.setTransitionName(mList.get(adapterPosition).getUrl(Photo.ImageSize.SMALL));
        }

        void setImage(final int adapterPosition) {
            requestManager
                    .load(mList.get(adapterPosition).getUrl(Photo.ImageSize.SMALL))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(imageView, adapterPosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(imageView, adapterPosition);
                            return false;
                        }
                    })
                    .into(imageView);
        }
    }

    /**
     * Default {@link ViewHolderListener} implementation.
     */
    private static class ViewHolderListenerImpl implements ViewHolderListener {

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }

        @Override
        public void onLoadCompleted(ImageView view, int position) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (MainActivity.currentPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        /**
         * Handles a view click by setting the current position to the given {@code position} and
         * starting a {@link  com.example.imagesearch.ui.slider.ViewPagerFragment} which displays the image at the position.
         *
         * @param view the clicked {@link ImageView} (the shared element view will be re-mapped at the
         * GridFragment's SharedElementCallback)
         * @param position the selected view position
         */
        @Override
        public void onItemClicked(View view, int position) {
            // Update the position.
            MainActivity.currentPosition = position;

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);

            Fragment f2 = ViewPagerFragment.newInstance(position);

            ImageView transitioningView = view.findViewById(R.id.ivPhoto);
            fragment.getFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .replace(R.id.main_container, f2, FRAGMENT_FULL)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     */
    private interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }

    void log(String msg){
        Log.d("durga", msg);
    }
}

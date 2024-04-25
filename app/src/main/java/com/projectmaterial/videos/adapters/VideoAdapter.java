package com.projectmaterial.videos.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import com.google.android.material.transition.MaterialFadeThrough;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.common.VideoThumbnailManager;
import com.projectmaterial.videos.utils.BottomSheetDialogUtils;
import com.projectmaterial.videos.utils.SortingCriteria;
import com.projectmaterial.videos.utils.SortingOrder;
import com.projectmaterial.videos.utils.SortingUtils;
import com.projectmaterial.videos.utils.Video;
import com.projectmaterial.videos.utils.VideoUtils;
import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> implements BottomSheetDialogUtils.FavoriteStateToggleListener {
    private BottomSheetDialogUtils mBottomSheetDialogUtils;
    private Context mContext;
    private Handler mHandler;
    private List<Video> mVideoList;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private SortingCriteria.Video mSortingCriteria = SortingCriteria.Video.VIDEO_NAME;
    private SortingOrder mSortingOrder = SortingOrder.ASCENDING;
    private VideoThumbnailManager mVideoThumbnailManager;
    private VideoUtils mVideoUtils;
    private View mFragmentView;
    private ViewGroup mEmptyView;
    private static final String SHARED_PREFS_KEY_ADAPTER = "shared_prefs_video_adapter";
    private static final String SHARED_PREFS_KEY_SORTING_CRITERIA = "shared_prefs_sorting_criteria";
    private static final String SHARED_PREFS_KEY_SORTING_ORDER = "shared_prefs_sorting_order";
    
    public VideoAdapter(Context context, ViewGroup emptyView, View fragmentView) {
        mBottomSheetDialogUtils = new BottomSheetDialogUtils(context, fragmentView, this);
        mContext = context;
        mEmptyView = emptyView;
        mFragmentView = fragmentView;
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS_KEY_ADAPTER, Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mVideoThumbnailManager = new VideoThumbnailManager(context);
        mVideoList = new ArrayList<>();
        mVideoUtils = new VideoUtils(context);
        retrieveSortingPreferences();
        updateEmptyViewVisibility();
    }
    
    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = mVideoList.get(position);
        String videoData = video.getData();
        if (videoData != null && !videoData.isEmpty()) {
            mVideoThumbnailManager.getThumbnailBitmap(holder.mThumbnailImageView, video);
        } else {
            holder.mThumbnailImageView.setImageDrawable(null);
        }
        String formattedNameWithoutExtension = mVideoUtils.getFormattedNameWithoutExtension(video);
        holder.mTitleTextView.setText(mVideoUtils.extractTitle(formattedNameWithoutExtension));
        List<String> subtitles = mVideoUtils.extractSubtitle(formattedNameWithoutExtension);
        if (!subtitles.isEmpty()) {
            StringBuilder subtitleText = new StringBuilder();
            for (String subtitle : subtitles) {
                subtitleText.append(subtitle).append(" ");
            }
            String subtitleString = subtitleText.toString().trim();
            holder.mSubtitleTextView.setText(subtitleString);
            holder.mSubtitleTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mSubtitleTextView.setVisibility(View.GONE);
        }
        String formattedDuration = mVideoUtils.getFormattedDuration(video);
        holder.mDurationTextView.setText(formattedDuration);
        holder.itemView.setOnClickListener(
            view -> {
                mVideoUtils.openVideo(video);
            }
        );
        holder.itemView.setOnLongClickListener(
            view -> {
                mBottomSheetDialogUtils.showVideoOptionsBottomSheetDialog(video);
                return true;
            }
        );
    }
    
    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
    
    @Override
    public void toggleFavoriteState(Video video) {
        boolean newFavoriteStatus = !video.isFavorite();
        video.setFavorite(newFavoriteStatus);
        video.setFavoriteState(mContext, newFavoriteStatus);
    }
    
    public SortingCriteria.Video getSelectedSortingCriteria() {
        return mSortingCriteria;
    }
    
    public SortingOrder getSelectedSortingOrder() {
        return mSortingOrder;
    }
    
    public void setSortingCriteria(SortingCriteria.Video sortingCriteria) {
        mSortingCriteria = sortingCriteria;
        SortingUtils.sortVideoList(mVideoList, mSortingCriteria, mSortingOrder, mContext);
        saveSortingCriteria(sortingCriteria);
        notifyDataSetChanged();
    }
    
    public void setSortingOrder(SortingOrder sortingOrder) {
        mSortingOrder = sortingOrder;
        SortingUtils.sortVideoList(mVideoList, mSortingCriteria, mSortingOrder, mContext);
        saveSortingOrder(sortingOrder);
        notifyDataSetChanged();
    }
    
    public void setVideoList(List<Video> videoList) {
        mVideoList.clear();
        mVideoList.addAll(videoList);
        SortingUtils.sortVideoList(mVideoList, mSortingCriteria, mSortingOrder, mContext);
        updateEmptyViewVisibility();
        notifyDataSetChanged();
    }
    
    private void retrieveSortingPreferences() {
        String savedSortingCriteria = mSharedPreferences.getString(SHARED_PREFS_KEY_SORTING_CRITERIA, null);
        if (savedSortingCriteria != null) {
            mSortingCriteria = SortingCriteria.Video.valueOf(savedSortingCriteria);
        }
        String savedSortingOrder = mSharedPreferences.getString(SHARED_PREFS_KEY_SORTING_ORDER, null);
        if (savedSortingOrder != null) {
            mSortingOrder = SortingOrder.valueOf(savedSortingOrder);
        }
    }
    
    private void saveSortingCriteria(@NonNull SortingCriteria.Video sortingCriteria) {
        mSharedPreferencesEditor.putString(SHARED_PREFS_KEY_SORTING_CRITERIA, sortingCriteria.name());
        mSharedPreferencesEditor.apply();
    }
    
    private void saveSortingOrder(@NonNull SortingOrder sortingOrder) {
        mSharedPreferencesEditor.putString(SHARED_PREFS_KEY_SORTING_ORDER, sortingOrder.name());
        mSharedPreferencesEditor.apply();
    }
    
    private void updateEmptyViewVisibility() {
        if (mEmptyView != null) {
            boolean isEmpty = mVideoList.isEmpty();
            if (isEmpty && mEmptyView.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition((ViewGroup) mEmptyView.getParent(), new MaterialFadeThrough());
                mEmptyView.setVisibility(View.VISIBLE);
            } else if (!isEmpty && mEmptyView.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition((ViewGroup) mEmptyView.getParent(), new MaterialFadeThrough());
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }
    
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mThumbnailImageView;
        private final TextView mDurationTextView;
        private final TextView mSubtitleTextView;
        private final TextView mTitleTextView;
        
        public VideoViewHolder(View itemView) {
            super(itemView);
            mThumbnailImageView = itemView.findViewById(R.id.thumbnail);
            mDurationTextView = itemView.findViewById(R.id.duration);
            mSubtitleTextView = itemView.findViewById(R.id.subtitle);
            mTitleTextView = itemView.findViewById(R.id.title);
        }
    }
}
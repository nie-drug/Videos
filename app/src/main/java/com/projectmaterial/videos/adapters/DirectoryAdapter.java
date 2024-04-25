package com.projectmaterial.videos.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;
import com.google.android.material.transition.MaterialFadeThrough;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.activities.DirectoryVideosActivity;
import com.projectmaterial.videos.utils.DirectoryBottomSheetDialogUtils;
import com.projectmaterial.videos.utils.DirectoryUtils;
import com.projectmaterial.videos.utils.LayoutType;
import com.projectmaterial.videos.utils.SortingCriteria;
import com.projectmaterial.videos.utils.SortingOrder;
import com.projectmaterial.videos.utils.SortingUtils;
import com.projectmaterial.videos.utils.Video;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    private Context mContext;
    private DirectoryBottomSheetDialogUtils mDirectoryBottomSheetDialogUtils;
    private DirectoryUtils mDirectoryUtils;
    private LayoutType mLayoutType;
    private List<String> mDirectoryList;
    private Map<String, Integer> mVideoCountMap;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private SortingCriteria.Directory mSortingCriteria = SortingCriteria.Directory.DIRECTORY_NAME;
    private SortingOrder mSortingOrder = SortingOrder.ASCENDING;
    private View mFragmentView;
    private ViewGroup mEmptyView;
    private static final int VIEW_TYPE_GRID = 2;
    private static final int VIEW_TYPE_LIST = 1;
    private static final String SHARED_PREFS_KEY_ADAPTER = "shared_prefs_directory_adapter";
    private static final String SHARED_PREFS_KEY_SORTING_CRITERIA = "shared_prefs_sorting_criteria";
    private static final String SHARED_PREFS_KEY_SORTING_ORDER = "shared_prefs_sorting_order";
    
    public DirectoryAdapter(Context context, ViewGroup emptyView, View fragmentView) {
        mContext = context;
        mDirectoryBottomSheetDialogUtils = new DirectoryBottomSheetDialogUtils(context);
        mDirectoryList = new ArrayList<>();
        mDirectoryUtils = new DirectoryUtils(context);
        mEmptyView = emptyView;
        mFragmentView = fragmentView;
        mLayoutType = LayoutType.LIST;
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFS_KEY_ADAPTER, Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mVideoCountMap = new HashMap<>();
        retrieveSortingPreferences();
        updateEmptyViewVisibility();
    }
    
    @NonNull
    @Override
    public DirectoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_LIST) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_list, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder_grid, parent, false);
        }
        return new DirectoryViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DirectoryViewHolder holder, int position) {
        String directory = mDirectoryList.get(position);
        holder.mDirectoryNameTextView.setText(mDirectoryUtils.getFormattedName(directory));
        Integer videoCount = mVideoCountMap.get(directory);
        holder.mVideoCountTextView.setText(mContext.getResources().getQuantityString(R.plurals.video_count, videoCount, videoCount));
        holder.itemView.setOnClickListener(
            view -> {
                Intent intent = new Intent(mContext, DirectoryVideosActivity.class);
                intent.putExtra("directory", directory);
                mContext.startActivity(intent);
            }
        );
        holder.itemView.setOnLongClickListener(
            view -> {
                mDirectoryBottomSheetDialogUtils.showDirectoryInfoBottomSheetDialog(directory);
                return true;
            }
        );
    }
    
    @Override
    public int getItemCount() {
        return mDirectoryList.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        return mLayoutType == LayoutType.LIST ? VIEW_TYPE_LIST : VIEW_TYPE_GRID;
    }
    
    public SortingCriteria.Directory getSelectedSortingCriteria() {
        return mSortingCriteria;
    }
    
    public SortingOrder getSelectedSortingOrder() {
        return mSortingOrder;
    }
    
    public void setDirectoryList(List<String> directoryList, List<Video> videoList) {
        mDirectoryList.clear();
        mDirectoryList.addAll(directoryList);
        countVideos(videoList);
        SortingUtils.sortDirectoryList(mDirectoryList, mSortingCriteria, mSortingOrder, mContext, mVideoCountMap);
        updateEmptyViewVisibility();
        notifyDataSetChanged();
    }
    
    public void setLayoutType(LayoutType layoutType) {
        mLayoutType = layoutType;
        notifyDataSetChanged();
    }
    
    public void setSortingCriteria(SortingCriteria.Directory sortingCriteria) {
        mSortingCriteria = sortingCriteria;
        SortingUtils.sortDirectoryList(mDirectoryList, mSortingCriteria, mSortingOrder, mContext, mVideoCountMap);
        saveSortingCriteria(sortingCriteria);
        notifyDataSetChanged();
    }
    
    public void setSortingOrder(SortingOrder sortingOrder) {
        mSortingOrder = sortingOrder;
        SortingUtils.sortDirectoryList(mDirectoryList, mSortingCriteria, mSortingOrder, mContext, mVideoCountMap);
        saveSortingOrder(sortingOrder);
        notifyDataSetChanged();
    }
    
    private void countVideos(@Nullable List<Video> videoList) {
        mVideoCountMap.clear();
        if (videoList != null) {
            for (Video video : videoList) {
                String videoData = video.getData();
                String directory = mDirectoryUtils.retrieveDirectoryPathFromVideoData(videoData);
                if (directory != null) {
                    mVideoCountMap.merge(directory, 1, Integer::sum);
                }
            }
        }
    }
    
    private void retrieveSortingPreferences() {
        String savedSortingCriteria = mSharedPreferences.getString(SHARED_PREFS_KEY_SORTING_CRITERIA, null);
        if (savedSortingCriteria != null) {
            mSortingCriteria = SortingCriteria.Directory.valueOf(savedSortingCriteria);
        }
        String savedSortingOrder = mSharedPreferences.getString(SHARED_PREFS_KEY_SORTING_ORDER, null);
        if (savedSortingOrder != null) {
            mSortingOrder = SortingOrder.valueOf(savedSortingOrder);
        }
    }
    
    private void saveSortingCriteria(@NonNull SortingCriteria.Directory sortingCriteria) {
        mSharedPreferencesEditor.putString(SHARED_PREFS_KEY_SORTING_CRITERIA, sortingCriteria.name());
        mSharedPreferencesEditor.apply();
    }
    
    private void saveSortingOrder(@NonNull SortingOrder sortingOrder) {
        mSharedPreferencesEditor.putString(SHARED_PREFS_KEY_SORTING_ORDER, sortingOrder.name());
        mSharedPreferencesEditor.apply();
    }
    
    private void updateEmptyViewVisibility() {
        if (mEmptyView != null) {
            boolean isEmpty = mDirectoryList.isEmpty();
            if (isEmpty && mEmptyView.getVisibility() != View.VISIBLE) {
                TransitionManager.beginDelayedTransition((ViewGroup) mEmptyView.getParent(), new MaterialFadeThrough());
                mEmptyView.setVisibility(View.VISIBLE);
            } else if (!isEmpty && mEmptyView.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition((ViewGroup) mEmptyView.getParent(), new MaterialFadeThrough());
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }
    
    public static class DirectoryViewHolder extends RecyclerView.ViewHolder {
        private TextView mDirectoryNameTextView;
        private TextView mVideoCountTextView;
        
        public DirectoryViewHolder(View itemView) {
            super(itemView);
            mDirectoryNameTextView = itemView.findViewById(R.id.title);
            mVideoCountTextView = itemView.findViewById(R.id.video_count);
        }
    }
}
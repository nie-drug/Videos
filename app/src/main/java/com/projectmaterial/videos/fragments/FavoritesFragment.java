package com.projectmaterial.videos.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.adapters.FavoriteVideoAdapter;
import com.projectmaterial.videos.recyclerview.LinearLayoutItemDecoration;
import com.projectmaterial.videos.utils.SortingCriteria;
import com.projectmaterial.videos.utils.SortingOrder;
import com.projectmaterial.videos.utils.Video;
import com.projectmaterial.videos.viewmodel.VideoViewModel;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends BaseFragment {
    private BottomSheetDialog mBottomSheetDialog;
    private FavoriteVideoAdapter mFavoriteVideoAdapter;
    private MaterialButton mButtonSortingCriteriaName;
    private MaterialButton mButtonSortingCriteriaDuration;
    private MaterialButton mButtonSortingCriteriaDate;
    private MaterialButton mButtonSortingCriteriaSize;
    private MaterialButton mButtonSortingOrderAsc;
    private MaterialButton mButtonSortingOrderDesc;
    private MaterialToolbar mMaterialToolbar;
    private RecyclerView mRecyclerView;
    private VideoViewModel mVideoViewModel;
    private View mContentView;
    private ViewGroup mEmptyView;
    
    @SuppressWarnings("deprecation")
    @MainThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @MainThread
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);
        setUpTransition(rootView);
        return rootView;
    }
    
    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMaterialToolbar = view.findViewById(R.id.toolbar);
        mMaterialToolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(), R.drawable.quantum_ic_more_vert_vd_theme_24));
        mMaterialToolbar.setTitle(R.string.navigation_title_starred);
        setSupportActionBar(mMaterialToolbar);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mEmptyView = view.findViewById(R.id.empty_view);
        mFavoriteVideoAdapter = new FavoriteVideoAdapter(getContext(), mEmptyView, view);
        mRecyclerView.setAdapter(mFavoriteVideoAdapter);
        mVideoViewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);
        mVideoViewModel.getVideoLiveData().observe(getViewLifecycleOwner(), this::onFavoriteVideoListChanged);
    }
    
    @SuppressWarnings("deprecation")
    @MainThread
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_starred, menu);
    }
    
    @SuppressWarnings("deprecation")
    @MainThread
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_sort) {
            showSortingOptionBottomSheetDialog();
            return true;
        }
        return false;
    }
    
    @MainThread
    private void onFavoriteVideoListChanged(List<Video> videoList) {
        List<Video> favoriteVideoList = new ArrayList<>();
        for (Video video : videoList) {
            boolean isFavorite = video.getFavoriteState(getContext());
            video.setFavorite(isFavorite);
            if (video.isFavorite()) {
                favoriteVideoList.add(video);
            }
        }
        mFavoriteVideoAdapter.setFavoriteVideoList(favoriteVideoList);
    }
    
    private void showSortingOptionBottomSheetDialog() {
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_video_sorting, null);
        
        mButtonSortingCriteriaName = mContentView.findViewById(R.id.sorting_button_video_name);
        mButtonSortingCriteriaName.setOnClickListener(
            (view) -> {
                mFavoriteVideoAdapter.setSortingCriteria(SortingCriteria.Video.VIDEO_NAME);
                mBottomSheetDialog.dismiss();
            }
        );
        
        mButtonSortingCriteriaDuration = mContentView.findViewById(R.id.sorting_button_video_duration);
        mButtonSortingCriteriaDuration.setOnClickListener(
            (view) -> {
                mFavoriteVideoAdapter.setSortingCriteria(SortingCriteria.Video.VIDEO_DURATION);
                mBottomSheetDialog.dismiss();
            }
        );
        
        mButtonSortingCriteriaDate = mContentView.findViewById(R.id.sorting_button_video_date);
        mButtonSortingCriteriaDate.setOnClickListener(
            (view) -> {
                mFavoriteVideoAdapter.setSortingCriteria(SortingCriteria.Video.VIDEO_DATE);
                mBottomSheetDialog.dismiss();
            }
        );
        
        mButtonSortingCriteriaSize = mContentView.findViewById(R.id.sorting_button_video_size);
        mButtonSortingCriteriaSize.setOnClickListener(
            (view) -> {
                mFavoriteVideoAdapter.setSortingCriteria(SortingCriteria.Video.VIDEO_SIZE);
                mBottomSheetDialog.dismiss();
            }
        );
        
        mButtonSortingOrderAsc = mContentView.findViewById(R.id.sorting_button_asc);
        mButtonSortingOrderAsc.setOnClickListener(
            (view) -> {
                mButtonSortingOrderAsc.setChecked(true);
                mButtonSortingOrderDesc.setChecked(false);
                mFavoriteVideoAdapter.setSortingOrder(SortingOrder.ASCENDING);
            }
        );
        
        mButtonSortingOrderDesc = mContentView.findViewById(R.id.sorting_button_desc);
        mButtonSortingOrderDesc.setOnClickListener(
            (view) -> {
                mButtonSortingOrderAsc.setChecked(false);
                mButtonSortingOrderDesc.setChecked(true);
                mFavoriteVideoAdapter.setSortingOrder(SortingOrder.DESCENDING);
            }
        );
        
        if (mFavoriteVideoAdapter.getSelectedSortingCriteria() == SortingCriteria.Video.VIDEO_NAME) {
            mButtonSortingCriteriaName.setChecked(true);
            mButtonSortingCriteriaDuration.setChecked(false);
            mButtonSortingCriteriaDate.setChecked(false);
            mButtonSortingCriteriaSize.setChecked(false);
            mButtonSortingOrderAsc.setText(R.string.bottom_sheet_sort_by_order_a_z);
            mButtonSortingOrderDesc.setText(R.string.bottom_sheet_sort_by_order_z_a);
        } else if (mFavoriteVideoAdapter.getSelectedSortingCriteria() == SortingCriteria.Video.VIDEO_DURATION) {
            mButtonSortingCriteriaName.setChecked(false);
            mButtonSortingCriteriaDuration.setChecked(true);
            mButtonSortingCriteriaDate.setChecked(false);
            mButtonSortingCriteriaSize.setChecked(false);
            mButtonSortingOrderAsc.setText(R.string.bottom_sheet_sort_by_order_shortest);
            mButtonSortingOrderDesc.setText(R.string.bottom_sheet_sort_by_order_longest);
        } else if (mFavoriteVideoAdapter.getSelectedSortingCriteria() == SortingCriteria.Video.VIDEO_DATE) {
            mButtonSortingCriteriaName.setChecked(false);
            mButtonSortingCriteriaDuration.setChecked(false);
            mButtonSortingCriteriaDate.setChecked(true);
            mButtonSortingCriteriaSize.setChecked(false);
            mButtonSortingOrderAsc.setText(R.string.bottom_sheet_sort_by_order_oldest);
            mButtonSortingOrderDesc.setText(R.string.bottom_sheet_sort_by_order_newest);
        } else {
            mButtonSortingCriteriaName.setChecked(false);
            mButtonSortingCriteriaDuration.setChecked(false);
            mButtonSortingCriteriaDate.setChecked(false);
            mButtonSortingCriteriaSize.setChecked(true);
            mButtonSortingOrderAsc.setText(R.string.bottom_sheet_sort_by_order_smallest);
            mButtonSortingOrderDesc.setText(R.string.bottom_sheet_sort_by_order_largest);
        }
        
        if (mFavoriteVideoAdapter.getSelectedSortingOrder() == SortingOrder.ASCENDING) {
            mButtonSortingOrderAsc.setChecked(true);
            mButtonSortingOrderDesc.setChecked(false);
        } else {
            mButtonSortingOrderAsc.setChecked(false);
            mButtonSortingOrderDesc.setChecked(true);
        }
        
        mBottomSheetDialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mBottomSheetDialog.setContentView(mContentView);
        mBottomSheetDialog.show();
    }
}
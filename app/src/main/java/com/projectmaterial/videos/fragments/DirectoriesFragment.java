package com.projectmaterial.videos.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.transition.MaterialFadeThrough;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.adapters.DirectoryAdapter;
import com.projectmaterial.videos.recyclerview.GridLayoutItemDecoration;
import com.projectmaterial.videos.recyclerview.LinearLayoutItemDecoration;
import com.projectmaterial.videos.utils.LayoutType;
import com.projectmaterial.videos.utils.SortingCriteria;
import com.projectmaterial.videos.utils.SortingOrder;
import com.projectmaterial.videos.utils.Video;
import com.projectmaterial.videos.viewmodel.VideoViewModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoriesFragment extends BaseFragment {
    private BottomSheetDialog mBottomSheetDialog;
    private DirectoryAdapter mDirectoryAdapter;
    private MaterialButton mButtonSortingCriteriaName;
    private MaterialButton mButtonSortingCriteriaCount;
    private MaterialButton mButtonSortingOrderAsc;
    private MaterialButton mButtonSortingOrderDesc;
    private MaterialToolbar mMaterialToolbar;
    private MenuItem mMenuItemListView;
    private MenuItem mMenuItemGridView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private VideoViewModel mVideoViewModel;
    private View mContentView;
    private ViewGroup mEmptyView;
    private boolean isGridView = false;
    private static String GRID_VIEW_NAME = "shared_grid_view_name";
    private static String GRID_VIEW_STATE = "shared_grid_view_state";
    
    @SuppressWarnings("deprecation")
    @MainThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @MainThread
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_directories, container, false);
        setUpTransition(rootView);
        return rootView;
    }
    
    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mMaterialToolbar = view.findViewById(R.id.toolbar);
        mMaterialToolbar.setOverflowIcon(ContextCompat.getDrawable(getContext(), R.drawable.quantum_ic_more_vert_vd_theme_24));
        mMaterialToolbar.setTitle(R.string.navigation_title_folders);
        setSupportActionBar(mMaterialToolbar);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mEmptyView = view.findViewById(R.id.empty_view);
        mDirectoryAdapter = new DirectoryAdapter(getContext(), mEmptyView, view);
        mRecyclerView.setAdapter(mDirectoryAdapter);
        isGridView = getGridViewState();
        setGridLayoutManager(isGridView);
        mVideoViewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);
        mVideoViewModel.getVideoLiveData().observe(getViewLifecycleOwner(), this::onDirectoryListChanged);
    }
    
    @SuppressWarnings("deprecation")
    @MainThread
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_folders, menu);
    }
    
    @SuppressWarnings("deprecation")
    @MainThread
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        mMenuItemGridView = menu.findItem(R.id.menu_grid_view);
        mMenuItemGridView.setVisible(!isGridView);
        mMenuItemListView = menu.findItem(R.id.menu_list_view);
        mMenuItemListView.setVisible(isGridView);
    }
    
    @SuppressWarnings("deprecation")
    @MainThread
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_list_view) {
            isGridView = false;
            setGridLayoutManager(false);
            setGridViewState(false);
            setUpLayoutTypeTransition();
            return true;
        } else if (item.getItemId() == R.id.menu_grid_view) {
            isGridView = true;
            setGridLayoutManager(true);
            setGridViewState(true);
            setUpLayoutTypeTransition();
            return true;
        } else if (item.getItemId() == R.id.menu_sort) {
            showSortingOptionBottomSheetDialog();
            return true;
        }
        return false;
    }
    
    @MainThread
    private boolean getGridViewState() {
        mSharedPreferences = getContext().getSharedPreferences(GRID_VIEW_NAME, Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(GRID_VIEW_STATE, false);
    }
    
    @MainThread
    private void onDirectoryListChanged(List<Video> videoList) {
        List<String> directoryList = new ArrayList<>();
        for (Video video : videoList) {
            String videoData = video.getData();
            String directory = videoData.substring(0, videoData.lastIndexOf(File.separator));
            if (!directoryList.contains(directory)) {
                directoryList.add(directory);
            }
        }
        mDirectoryAdapter.setDirectoryList(directoryList, videoList);
    }
    
    @MainThread
    private void setGridLayoutManager(boolean isGridView) {
        if (isGridView) {
            int spacing = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing);
            int spacingEdgeBottom = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing_edge_bottom);
            int spacingEdgeSide = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing_edge_side);
            int spacingEdgeTop = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing_edge_top);
            
            if (mRecyclerView.getItemDecorationCount() > 0) {
                mRecyclerView.removeItemDecorationAt(0);
            }
            
            mRecyclerView.addItemDecoration(new GridLayoutItemDecoration(2, spacing, spacingEdgeBottom, spacingEdgeSide, spacingEdgeSide, spacingEdgeTop, false));
            mRecyclerViewLayoutManager = new GridLayoutManager(getContext(), 2);
        } else {
            if (mRecyclerView.getItemDecorationCount() > 0) {
                mRecyclerView.removeItemDecorationAt(0);
            }
            
            mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext()));
            mRecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        }
        
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mDirectoryAdapter.setLayoutType(isGridView ? LayoutType.GRID : LayoutType.LIST);
    }
    
    @MainThread
    private void setGridViewState(boolean isGridView) {
        mSharedPreferences = getContext().getSharedPreferences(GRID_VIEW_NAME, Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mSharedPreferencesEditor.putBoolean(GRID_VIEW_STATE, isGridView);
        mSharedPreferencesEditor.apply();
    }
    
    private void setUpLayoutTypeTransition() {
        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        materialFadeThrough.setDuration(getResources().getInteger(com.google.android.material.R.integer.m3_sys_motion_duration_short4));
        materialFadeThrough.addListener(
            new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    
                }
                @Override
                public void onTransitionEnd(Transition transition) {
                    requireActivity().invalidateOptionsMenu();
                }
                @Override
                public void onTransitionCancel(Transition transition) {
                    
                }
                @Override
                public void onTransitionPause(Transition transition) {
                    
                }
                @Override
                public void onTransitionResume(Transition transition) {
                    
                }
            }
        );
        TransitionManager.beginDelayedTransition((ViewGroup) mRecyclerView.getParent(), materialFadeThrough);
    }
    
    private void showSortingOptionBottomSheetDialog() {
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_directory_sorting, null);
        
        mButtonSortingCriteriaName = mContentView.findViewById(R.id.sorting_button_folder_name);
        mButtonSortingCriteriaName.setOnClickListener(
            (view) -> {
                mDirectoryAdapter.setSortingCriteria(SortingCriteria.Directory.DIRECTORY_NAME);
                mBottomSheetDialog.dismiss();
            }
        );
        
        mButtonSortingCriteriaCount = mContentView.findViewById(R.id.sorting_button_video_count);
        mButtonSortingCriteriaCount.setOnClickListener(
            (view) -> {
                mDirectoryAdapter.setSortingCriteria(SortingCriteria.Directory.VIDEO_COUNT);
                mBottomSheetDialog.dismiss();
            }
        );
        
        mButtonSortingOrderAsc = mContentView.findViewById(R.id.sorting_button_asc);
        mButtonSortingOrderAsc.setOnClickListener(
            (view) -> {
                mButtonSortingOrderAsc.setChecked(true);
                mButtonSortingOrderDesc.setChecked(false);
                mDirectoryAdapter.setSortingOrder(SortingOrder.ASCENDING);
            }
        );
        
        mButtonSortingOrderDesc = mContentView.findViewById(R.id.sorting_button_desc);
        mButtonSortingOrderDesc.setOnClickListener(
            (view) -> {
                mButtonSortingOrderAsc.setChecked(false);
                mButtonSortingOrderDesc.setChecked(true);
                mDirectoryAdapter.setSortingOrder(SortingOrder.DESCENDING);
            }
        );
        
        if (mDirectoryAdapter.getSelectedSortingCriteria() == SortingCriteria.Directory.DIRECTORY_NAME) {
            mButtonSortingCriteriaName.setChecked(true);
            mButtonSortingCriteriaCount.setChecked(false);
            mButtonSortingOrderAsc.setText(R.string.bottom_sheet_sort_by_order_a_z);
            mButtonSortingOrderDesc.setText(R.string.bottom_sheet_sort_by_order_z_a);
        } else {
            mButtonSortingCriteriaName.setChecked(false);
            mButtonSortingCriteriaCount.setChecked(true);
            mButtonSortingOrderAsc.setText(R.string.bottom_sheet_sort_by_order_smallest);
            mButtonSortingOrderDesc.setText(R.string.bottom_sheet_sort_by_order_largest);
        }
        
        if (mDirectoryAdapter.getSelectedSortingOrder() == SortingOrder.ASCENDING) {
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
    
    
//    @MainThread
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mMaterialToolbar = view.findViewById(R.id.toolbar);
//        mMaterialToolbar.setTitle(R.string.navigation_title_folders);
//        setSupportActionBar(mMaterialToolbar);
//        mEmptyView = view.findViewById(R.id.empty_view);
//        mDirectoryAdapter = new DirectoryAdapter(requireContext(), mEmptyView, view);
//        mRecyclerView = view.findViewById(R.id.recycler_view);
//        mRecyclerView.setAdapter(mDirectoryAdapter);
//        boolean viewState = getViewState();
//        isGridView = getViewState();
//        setGridLayoutManager(viewState);
//        mVideoViewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);
//        mVideoViewModel.getVideoLiveData().observe(getViewLifecycleOwner(), this::onDirectoryListChanged);
//    }
    
//    @MainThread
//    @SuppressWarnings("deprecation")
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_folders, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//    
//    @MainThread
//    @SuppressWarnings("deprecation")
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.menu_list_view) {
//            if (!isGridView) {
//                return true;
//            }
//            isGridView = false;
////            setGridLayoutManager(false);
//            setViewState(false);
//            setUpLayoutTypeTransition();
//            return true;
//        } else if (item.getItemId() == R.id.menu_grid_view) {
//            if (isGridView) {
//                return true;
//            }
//            isGridView = true;
////            setGridLayoutManager(true);
//            setViewState(true);
//            setUpLayoutTypeTransition();
//            return true;
//        } else if (item.getItemId() == R.id.menu_sort) {
//            showSortingOptionBottomSheetDialog();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    
//    @Override
//    @SuppressWarnings("deprecation")
//    public void onPrepareOptionsMenu(@NonNull Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        {
//            mMenuItem = menu.findItem(R.id.menu_grid_view);
//            mMenuItem.setVisible(!isGridView);
//        }
//        {
//            mMenuItem = menu.findItem(R.id.menu_list_view);
//            mMenuItem.setVisible(isGridView);
//        }
//    }
    
//    private List<String> getDirectoryList(List<Video> videoList) {
//        mDirectoryList = new ArrayList<>();
//        for (Video video : videoList) {
//            String videoData = video.getData();
//            String directory = videoData.substring(0, videoData.lastIndexOf(File.separator));
//            if (!mDirectoryList.contains(directory)) {
//                mDirectoryList.add(directory);
//            }
//        }
//        return mDirectoryList;
//    }
//    
//    
//    
//    private void onDirectoryListChanged(List<Video> videoList) {
//        List<String> directoryList = getDirectoryList(videoList);
//        mDirectoryAdapter.setDirectoryList(directoryList, videoList);
//    }
    
//    private void setGridLayoutManager(boolean isGridView) {
//        if (isGridView) {
//            int spanCount = 2;
//            int spacingItems = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing);
//            int spacingEdgeBottom = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing_edge_bottom);
//            int spacingEdgeSide = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing_edge_side);
//            int spacingEdgeTop = getResources().getDimensionPixelSize(R.dimen.item_grid_spacing_edge_top);
//            
//            mRecyclerViewLayoutManager = new GridLayoutManager(requireContext(), spanCount);
//            if (mRecyclerView.getItemDecorationCount() > 0) {
//                mRecyclerView.removeItemDecorationAt(0);
//            }
//            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacingItems, spacingEdgeBottom, spacingEdgeSide, spacingEdgeSide, spacingEdgeTop, false));
//        } else {
//            mRecyclerViewLayoutManager = new LinearLayoutManager(requireContext());
//            if (mRecyclerView.getItemDecorationCount() > 0) {
//                mRecyclerView.removeItemDecorationAt(0);
//            }
//            mRecyclerView.addItemDecoration(new LinearSpacingItemDecoration(requireContext()));
//        }
//        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
//        mDirectoryAdapter.setLayoutType(isGridView ? LayoutType.GRID : LayoutType.LIST);
//    }
    
    
    
    
    
    /**
//     * Displays a bottom sheet dialog to allow the user to select sorting options for directory items.
//     */
//    private void showSortingOptionBottomSheetDialog() {
//        // Create a BottomSheetDialog instance
//        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
//        // Inflate the layout for the dialog
//        View contentView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_directory_sorting, null);
//        
//        // Initialize buttons for sorting options
//        MaterialButton buttonName = contentView.findViewById(R.id.sorting_button_folder_name);
//        MaterialButton buttonCount = contentView.findViewById(R.id.sorting_button_video_count);
//        MaterialButton buttonAsc = contentView.findViewById(R.id.sorting_button_asc);
//        MaterialButton buttonDesc = contentView.findViewById(R.id.sorting_button_desc);
//        
//        // Set click listeners for sorting buttons
//        buttonName.setOnClickListener(view -> {
//            mDirectoryAdapter.setSortingCriteria(SortingCriteria.Directory.DIRECTORY_NAME);
//            dialog.dismiss();
//        });
//        
//        buttonCount.setOnClickListener(view -> {
//            mDirectoryAdapter.setSortingCriteria(SortingCriteria.Directory.VIDEO_COUNT);
//            dialog.dismiss();
//        });
//        
//        buttonAsc.setOnClickListener(view -> {
//            buttonAsc.setChecked(true);
//            buttonDesc.setChecked(false);
//            mDirectoryAdapter.setSortingOrder(SortingOrder.ASCENDING);
//        });
//        
//        buttonDesc.setOnClickListener(view -> {
//            buttonAsc.setChecked(false);
//            buttonDesc.setChecked(true);
//            mDirectoryAdapter.setSortingOrder(SortingOrder.DESCENDING);
//        });
//        
//        // Set initial state of sorting buttons based on current sorting criteria and order
//        if (mDirectoryAdapter.getSelectedSortingCriteria() == SortingCriteria.Directory.DIRECTORY_NAME) {
//            buttonName.setChecked(true);
//            buttonCount.setChecked(false);
//            buttonAsc.setText(R.string.bottom_sheet_sort_by_order_a_z);
//            buttonDesc.setText(R.string.bottom_sheet_sort_by_order_z_a);
//        } else {
//            buttonName.setChecked(false);
//            buttonCount.setChecked(true);
//            buttonAsc.setText(R.string.bottom_sheet_sort_by_order_smallest);
//            buttonDesc.setText(R.string.bottom_sheet_sort_by_order_largest);
//        }
//        
//        if (mDirectoryAdapter.getSelectedSortingOrder() == SortingOrder.ASCENDING) {
//            buttonAsc.setChecked(true);
//            buttonDesc.setChecked(false);
//        } else {
//            buttonAsc.setChecked(false);
//            buttonDesc.setChecked(true);
//        }
//        
//        // Customize dialog window appearance
//        dialog.getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        // Set the content view for the dialog
//        dialog.setContentView(contentView);
//        // Display the dialog
//        dialog.show();
//    }
}
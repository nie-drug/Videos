package com.projectmaterial.videos.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.adapters.VideoAdapter;
import com.projectmaterial.videos.recyclerview.LinearLayoutItemDecoration;
import com.projectmaterial.videos.utils.Video;
import com.projectmaterial.videos.viewmodel.DirectoryViewModel;
import java.util.List;

public class DirectoryVideosFragment extends Fragment {
    private DirectoryViewModel mDirectoryViewModel;
    private RecyclerView mRecyclerView;
    private String mDirectory;
    private VideoAdapter mVideoAdapter;
    private static Bundle mArguments;
    private static DirectoryVideosFragment mDirectoryVideosFragment;
    private static final String DIRECTORY = "directory";
    
    public static DirectoryVideosFragment newInstance(String directory) {
        mArguments = new Bundle();
        mArguments.putString(DIRECTORY, directory);
        mDirectoryVideosFragment = new DirectoryVideosFragment();
        mDirectoryVideosFragment.setArguments(mArguments);
        return mDirectoryVideosFragment;
    }
    
    @MainThread
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDirectory = getArguments().getString(DIRECTORY);
        }
    }
    
    @MainThread
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_directory, container, false);
        return rootView;
    }
    
    @MainThread
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new LinearLayoutItemDecoration(getContext()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mVideoAdapter = new VideoAdapter(getContext(), null, view);
        mRecyclerView.setAdapter(mVideoAdapter);
        ViewCompat.setOnApplyWindowInsetsListener(mRecyclerView,
            (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, 0, 0, insets.bottom);
                return WindowInsetsCompat.CONSUMED;
            }
        );
        mDirectoryViewModel = new ViewModelProvider(requireActivity()).get(DirectoryViewModel.class);
        mDirectoryViewModel.retrieveDirectoryVideosLiveData(mDirectory).observe(getViewLifecycleOwner(), this::onDirectoryVideoListChanged);
    }
    
    private void onDirectoryVideoListChanged(List<Video> directoryVideoList) {
        mVideoAdapter.setVideoList(directoryVideoList);
        if (directoryVideoList.isEmpty()) {
            requireActivity().finish();
        }
    }
}
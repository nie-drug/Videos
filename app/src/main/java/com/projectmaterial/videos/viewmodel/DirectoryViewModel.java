package com.projectmaterial.videos.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.projectmaterial.videos.VideoApplication;
import com.projectmaterial.videos.utils.Video;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryViewModel extends AndroidViewModel {
    private VideoApplication mVideoApplication;
    
    public DirectoryViewModel(@NonNull Application application) {
        super(application);
        mVideoApplication = (VideoApplication) application;
    }
    
    public List<Video> retrieveDirectoryVideos(@NonNull String directory, @NonNull List<Video> videoList) {
        if (directory == null || videoList == null) {
            return new ArrayList<>();
        }
        
        List<Video> directoryVideos = new ArrayList<>();
        for (Video video : videoList) {
            String videoData = video.getData();
            if (videoData != null) {
                File videoFile = new File(videoData);
                File videoDirectory = videoFile.getParentFile();
                
                if (videoDirectory != null && videoDirectory.getAbsolutePath().equals(directory)) {
                    directoryVideos.add(video);
                }
            }
        }
        
        return directoryVideos;
    }
    
    public LiveData<List<Video>> retrieveDirectoryVideosLiveData(@NonNull String directory) {
        MutableLiveData<List<Video>> videosLiveData = new MutableLiveData<>();
        
        if (directory != null) {
            mVideoApplication.getVideoLiveData().observeForever(
                videoList -> {
                    if (videoList != null) {
                        List<Video> directoryVideos = retrieveDirectoryVideos(directory, videoList);
                        videosLiveData.postValue(directoryVideos);
                    }
                }
            );
        }
        
        return videosLiveData;
    }
    
}
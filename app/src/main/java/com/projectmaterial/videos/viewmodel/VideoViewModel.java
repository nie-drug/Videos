package com.projectmaterial.videos.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.projectmaterial.videos.VideoApplication;
import com.projectmaterial.videos.utils.Video;
import java.util.List;

public class VideoViewModel extends AndroidViewModel {
    private VideoApplication videoApplication;
    
    public VideoViewModel(@NonNull Application application) {
        super(application);
        videoApplication = (VideoApplication) application;
    }
    
    public LiveData<List<Video>> getVideoLiveData() {
        return videoApplication.getVideoLiveData();
    }
}
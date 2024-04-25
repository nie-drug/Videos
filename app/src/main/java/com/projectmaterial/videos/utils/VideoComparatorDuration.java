package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Comparator;

public class VideoComparatorDuration implements Comparator<Video> {
    
    public VideoComparatorDuration(Context context) {
        
    }
    
    @Override
    public int compare(Video video1, Video video2) {
        long duration1 = video1.getDuration();
        long duration2 = video2.getDuration();
        return Long.compare(duration1, duration2);
    }
}
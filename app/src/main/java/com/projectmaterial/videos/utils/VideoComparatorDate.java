package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Comparator;

public class VideoComparatorDate implements Comparator<Video> {
    private VideoUtils mVideoUtils;
    
    public VideoComparatorDate(Context context) {
        mVideoUtils = new VideoUtils(context);
    }
    
    @Override
    public int compare(Video video1, Video video2) {
        long date1 = mVideoUtils.getDate(video1);
        long date2 = mVideoUtils.getDate(video2);
        return Long.compare(date1, date2);
    }
}
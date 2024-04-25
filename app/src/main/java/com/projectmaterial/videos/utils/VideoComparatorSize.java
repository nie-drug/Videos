package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Comparator;

public class VideoComparatorSize implements Comparator<Video> {
    private VideoUtils mVideoUtils;
    
    public VideoComparatorSize(Context context) {
        mVideoUtils = new VideoUtils(context);
    }
    
    @Override
    public int compare(Video video1, Video video2) {
        long size1 = mVideoUtils.getSize(video1);
        long size2 = mVideoUtils.getSize(video2);
        return Long.compare(size1, size2);
    }
}
package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Comparator;

public class VideoComparatorTitle implements Comparator<Video> {
    private VideoUtils mVideoUtils;
    
    public VideoComparatorTitle(Context context) {
        mVideoUtils = new VideoUtils(context);
    }
    
    @Override
    public int compare(Video video1, Video video2) {
        String name1 = mVideoUtils.getFormattedNameWithoutSquareBrackets(video1);
        String name2 = mVideoUtils.getFormattedNameWithoutSquareBrackets(video2);
        return name1.compareToIgnoreCase(name2);
    }
}
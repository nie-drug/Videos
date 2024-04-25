package com.projectmaterial.videos.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import java.io.File;

public class Video {
    private String mVideoData;
    private boolean isFavorite;
    private long mDuration;
    private static final String FAVORITE_KEY_PREFIX = "favorite_";
    
    public Video(String videoData, long duration) {
        mVideoData = videoData;
        mDuration = duration;
        isFavorite = false;
    }
    
    public String getData() {
        return mVideoData;
    }
    
    public long getDuration() {
        return mDuration;
    }
    
    public boolean isFavorite() {
        return isFavorite;
    }
    
    private String getName() {
        File videoFile = new File(mVideoData);
        String videoFileName = videoFile.getName();
        int dotIndex = videoFileName.lastIndexOf('.');
        return videoFileName.substring(0, dotIndex);
    }
    ;
    public boolean getFavoriteState(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return isFavorite = sharedPreferences.getBoolean(FAVORITE_KEY_PREFIX + getName(), false);
    }
    
    public void setFavoriteState(Context context, boolean isFavorite) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(FAVORITE_KEY_PREFIX + getName(), isFavorite).apply();
    }
    
    public void setFavorite(boolean favoriteState) {
        isFavorite = favoriteState;
    }
}
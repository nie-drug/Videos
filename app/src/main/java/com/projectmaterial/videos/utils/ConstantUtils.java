package com.projectmaterial.videos.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConstantUtils {
    public static final int FRAME_DURATION_MICROSECONDS = 85000000;
    public static final int IMAGE_DIMENSION_DP = 175;
    public static final String JPEG_FILE_EXTENSION = ".jpg";
    private static final String PREF_THUMBNAILS_DIRECTORY_PATH = "thumbnails_directory_path";
    public static String THUMBNAILS_DIRECTORY_PATH = "";

    public static void setThumbnailsDirectoryPath(Context context, String path) {
        THUMBNAILS_DIRECTORY_PATH = path;
        SharedPreferences preferences = context.getSharedPreferences("shared_prefs_thumbnail_path", Context.MODE_PRIVATE);
        preferences.edit().putString(PREF_THUMBNAILS_DIRECTORY_PATH, path).apply();
    }

    public static String getThumbnailsDirectoryPath(Context context) {
        if (THUMBNAILS_DIRECTORY_PATH.isEmpty()) {
            SharedPreferences preferences = context.getSharedPreferences("shared_prefs_thumbnail_path", Context.MODE_PRIVATE);
            THUMBNAILS_DIRECTORY_PATH = preferences.getString(PREF_THUMBNAILS_DIRECTORY_PATH, "");
        }
        return THUMBNAILS_DIRECTORY_PATH;
    }
}
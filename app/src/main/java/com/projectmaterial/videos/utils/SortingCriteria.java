package com.projectmaterial.videos.utils;

public class SortingCriteria {
    
    /**
     * Enum representing different sorting criteria for folders.
     */
    public enum Directory {
        // Sort by folder name.
        DIRECTORY_NAME,
        // Sort by video count in the folder.
        VIDEO_COUNT
    }

    /**
     * Enum representing different sorting criteria for videos.
     */
    public enum Video {
        // Sort by video name.
        VIDEO_NAME,
        // Sort by video duration.
        VIDEO_DURATION,
        // Sort by video date.
        VIDEO_DATE,
        // Sort by video size.
        VIDEO_SIZE
    }
}
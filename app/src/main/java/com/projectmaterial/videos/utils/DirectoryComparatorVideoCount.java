package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Comparator;
import java.util.Map;

public class DirectoryComparatorVideoCount implements Comparator<String> {
    private Map<String, Integer> mVideoCountMap;
    
    public DirectoryComparatorVideoCount(Context context, Map<String, Integer> videoCountMap) {
        mVideoCountMap = videoCountMap;
    }
    
    /**
     * Comparator for sorting directories by their associated video counts.
     */
    @Override
    public int compare(String directory1, String directory2) {
        // Retrieve the video counts for each directory from the map
        Integer count1 = mVideoCountMap.get(directory1);
        Integer count2 = mVideoCountMap.get(directory2);
    
        // If the video count is null, assign it as 0
        if (count1 == null) {
            count1 = 0;
        }
        if (count2 == null) {
            count2 = 0;
        }
    
        // Compare the video counts of the directories
        return count1.compareTo(count2);
    }
}
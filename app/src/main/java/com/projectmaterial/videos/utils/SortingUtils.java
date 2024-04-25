package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SortingUtils {
    
    /**
     * Sorts the list of videos based on the specified sorting criteria and order.
     * @param videoList The list of videos to be sorted.
     * @param sortingCriteria The criteria based on which videos should be sorted.
     * @param sortingOrder The order in which videos should be sorted.
     * @param context The context needed for video comparison.
     */
    public static void sortVideoList(List<Video> videoList, SortingCriteria.Video sortingCriteria, SortingOrder sortingOrder, Context context) {
        // Sorting based on the specified criteria
        switch (sortingCriteria) {
            case VIDEO_NAME:
                // Sorting videos by name
                Collections.sort(videoList, new VideoComparatorTitle(context));
                break;
            case VIDEO_DURATION:
                // Sorting videos by duration
                Collections.sort(videoList, new VideoComparatorDuration(context));
                break;
            case VIDEO_DATE:
                // Sorting videos by date
                Collections.sort(videoList, new VideoComparatorDate(context));
                break;
            case VIDEO_SIZE:
                // Sorting videos by size
                Collections.sort(videoList, new VideoComparatorSize(context));
                break;
        }
        
        // Reversing the list if sorting order is descending
        if (sortingOrder == SortingOrder.DESCENDING) {
            Collections.reverse(videoList);
        }
    }
    
    /**
     * Sorts the list of directories based on the specified sorting criteria and order.
     * @param directoryList The list of directories to be sorted.
     * @param sortingCriteria The criteria based on which directories should be sorted.
     * @param sortingOrder The order in which directories should be sorted.
     * @param context The context needed for directory comparison.
     * @param videoCountMap A map containing the video count for each directory.
     */
    public static void sortDirectoryList(List<String> directoryList, SortingCriteria.Directory sortingCriteria, SortingOrder sortingOrder, Context context, Map<String, Integer> videoCountMap) {
        // Sorting based on the specified criteria
        switch (sortingCriteria) {
            case DIRECTORY_NAME:
                // Sorting directories by name
                Collections.sort(directoryList, new DirectoryComparatorName(context));
                break;
            case VIDEO_COUNT:
                // Sorting directories by video count
                Collections.sort(directoryList, new DirectoryComparatorVideoCount(context, videoCountMap));
                break;
        }
        
        // Reversing the list if sorting order is descending
        if (sortingOrder == SortingOrder.DESCENDING) {
            Collections.reverse(directoryList);
        }
    }
}
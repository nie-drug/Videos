package com.projectmaterial.videos.utils;

import android.content.Context;
import java.util.Comparator;

public class DirectoryComparatorName implements Comparator<String> {
    private DirectoryUtils mDirectoryUtils;
    
    public DirectoryComparatorName(Context context) {
        mDirectoryUtils = new DirectoryUtils(context);
    }
    
    /**
     * Comparator for sorting directories by their formatted names.
     */
    @Override
    public int compare(String directory1, String directory2) {
        // Get the formatted name of each directory for comparison, ignoring case sensitivity
        String formattedName1 = mDirectoryUtils.getFormattedName(directory1);
        String formattedName2 = mDirectoryUtils.getFormattedName(directory2);
    
        // Compare the formatted names of the directories
        return formattedName1.compareToIgnoreCase(formattedName2);
    }
}
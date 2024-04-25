package com.projectmaterial.videos.utils;

import android.content.Context;
import android.os.Environment;
import com.projectmaterial.videos.R;
import java.io.File;

public class DirectoryUtils {
    private Context mContext;
    private VideoUtils mVideoUtils;
    
    public DirectoryUtils(Context context) {
        mContext = context;
        mVideoUtils = new VideoUtils(context);
    }
    
    /**
     * Retrieves the formatted name of a directory.
     * @param directory The directory path.
     * @return The formatted name of the directory.
     */
    public String getFormattedName(String directory) {
        // Check if the directory is the external storage directory
        if (directory.equals(Environment.getExternalStorageDirectory().getPath())) {
            // Return the localized string for the internal storage directory
            return mContext.getString(R.string.directory_internal_storage);
        } else {
            // Extract the name of the directory from its path
            return directory.substring(directory.lastIndexOf(File.separator) + 1);
        }
    }
    
    public String getFormattedPath(String directory) {
        // Find the index of the last separator
        int lastSeparatorIndex = directory.lastIndexOf(File.separator);
        
        // Check if there is a separator in the path
        if (lastSeparatorIndex != -1) {
            // Remove the last folder name and return the modified path
            return directory.substring(0, lastSeparatorIndex) + "/";
        } else {
            // If no separator is found, return the directory as is
            return directory + "/";
        }
    }
    
    /**
     * Retrieves the directory path from video data.
     * @param videoData The video data containing the path.
     * @return The directory path extracted from the video data.
     */
    public String retrieveDirectoryPathFromVideoData(String videoData) {
        // Check if videoData is null
        if (videoData == null) {
            return null;
        } else {
            // Extract the directory path from videoData
            String directoryPath = videoData.substring(0, videoData.lastIndexOf(File.separator));
            
            // Convert directory path to lowercase for case-insensitive comparison
            String directoryPathWithLowerCase = directoryPath.toLowerCase();
            
            // Create a File object for the directory
            File directory = new File(directoryPathWithLowerCase);
            
            // Check if the directory exists
            if (directory.exists()) {
                return directoryPath;
            }
            
            // If the directory doesn't exist, check its parent directory for similar named directories
            File parentDirectory = directory.getParentFile();
            
            // Check if the parent directory exists
            if (parentDirectory != null) {
                // Get the list of files in the parent directory
                File[] videoFiles = parentDirectory.listFiles();
                
                // Check if the list of files is not null
                if (videoFiles != null) {
                    // Iterate through the files in the parent directory
                    for (File videoFile : videoFiles) {
                        // Check if the file is a directory and its name matches the directory name we're looking for
                        if (videoFile.isDirectory() && videoFile.getName().equalsIgnoreCase(directory.getName())) {
                            // Return the absolute path of the matching directory
                            return videoFile.getAbsolutePath();
                        }
                    }
                }
            }
            // If no matching directory is found, return the original directory path
            return directoryPath;
        }
    }
}
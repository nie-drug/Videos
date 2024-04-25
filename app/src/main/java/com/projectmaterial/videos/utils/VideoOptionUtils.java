package com.projectmaterial.videos.utils;

import android.content.Context;
import android.view.View;
import com.projectmaterial.videos.R;
import com.projectmaterial.videos.common.CacheManager;
import java.io.File;

public class VideoOptionUtils {
    private CacheManager mCacheManager;
    private Context mContext;
    private VideoUtils mVideoUtils;
    
    public VideoOptionUtils(Context context) {
        mCacheManager = new CacheManager(context);
        mContext = context;
        mVideoUtils = new VideoUtils(context);
    }
    
    /**
     * Deletes the specified video file and its corresponding thumbnail,
     * and shows appropriate messages to the user.
     * This method checks if the video file exists, attempts to delete it,
     * and displays a snackbar message indicating success or failure of the deletion operation.
     * @param fragment The fragment or view context where the snackbar should be displayed.
     * @param video The Video object representing the video to be deleted.
     */
    public void deleteVideo(View fragment, Video video) {
        // Get video data and thumbnail path
        String videoData = video.getData();
        String videoName = mVideoUtils.getFormattedNameWithoutExtension(video);
        String thumbnailPath = ConstantUtils.getThumbnailsDirectoryPath(mContext) + videoName + ConstantUtils.JPEG_FILE_EXTENSION;
        
        String messageDoesNotExist = mContext.getString(R.string.snackbar_message_delete_result_error_does_not_exist);
        String messageError = mContext.getString(R.string.snackbar_message_delete_result_error);
        String messageSuccess = mContext.getString(R.string.snackbar_message_delete_result_success, videoName);
        
        File videoFile = new File(videoData);
        File thumbnailFile = new File(thumbnailPath);
        
        if (videoFile.exists()) {
            boolean deleted = videoFile.delete();
            if (deleted) {
                mCacheManager.clearCacheForKey(videoData);
                if (thumbnailFile.exists()) {
                    thumbnailFile.delete();
                }
                mVideoUtils.showSnackbar(fragment, messageSuccess);
            } else {
                mVideoUtils.showSnackbar(fragment, messageError);
            }
        } else {
            mVideoUtils.showSnackbar(fragment, messageDoesNotExist);
        }
    }
}

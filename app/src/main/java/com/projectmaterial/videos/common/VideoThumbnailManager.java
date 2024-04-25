package com.projectmaterial.videos.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.projectmaterial.videos.utils.ConstantUtils;
import com.projectmaterial.videos.utils.Video;
import com.projectmaterial.videos.utils.VideoUtils;
import java.io.File;

public class VideoThumbnailManager {
    private CacheManager mCacheManager;
    private Context mContext;
    private VideoUtils mVideoUtils;
    
    public VideoThumbnailManager(Context context) {
        mCacheManager = new CacheManager(context);
        mContext = context;
        mVideoUtils = new VideoUtils(context);
    }
    
    public void getThumbnailBitmap(ImageView thumbnailImageView, Video video) {
        String videoData = video.getData();
        Bitmap cachedThumbnail = mCacheManager.getBitmap(videoData);
        if (cachedThumbnail != null && mCacheManager.isCached(videoData, cachedThumbnail)) {
            thumbnailImageView.setImageBitmap(cachedThumbnail);
        } else {
            String thumbnailName = mVideoUtils.getFormattedNameWithoutExtension(video);
            String thumbnailPath = ConstantUtils.getThumbnailsDirectoryPath(mContext) + thumbnailName + ConstantUtils.JPEG_FILE_EXTENSION;
            File thumbnailFile = new File(thumbnailPath);
            if (thumbnailFile.exists()) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                requestOptions.override(ConstantUtils.IMAGE_DIMENSION_DP, ConstantUtils.IMAGE_DIMENSION_DP);
                Glide.with(mContext)
                    .asBitmap()
                    .load(Uri.fromFile(thumbnailFile))
                    .apply(requestOptions)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            thumbnailImageView.setImageBitmap(resource);
                            mCacheManager.putBitmap(videoData, resource, videoData);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            
                        }
                    });
            } else {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.frame(ConstantUtils.FRAME_DURATION_MICROSECONDS);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                requestOptions.override(ConstantUtils.IMAGE_DIMENSION_DP, ConstantUtils.IMAGE_DIMENSION_DP);
                Glide.with(mContext)
                    .asBitmap()
                    .load(Uri.fromFile(new File(videoData)))
                    .apply(requestOptions)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            thumbnailImageView.setImageBitmap(resource);
                            mCacheManager.putBitmap(videoData, resource, videoData);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            
                        }
                    });
            }
        }
    }
}
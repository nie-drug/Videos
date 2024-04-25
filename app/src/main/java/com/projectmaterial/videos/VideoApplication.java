package com.projectmaterial.videos;

import android.app.Application;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.projectmaterial.videos.utils.Video;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoApplication extends Application {
    private ContentResolver mContentResolver;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    private Handler mHandler;
    private MutableLiveData<List<Video>> mVideoLiveData;
    private static final Uri VIDEO_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    private static final String[] PROJECTION = { MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION };
    private static final String SELECTION = null;
    private static final String[] SELECTION_ARGS = null;
    private static final String SORT_ORDER = null;
    private static final int VIDEO_CHANGED = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        mContentResolver = getContentResolver();
        mHandler = new VideoListChangeHandler(Looper.getMainLooper());
        mVideoLiveData = new MutableLiveData<>();
        initVideoList();
        registerVideoContentObserver();
    }

    public void initVideoList() {
        mExecutorService.execute(() -> {
            List<Video> videoList = fetchVideoListFromMediaStore();
            updateVideoList(videoList);
        });
    }

    public LiveData<List<Video>> getVideoLiveData() {
        return mVideoLiveData;
    }

    private void registerVideoContentObserver() {
        mContentResolver.registerContentObserver(
                VIDEO_URI,
                true,
                new VideoContentObserver(mHandler)
        );
    }

    private List<Video> fetchVideoListFromMediaStore() {
        List<Video> videoList = new ArrayList<>();
        try (Cursor cursor = mContentResolver.query(
                VIDEO_URI,
                PROJECTION,
                SELECTION,
                SELECTION_ARGS,
                SORT_ORDER)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)) / 1000;
                    Video video = new Video(data, duration);
                    videoList.add(video);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoList;
    }

    private void updateVideoList(List<Video> videoList) {
        mHandler.sendMessage(Message.obtain(mHandler, VIDEO_CHANGED, videoList));
    }
    
    private class VideoListChangeHandler extends Handler {
        public VideoListChangeHandler(@NonNull Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(@NonNull Message message) {
            super.handleMessage(message);
            if (message.what == VIDEO_CHANGED && message.obj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Video> videoList = (List<Video>) message.obj;
                mVideoLiveData.postValue(videoList);
            }
        }
    }
    
    private void fetchAndUpdateVideoList() {
        mExecutorService.execute(() -> {
            List<Video> videoList = fetchVideoListFromMediaStore();
            updateVideoList(videoList);
        });
    }
    
    private class VideoContentObserver extends ContentObserver {
        public VideoContentObserver(Handler handler) {
            super(handler);
        }
        
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            fetchAndUpdateVideoList();
        }
    }
}
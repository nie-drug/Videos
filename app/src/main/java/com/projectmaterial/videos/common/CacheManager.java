package com.projectmaterial.videos.common;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.collection.LruCache;
import java.util.HashMap;
import java.util.Map;

public class CacheManager {
    private static CacheManager instance;
    private LruCache<String, Bitmap> mThumbnailCache;
    private Map<String, String> mThumbnailKeyMap;

    public CacheManager(Context context) {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mThumbnailCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getAllocationByteCount() / 1024;
            }
        };
        mThumbnailKeyMap = new HashMap<>();
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public void clearCacheForKey(String key) {
        mThumbnailCache.remove(key);
        mThumbnailKeyMap.remove(key);
    }

    public void putBitmap(String key, Bitmap bitmap, String videoData) {
        mThumbnailCache.put(key, bitmap);
        mThumbnailKeyMap.put(key, videoData);
    }

    public Bitmap getBitmap(String key) {
        Bitmap bitmap = mThumbnailCache.get(key);
        if (bitmap != null && !bitmap.isRecycled()) {
            return bitmap;
        }
        return null;
    }
  
    public boolean isCached(String key, Bitmap bitmap) {
        String cachedVideoData = mThumbnailKeyMap.get(key);
        Bitmap cachedBitmap = mThumbnailCache.get(key);
        return cachedVideoData != null && cachedBitmap != null && cachedBitmap.equals(bitmap);
    }
  
    public void clearCache() {
        mThumbnailCache.evictAll();
        mThumbnailKeyMap.clear();
    }
}